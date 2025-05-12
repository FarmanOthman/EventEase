package services;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
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
   * Import data from Excel file for the specified entity type
   * 
   * @param filePath   the path to the Excel file
   * @param entityType the type of entity to import (e.g., "event", "ticket")
   * @return Import status with success flag and message
   */
  public ImportResult importFromExcel(String filePath, String entityType) {
    if (!new File(filePath).exists()) {
      return new ImportResult(false, "File not found: " + filePath);
    }

    try (FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = WorkbookFactory.create(fis)) {

      Sheet sheet = workbook.getSheetAt(0);

      // Get headers
      Row headerRow = sheet.getRow(0);
      if (headerRow == null) {
        return new ImportResult(false, "Empty file or missing header row");
      }

      // Read header columns
      Map<Integer, String> columnMap = new HashMap<>();
      for (int i = 0; i < headerRow.getLastCellNum(); i++) {
        Cell cell = headerRow.getCell(i);
        if (cell != null) {
          columnMap.put(i, cell.getStringCellValue());
        }
      }

      // Determine which import processor to use based on entity type
      switch (entityType.toLowerCase()) {
        case "event":
          return importEvents(sheet, columnMap);
        case "ticket":
          return importTickets(sheet, columnMap);
        default:
          return new ImportResult(false, "Unsupported entity type: " + entityType);
      }
    } catch (IOException e) {
      return new ImportResult(false, "Error reading file: " + e.getMessage());
    }
  }

  private ImportResult importEvents(Sheet sheet, Map<Integer, String> columnMap) {
    List<Map<String, Object>> events = new ArrayList<>();

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row == null)
        continue;

      Map<String, Object> event = new HashMap<>();
      for (int j = 0; j < columnMap.size(); j++) {
        Cell cell = row.getCell(j);
        if (cell != null) {
          String columnName = columnMap.get(j);
          event.put(columnName, getCellValue(cell));
        }
      }

      // Validate event data
      if (!isValidEventData(event)) {
        return new ImportResult(false, "Invalid event data at row " + (i + 1)
            + ". Required fields: event_name, event_date, team_a, team_b, category, event_type");
      }

      events.add(event);
    }

    // Import events to database
    boolean success = saveEventsToDatabase(events);
    return success
        ? new ImportResult(true, events.size() + " events imported successfully")
        : new ImportResult(false, "Failed to import events to database");
  }

  private boolean isValidEventData(Map<String, Object> event) {
    // Required fields must be present and have valid values according to database
    // schema
    return event.containsKey("event_name") && event.get("event_name") != null
        && event.containsKey("event_date") && event.get("event_date") != null
        && event.containsKey("team_a") && event.get("team_a") != null
        && event.containsKey("team_b") && event.get("team_b") != null
        && event.containsKey("category") && isValidCategory((String) event.get("category"))
        && event.containsKey("event_type") && isValidEventType((String) event.get("event_type"));
  }

  private boolean isValidCategory(String category) {
    if (category == null)
      return false;
    return category.equals("Regular") || category.equals("VIP");
  }

  private boolean isValidEventType(String eventType) {
    if (eventType == null)
      return false;
    return eventType.equals("Event") || eventType.equals("Match");
  }

  private boolean saveEventsToDatabase(List<Map<String, Object>> events) {
    // SQL matched to the actual database schema
    String sql = "INSERT INTO Event (event_name, event_date, event_description, category, event_type, team_a, team_b) "
        +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      conn.setAutoCommit(false);

      for (Map<String, Object> event : events) {
        pstmt.setString(1, (String) event.get("event_name"));
        pstmt.setString(2, (String) event.get("event_date"));

        // Event description
        if (event.get("event_description") != null) {
          pstmt.setString(3, (String) event.get("event_description"));
        } else {
          pstmt.setNull(3, Types.VARCHAR);
        }

        pstmt.setString(4, (String) event.get("category")); // Category (Regular or VIP)
        pstmt.setString(5, (String) event.get("event_type")); // Event type (Event or Match)
        pstmt.setString(6, (String) event.get("team_a"));
        pstmt.setString(7, (String) event.get("team_b"));

        pstmt.addBatch();
      }

      pstmt.executeBatch();
      conn.commit();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private ImportResult importTickets(Sheet sheet, Map<Integer, String> columnMap) {
    List<Map<String, Object>> tickets = new ArrayList<>();

    for (int i = 1; i <= sheet.getLastRowNum(); i++) {
      Row row = sheet.getRow(i);
      if (row == null)
        continue;

      Map<String, Object> ticket = new HashMap<>();
      for (int j = 0; j < columnMap.size(); j++) {
        Cell cell = row.getCell(j);
        if (cell != null) {
          String columnName = columnMap.get(j);
          ticket.put(columnName, getCellValue(cell));
        }
      }

      // Validate ticket data
      if (!isValidTicketData(ticket)) {
        return new ImportResult(false,
            "Invalid ticket data at row " + (i + 1) + ". Required fields: event_id, ticket_type, ticket_date, price");
      }

      tickets.add(ticket);
    }

    // Import tickets to database
    boolean success = saveTicketsToDatabase(tickets);
    return success
        ? new ImportResult(true, tickets.size() + " tickets imported successfully")
        : new ImportResult(false, "Failed to import tickets to database");
  }

  private boolean isValidTicketData(Map<String, Object> ticket) {
    // Required fields must be present and have valid values according to database
    // schema
    boolean basic = ticket.containsKey("event_id") && ticket.get("event_id") != null
        && ticket.containsKey("ticket_type") && isValidTicketType((String) ticket.get("ticket_type"))
        && ticket.containsKey("ticket_date") && ticket.get("ticket_date") != null
        && ticket.containsKey("price") && isPositiveNumber(ticket.get("price"));

    // Ticket status if provided must be valid
    if (ticket.containsKey("ticket_status") && ticket.get("ticket_status") != null) {
      return basic && isValidTicketStatus((String) ticket.get("ticket_status"));
    }

    return basic;
  }

  private boolean isValidTicketType(String ticketType) {
    if (ticketType == null)
      return false;
    return ticketType.equals("Regular") || ticketType.equals("VIP");
  }

  private boolean isValidTicketStatus(String ticketStatus) {
    if (ticketStatus == null)
      return false;
    return ticketStatus.equals("Available") || ticketStatus.equals("Sold") || ticketStatus.equals("Canceled");
  }

  private boolean isPositiveNumber(Object value) {
    if (value == null)
      return false;
    try {
      if (value instanceof Number) {
        return ((Number) value).doubleValue() > 0;
      } else {
        return Double.parseDouble(value.toString()) > 0;
      }
    } catch (NumberFormatException e) {
      return false;
    }
  }

  private boolean saveTicketsToDatabase(List<Map<String, Object>> tickets) {
    // SQL matched to the actual database schema
    String sql = "INSERT INTO Ticket (event_id, ticket_type, ticket_date, ticket_status, price) " +
        "VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

      conn.setAutoCommit(false);

      for (Map<String, Object> ticket : tickets) {
        // Event ID
        Object eventIdObj = ticket.get("event_id");
        if (eventIdObj instanceof Number) {
          pstmt.setInt(1, ((Number) eventIdObj).intValue());
        } else {
          pstmt.setInt(1, Integer.parseInt(eventIdObj.toString()));
        }

        pstmt.setString(2, (String) ticket.get("ticket_type"));
        pstmt.setString(3, (String) ticket.get("ticket_date"));

        // Ticket status (default to 'Available' if not provided)
        if (ticket.containsKey("ticket_status") && ticket.get("ticket_status") != null) {
          pstmt.setString(4, (String) ticket.get("ticket_status"));
        } else {
          pstmt.setString(4, "Available");
        }

        // Price
        Object priceObj = ticket.get("price");
        if (priceObj instanceof Number) {
          pstmt.setDouble(5, ((Number) priceObj).doubleValue());
        } else {
          pstmt.setDouble(5, Double.parseDouble(priceObj.toString()));
        }

        pstmt.addBatch();
      }

      pstmt.executeBatch();
      conn.commit();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  private Object getCellValue(Cell cell) {
    switch (cell.getCellType()) {
      case STRING:
        return cell.getStringCellValue();
      case NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
          return cell.getDateCellValue();
        }
        return cell.getNumericCellValue();
      case BOOLEAN:
        return cell.getBooleanCellValue();
      default:
        return null;
    }
  }

  /**
   * Export data to Excel file
   * 
   * @param data        Data to export (list of maps with row data)
   * @param filePath    Output file path
   * @param sheetName   Name of the Excel sheet
   * @param columnNames Column headers
   * @return True if export was successful
   */
  public boolean exportToExcel(List<Map<String, Object>> data, String filePath,
      String sheetName, String[] columnNames) {
    return excelExportService.exportToExcel(data, filePath, sheetName, columnNames);
  }

  /**
   * Export data to PDF file
   * 
   * @param data        Data to export
   * @param filePath    Output file path
   * @param title       Title of the PDF document
   * @param columnNames Column headers for the PDF table
   * @return True if export was successful
   */
  public boolean exportToPDF(List<Map<String, Object>> data, String filePath, String title, String[] columnNames) {
    return pdfExportService.exportToPDF(data, filePath, title, columnNames);
  }

  /**
   * Create a database backup
   * 
   * @param backupName Name for the backup file
   * @return Backup result with success flag and message
   */  public BackupResult createBackup(String backupName) {
    if (backupName == null || backupName.trim().isEmpty()) {
      backupName = "backup_" + System.currentTimeMillis();
    }

    String backupPath = BACKUP_DIRECTORY + backupName + ".db";
    try {
      // Make sure the backup directory exists
      File backupDir = new File(BACKUP_DIRECTORY);
      if (!backupDir.exists()) {
        backupDir.mkdirs();
      }
      
      // Use BackupManager to create a proper SQLite database file
      BackupManager.backupDatabase(backupPath);
      
      // Verify the backup was created and is a valid SQLite database
      File backupFile = new File(backupPath);
      if (backupFile.exists() && isSQLiteDatabase(backupFile)) {
        return new BackupResult(true, "Backup created successfully at " + backupPath);
      } else {
        return new BackupResult(false, "Backup creation failed: Backup file is not a valid SQLite database");
      }
    } catch (Exception e) {
      return new BackupResult(false, "Backup failed: " + e.getMessage());
    }
  }

  /**
   * List all available backups
   * 
   * @return List of backup files with metadata
   */
  public List<BackupInfo> listBackups() {
    File backupDir = new File(BACKUP_DIRECTORY);
    File[] files = backupDir.listFiles((dir, name) -> name.endsWith(".db"));

    List<BackupInfo> backups = new ArrayList<>();
    if (files != null) {
      for (File file : files) {
        backups.add(new BackupInfo(
            file.getName(),
            new Date(file.lastModified()),
            file.length(),
            file.getAbsolutePath()));
      }
    }

    return backups;
  }

  private List<Map<String, Object>> fetchDataForExport(String dataType, Date fromDate, Date toDate) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      String fromDateStr = sdf.format(fromDate);
      String toDateStr = sdf.format(toDate);

      Map<String, Object> filters = new HashMap<>();
      String[] columns;
      String tableName;

      switch (dataType) {
        case "Events":
          tableName = "Event";
          filters.put("event_date >= ", fromDateStr);
          filters.put("event_date <= ", toDateStr);
          columns = new String[] {
              "event_id",
              "event_name",
              "event_date",
              "event_description",
              "team_a",
              "team_b",
              "category",
              "event_type"
          };
          break;

        case "Tickets":
          tableName = "Ticket";
          filters.put("ticket_date >= ", fromDateStr);
          filters.put("ticket_date <= ", toDateStr);
          columns = new String[] {
              "ticket_id",
              "event_id",
              "ticket_type",
              "ticket_date",
              "price",
              "ticket_status"
          };
          break;

        case "Sales Report":
          tableName = "Sales";
          filters.put("sale_date >= ", fromDateStr);
          filters.put("sale_date <= ", toDateStr);
          columns = new String[] {
              "sale_id",
              "sale_date",
              "tickets_sold",
              "revenue",
              "category"
          };
          break;

        default:
          System.err.println("Unknown data type: " + dataType);
          return new ArrayList<>();
      }

      List<Map<String, Object>> results = queryBuilder.selectWithFilters(tableName, filters, columns);
      System.out.println("Fetched " + results.size() + " records from " + tableName);

      // Map the database column names to display names
      List<Map<String, Object>> mappedResults = new ArrayList<>();
      for (Map<String, Object> result : results) {
        Map<String, Object> mappedResult = new HashMap<>();
        for (Map.Entry<String, Object> entry : result.entrySet()) {
          String displayName = getDisplayColumnName(entry.getKey());
          mappedResult.put(displayName.toLowerCase().replace(" ", "_"), entry.getValue());
        }
        mappedResults.add(mappedResult);
      }

      return mappedResults;

    } catch (Exception e) {
      System.err.println("Error fetching data for export: " + e.getMessage());
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
  private String getDisplayColumnName(String dbColumnName) {
    switch (dbColumnName) {
      case "event_id":
        return "ID";
      case "ticket_id":
        return "ID";
      case "sale_id":
        return "ID";
      case "event_date":
        return "Date";
      case "event_name":
        return "Event Name";
      case "event_description":
        return "Description";
      case "team_a":
        return "Team A";
      case "team_b":
        return "Team B";
      case "category":
        return "Category";
      case "event_type":
        return "Event Type";
      case "ticket_type":
        return "Ticket Type";
      case "ticket_date":
        return "Date";
      case "price":
        return "Price";
      case "ticket_status":
        return "Status";
      case "sale_date":
        return "Date";
      case "tickets_sold":
        return "Tickets Sold";
      case "revenue":
        return "Revenue";
      default:
        // Convert snake_case to Title Case
        return Arrays.stream(dbColumnName.split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
  }

  public List<Map<String, Object>> getExportData(String dataType, Date fromDate, Date toDate) {
    List<Map<String, Object>> data = fetchDataForExport(dataType, fromDate, toDate);

    // For Sales Report, we need to join with Event table to get team information
    if (dataType.equals("Sales Report") && !data.isEmpty()) {
      // Fetch event details for each sale
      for (Map<String, Object> sale : data) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("event_date", sale.get("date")); // Using mapped column name

        List<Map<String, Object>> eventDetails = queryBuilder.selectWithFilters(
            "Event",
            filters,
            new String[] { "event_name", "team_a", "team_b" });

        if (!eventDetails.isEmpty()) {
          // Map the column names before adding to sale
          Map<String, Object> mappedDetails = new HashMap<>();
          for (Map.Entry<String, Object> entry : eventDetails.get(0).entrySet()) {
            String displayName = getDisplayColumnName(entry.getKey());
            mappedDetails.put(displayName.toLowerCase().replace(" ", "_"), entry.getValue());
          }
          sale.putAll(mappedDetails);
        }
      }
    }

    return data;
  }

  /**
   * Class representing import operation result
   */
  public static class ImportResult {
    private final boolean success;
    private final String message;

    public ImportResult(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
    }
  }

  /**
   * Class representing backup operation result
   */
  public static class BackupResult {
    private final boolean success;
    private final String message;

    public BackupResult(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    public boolean isSuccess() {
      return success;
    }

    public String getMessage() {
      return message;
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

      // Make sure it's a valid SQLite database file by checking for SQLite header
      if (!isSQLiteDatabase(backupFile)) {
        return new BackupResult(false, "The selected file is not a valid SQLite database: " + backupPath);
      }
      
      // Instead of trying to copy the backup file to the current database location,
      // we'll update the configuration to point directly to the backup file
      boolean configUpdated = updateDatabaseConfig(backupPath);
      
      if (configUpdated) {
        // Test if we can connect to the backup database
        try (Connection testConn = DriverManager.getConnection("jdbc:sqlite:" + backupPath)) {
          if (testConn == null || testConn.isClosed()) {
            return new BackupResult(false, "The backup database is not valid or cannot be connected to.");
          }
          
          // If we're here, the connection was successful
          System.out.println("Successfully connected to the backup database at: " + backupPath);
          String backupFilename = new File(backupPath).getName();
          return new BackupResult(true, 
              "Database successfully restored. Now using backup: " + backupFilename);
        } catch (SQLException e) {
          // If there's an error connecting to the backup, revert the configuration
          // Revert to the original database URL
          updateDatabaseConfig(currentPath);
          return new BackupResult(false, "Error connecting to backup database: " + e.getMessage() + 
                                 ". Configuration has been reverted.");
        }
      } else {
        return new BackupResult(false, "Failed to update configuration to use backup database.");
      }
    } catch (Exception e) {
      return new BackupResult(false, "Unexpected error during database restore: " + e.getMessage());
    }
  }

  /**
   * Check if a file is a valid SQLite database by looking for the SQLite file signature
   * 
   * @param file The file to check
   * @return true if the file appears to be a valid SQLite database
   */
  private boolean isSQLiteDatabase(File file) {
    try (FileInputStream fis = new FileInputStream(file)) {
      byte[] header = new byte[16];
      int bytesRead = fis.read(header);
      
      // SQLite database files start with the string "SQLite format 3\0"
      if (bytesRead >= 16) {
        String headerStr = new String(header);
        return headerStr.startsWith("SQLite format 3");
      }
      return false;
    } catch (IOException e) {
      System.err.println("Error checking SQLite header: " + e.getMessage());
      return false;
    }
  }

  /**
   * Updates the database configuration file to use a different database path
   * 
   * @param newDbPath The new database path to use
   * @return true if the configuration was successfully updated
   */  private boolean updateDatabaseConfig(String newDbPath) {
    // The path where config.properties is stored
    String configPath = "src/main/resources/config.properties";
    File configFile = new File(configPath);
    
    if (!configFile.exists()) {
      System.err.println("Config file not found at: " + configPath);
      return false;
    }
    
    try {
      // Convert Windows backslashes to forward slashes for consistency in property files
      String normalizedPath = newDbPath.replace('\\', '/');
      
      // If the path points to a backup file, format it as requested
      if (normalizedPath.contains("backups/")) {
        // Extract just the backup filename
        String backupFileName = new File(normalizedPath).getName();
        normalizedPath = "src/backups/" + backupFileName;
        System.out.println("Using backup-relative path format: " + normalizedPath);
      }
      
      String newDbUrl = "jdbc:sqlite:" + normalizedPath;
      // Read the existing properties
      java.util.Properties props = new java.util.Properties();
      try (java.io.FileInputStream in = new java.io.FileInputStream(configFile)) {
        props.load(in);
      }
      
      // Store the original URL for backup
      String originalUrl = props.getProperty("db.url");
      
      // Set the new URL
      props.setProperty("db.url", newDbUrl);
      
      // Write the updated properties back to the file
      try (java.io.FileOutputStream out = new java.io.FileOutputStream(configFile)) {
        props.store(out, "Updated automatically after database restore");
      }
      
      System.out.println("Database URL changed from: " + originalUrl + " to: " + newDbUrl);      
      
      // Also update the class-level database URL if possible
      try {
        // After updating config file, we need a more aggressive approach
        // to update the DB_URL field in memory
        
        // 1. Try to modify the final field using reflection
        java.lang.reflect.Field dbUrlField = Database.class.getDeclaredField("DB_URL");
        dbUrlField.setAccessible(true);
        
        // Different approaches for different Java versions
        try {
          // Java 9+ approach
          java.lang.reflect.Field modifiersField = java.lang.reflect.Field.class.getDeclaredField("modifiers");
          modifiersField.setAccessible(true);
          modifiersField.setInt(dbUrlField, dbUrlField.getModifiers() & ~java.lang.reflect.Modifier.FINAL);
        } catch (NoSuchFieldException e) {
          // Java 11+ approach - use VarHandle
          try {
            // For Java 11+
            Object unsafe = Class.forName("sun.misc.Unsafe").getDeclaredMethod("getUnsafe").invoke(null);
            long offset = (long) Class.forName("sun.misc.Unsafe")
                .getDeclaredMethod("staticFieldOffset", java.lang.reflect.Field.class)
                .invoke(unsafe, dbUrlField);
            Class.forName("sun.misc.Unsafe")
                .getDeclaredMethod("putObject", Object.class, long.class, Object.class)
                .invoke(unsafe, Class.forName("database.Database"), offset, newDbUrl);
          } catch (Exception ex) {
            // This is expected to fail in many cases due to security restrictions
            System.out.println("Could not use Unsafe to modify final field: " + ex.getMessage());
            System.out.println("Database URL change will take effect on application restart");
          }
        }
        
        // Actually set the field value
        dbUrlField.set(null, newDbUrl);
        System.out.println("Successfully updated Database.DB_URL field directly in memory");
        
        // Verify the change was applied
        String updatedDbUrl = (String)dbUrlField.get(null);
        if (updatedDbUrl.equals(newDbUrl)) {
          System.out.println("Verified that DB_URL field was updated to: " + updatedDbUrl);
        } else {
          System.out.println("DB_URL field could not be updated in memory. Current value: " + updatedDbUrl);
        }
      } catch (Exception e) {
        // This is not a failure as config file was updated successfully
        System.out.println("Could not update DB_URL field directly: " + e.getMessage());
        System.out.println("Database URL change will take effect on application restart");
      }
      
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
        // Additionally try to obtain an exclusive lock to truly verify no other process
        // has the file locked
        java.nio.channels.FileLock lock = raf.getChannel().tryLock();
        if (lock != null) {
          lock.release();
          return false; // File is not locked
        } else {
          return true; // Could not obtain lock, file is locked
        }
      } finally {
        if (raf != null) {
          try {
            raf.close();
          } catch (IOException e) {
            System.err.println("Error closing file during lock check: " + e.getMessage());
          }
        }
      }
    } catch (IOException e) {
      System.out.println("File appears to be locked: " + file.getPath() + ", error: " + e.getMessage());
      return true; // File is locked
    }
  }

  /**
   * Class representing backup file information
   */
  public static class BackupInfo {
    private final String name;
    private final Date creationDate;
    private final long size;
    private final String path;

    public BackupInfo(String name, Date creationDate, long size, String path) {
      this.name = name;
      this.creationDate = creationDate;
      this.size = size;
      this.path = path;
    }

    public String getName() {
      return name;
    }

    public Date getCreationDate() {
      return creationDate;
    }

    public long getSize() {
      return size;
    }

    public String getPath() {
      return path;
    }
  }
}