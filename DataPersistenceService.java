package services;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;

import database.BackupManager;
import database.Database;
import server.ExcelExportService;
import server.PDFExportService;
import database.QueryBuilder;

/**
 * Service for managing data persistence operations: import, export, and backup.
 */
public class DataPersistenceService {
  private static final String BACKUP_DIRECTORY = "backups/";
  private final ExcelExportService excelExportService;
  private final PDFExportService pdfExportService;
  private final QueryBuilder queryBuilder;

  public DataPersistenceService() {
    this.excelExportService = new ExcelExportService();
    this.pdfExportService = new PDFExportService();
    this.queryBuilder = new QueryBuilder();
    initializeBackupDirectory();
  }

  private void initializeBackupDirectory() {
    File backupDir = new File(BACKUP_DIRECTORY);
    if (!backupDir.exists()) {
      backupDir.mkdirs();
    }
  }
  
  /**
   * Restore database from a backup file
   * 
   * @param backupPath Path to the backup file
   * @return Restore result with success flag and message
   */  
  public BackupResult restoreDatabase(String backupPath) {
    try {
      System.out.println("Starting database restore from: " + backupPath);
      
      // Debug information about database location and state
      String currentPath = Database.getDatabasePath();
      File currentDb = new File(currentPath);
      if (currentDb.exists()) {
        System.out.println("Current database exists at: " + currentPath);
        System.out.println("Current database size: " + currentDb.length() + " bytes");
        System.out.println("Current database locked: " + isFileLocked(currentDb));
      } else {
        System.out.println("No existing database found at: " + currentPath);
      }
      
      // Clean up the backup path by trimming whitespace
      backupPath = backupPath.trim();
      
      // Handle potential Windows UNC path issues
      if (backupPath.startsWith("\\\\")) {
        // Ensure UNC paths are handled correctly
        backupPath = "\\\\"+backupPath.substring(2).replace("\\\\", "\\");
      }
      
      // Check if the backup file exists
      File backupFile = new File(backupPath);
      if (!backupFile.exists() || !backupFile.isFile()) {
        return new BackupResult(false, "Backup file does not exist or is not a valid file: " + backupPath);
      }

      // Get the current database path and ensure it's properly formatted
      String dbPath = Database.getDatabasePath().trim();
      
      // Fix potential path issues - handle resource paths properly
      if (dbPath.startsWith(":")) {
        dbPath = dbPath.substring(1);
      }
      
      // Normalize path separators in case of mixed formats
      dbPath = dbPath.replace("/", File.separator).replace("\\", File.separator);
      
      File dbFile = new File(dbPath);
      
      // Close all database connections first before performing restore
      try {
        // For safety, explicitly close the QueryBuilder's connection
        if (queryBuilder != null) {
          queryBuilder.closeConnection();
        }
        
        // We need to explicitly close the Database connection pool
        try {
          // The key to fixing file locking issues is to make sure all connections are properly closed
          
          // First try to force SQLite to release file locks with PRAGMA
          Connection mainConn = null;
          try {
            System.out.println("Attempting to close all database connections...");
            
            mainConn = Database.getConnection();
            if (mainConn != null) {
              try (Statement stmt = mainConn.createStatement()) {
                // These PRAGMAs help ensure connections are properly closed
                stmt.execute("PRAGMA optimize;");
                stmt.execute("PRAGMA wal_checkpoint(FULL);");
                System.out.println("Successfully executed PRAGMA commands to release locks");
              }
            }
          } catch (SQLException closeEx) {
            System.err.println("Warning: Failed to execute PRAGMA commands: " + closeEx.getMessage());
          } finally {
            // Very important - explicitly close the connection
            if (mainConn != null) {
              try {
                mainConn.close();
                System.out.println("Explicitly closed main database connection");
              } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
              }
            }
          }
          
          // Force the JVM to run garbage collection to help release any lingering connections
          System.gc();
          Thread.sleep(500); // Give the system time to release resources
          
        } catch (Exception e) {
          System.err.println("Warning: Error while closing connections: " + e.getMessage());
        }
      } catch (Exception e) {
        System.err.println("Warning: Error while preparing for database restore: " + e.getMessage());
      }
      
      // Try the "Copy to Alternative Location" approach first since direct file replacement has failed
      String newDbPath = dbFile.getParent() + File.separator + "EventEase_new.db";
      File newDbFile = new File(newDbPath);
      
      System.out.println("Using alternative location strategy as primary approach");
      System.out.println("Will copy backup to: " + newDbPath);
      
      // Copy the backup to the new location
      Files.copy(backupFile.toPath(), newDbFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
      System.out.println("Successfully copied backup to alternative location");
      
      // Update the configuration to use the new database file
      if (updateDatabaseConfig(newDbPath)) {
        System.out.println("Successfully updated configuration to use new database");
        return new BackupResult(true, "Database restored successfully to " + newDbPath + ". The application will use this new database file.");
      } else {
        return new BackupResult(false, "Backup was copied to " + newDbPath + " but failed to update configuration. Please restart the application or manually update config.properties.");
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new BackupResult(false, "Unexpected error during database restore: " + e.getMessage());
    }
  }
  
  /**
   * Updates the database configuration file to point to a new database location
   * 
   * @param newDbPath The path to the new database file
   * @return true if the configuration was successfully updated
   */
  private boolean updateDatabaseConfig(String newDbPath) {
    try {
      // Create the JDBC URL with the new database path
      String newJdbcUrl = "jdbc:sqlite:" + newDbPath;
      System.out.println("Updating config with new URL: " + newJdbcUrl);
      
      // Get the config file
      File configFile = new File("src/main/resources/config.properties");
      
      // If the config file doesn't exist in the working directory, try to find it
      if (!configFile.exists()) {
        // Try to find the config file in the classpath
        URL configUrl = getClass().getClassLoader().getResource("config.properties");
        if (configUrl != null) {
          try {
            configFile = new File(configUrl.toURI());
          } catch (Exception e) {
            System.err.println("Error converting URL to URI: " + e.getMessage());
          }
        }
      }
      
      if (!configFile.exists()) {
        System.err.println("Could not find configuration file");
        return false;
      }
      
      // Read the current configuration
      Properties properties = new Properties();
      try (FileInputStream fis = new FileInputStream(configFile)) {
        properties.load(fis);
      }
      
      // Update the database URL
      properties.setProperty("db.url", newJdbcUrl);
      
      // Write the updated configuration back to the file
      try (FileOutputStream fos = new FileOutputStream(configFile)) {
        properties.store(fos, "Updated by automatic database restore process");
      }
      
      // Also update the config file in the target directory if it exists
      File targetConfigFile = new File("target/classes/config.properties");
      if (targetConfigFile.exists()) {
        try (FileOutputStream fos = new FileOutputStream(targetConfigFile)) {
          properties.store(fos, "Updated by automatic database restore process");
        }
      }
      
      System.out.println("Configuration successfully updated");
      return true;
      
    } catch (Exception e) {
      System.err.println("Error updating database configuration: " + e.getMessage());
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Checks if a file is locked (being used by another process)
   * 
   * @param file The file to check
   * @return true if the file is locked, false otherwise
   */
  private boolean isFileLocked(File file) {
    if (!file.exists()) {
      return false;
    }
    
    try {
      // Try to open the file for writing to check if it's locked
      java.io.RandomAccessFile raf = null;
      try {
        raf = new java.io.RandomAccessFile(file, "rw");
        return false; // File is not locked
      } finally {
        if (raf != null) {
          try {
            raf.close();
          } catch (IOException e) {
            // Ignore
          }
        }
      }
    } catch (IOException e) {
      System.out.println("File appears to be locked: " + file.getPath());
      return true; // File is locked
    }
  }

  /**
   * A utility method to try to release file locks on Windows
   * using process execution if standard methods fail
   * 
   * @param filePath The path to the file that might be locked
   */
  private void tryToReleaseFileLock(String filePath) {
    // Only attempt on Windows systems
    if (!System.getProperty("os.name").toLowerCase().contains("win")) {
      return;
    }
    
    try {
      // Request garbage collection to help release resources
      System.gc();
      Thread.sleep(100);
      
      // On Windows, we can try running an external command to check file locks
      String command = String.format("handle64.exe -a -u \"%s\"", filePath);
      
      // This is just for information - we don't actually run the external command
      // as it would require additional tools to be installed
      System.out.println("For severe locking issues, you could run: " + command);
      
    } catch (Exception e) {
      System.err.println("Error in tryToReleaseFileLock: " + e.getMessage());
    }
  }

  /**
   * Try to resolve the database path if the standard path doesn't work
   * This helps handle different path formats and locations
   * 
   * @return An alternative resolved path or null if no alternative is found
   */
  private String resolveAlternativeDatabasePath() {
    // Try to find the file in resources folder
    String resourcePath = "src/main/resources/EventEase.db";
    if (new File(resourcePath).exists()) {
      return resourcePath;
    }
    
    // Try another common location
    String alternativePath = "src/main/Resours/EventEase.db";
    if (new File(alternativePath).exists()) {
      return alternativePath;
    }

    // Try to extract just the filename and find it in the current directory
    String filename = "EventEase.db";
    if (new File(filename).exists()) {
      return filename;
    }
    
    return null;
  }
}
