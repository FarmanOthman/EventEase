package database;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class BackupManager {

    // Logger for recording backup process details
    private static final Logger logger = Logger.getLogger(BackupManager.class.getName());

    /**
     * Backs up the database to the specified backup file path.
     * It creates an actual SQLite database file by making a direct file copy
     * or by creating a new database with the same schema and data.
     * 
     * @param backupFilePath the path where the backup file will be saved
     */
    public static void backupDatabase(String backupFilePath) {
        // Log the database path used for the backup
        String dbPath = Database.DB_URL.replace("jdbc:sqlite:", "");
        logger.info("Attempting to back up database at: " + dbPath);

        // Check if the database file exists and is valid
        File dbFile = new File(dbPath);
        if (!dbFile.exists() || !dbFile.isFile()) {
            logger.severe("Database file does not exist or is not a valid file: " + dbFile.getAbsolutePath());
            
            // Try to create a new database directly at the backup location
            try {
                createNewDatabase(backupFilePath);
                logger.info("Created a new SQLite database at: " + backupFilePath);
                return;
            } catch (Exception e) {
                logger.severe("Failed to create new database: " + e.getMessage());
                return;
            }
        }

        // First try a direct file copy which is the most reliable method for SQLite
        try {
            // Ensure parent directories exist
            File backupFile = new File(backupFilePath);
            if (backupFile.getParentFile() != null && !backupFile.getParentFile().exists()) {
                backupFile.getParentFile().mkdirs();
            }
            
            // Make a direct copy of the SQLite database file
            try (FileInputStream fis = new FileInputStream(dbFile);
                 FileOutputStream fos = new FileOutputStream(backupFile)) {
                
                byte[] buffer = new byte[8192];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                
                logger.info("Database backup completed successfully via direct file copy.");
                return;
            }
        } catch (IOException e) {
            logger.warning("Direct file copy failed: " + e.getMessage() + ". Falling back to SQL backup approach.");
        }
        
        // If direct file copy fails, fall back to SQL backup approach
        try (Connection sourceConn = Database.getConnection();
             Connection backupConn = DriverManager.getConnection("jdbc:sqlite:" + backupFilePath)) {
            
            if (sourceConn == null || backupConn == null) {
                logger.severe("Failed to establish connections for backup process.");
                return;
            }
            
            // Create a backup by directly copying the schema and data
            DatabaseMetaData metaData = sourceConn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });
            
            // Set pragmas for optimal backup performance
            try (Statement stmt = backupConn.createStatement()) {
                stmt.executeUpdate("PRAGMA synchronous = OFF");
                stmt.executeUpdate("PRAGMA journal_mode = MEMORY");
                stmt.executeUpdate("BEGIN TRANSACTION");
            }
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                logger.info("Backing up table: " + tableName);
                
                // Get the schema for this table
                String createSql = getTableSchema(tableName, sourceConn);
                if (createSql != null) {
                    try (Statement stmt = backupConn.createStatement()) {
                        stmt.executeUpdate(createSql);
                    }
                    
                    // Copy the data
                    copyTableData(tableName, sourceConn, backupConn);
                }
            }
            
            // Commit the transaction
            try (Statement stmt = backupConn.createStatement()) {
                stmt.executeUpdate("COMMIT");
                stmt.executeUpdate("PRAGMA optimize");
            }
            
            logger.info("Database backup completed successfully via SQL approach.");
        } catch (SQLException e) {
            logger.severe("Failed to back up database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the schema SQL for a table
     */
    private static String getTableSchema(String tableName, Connection conn) throws SQLException {
        String sql = "SELECT sql FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("sql");
            }
        }
        return null;
    }
    
    /**
     * Copies data from one table to another
     */
    private static void copyTableData(String tableName, Connection sourceConn, Connection targetConn) throws SQLException {
        // Get column names
        DatabaseMetaData metaData = sourceConn.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        
        List<String> columnNames = new ArrayList<>();
        while (columns.next()) {
            columnNames.add(columns.getString("COLUMN_NAME"));
        }
        
        if (columnNames.isEmpty()) {
            logger.warning("No columns found for table: " + tableName);
            return;
        }
        
        // Prepare source query
        String selectSql = "SELECT * FROM " + tableName;
        
        // Prepare insert statement
        StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + " (");
        insertSql.append(String.join(", ", columnNames));
        insertSql.append(") VALUES (");
        insertSql.append(String.join(", ", Collections.nCopies(columnNames.size(), "?")));
        insertSql.append(")");
        
        try (Statement selectStmt = sourceConn.createStatement();
             ResultSet rows = selectStmt.executeQuery(selectSql);
             PreparedStatement insertStmt = targetConn.prepareStatement(insertSql.toString())) {
            
            int totalRows = 0;
            while (rows.next()) {
                for (int i = 0; i < columnNames.size(); i++) {
                    insertStmt.setObject(i + 1, rows.getObject(columnNames.get(i)));
                }
                insertStmt.addBatch();
                totalRows++;
                
                // Execute in batches of 500 rows
                if (totalRows % 500 == 0) {
                    insertStmt.executeBatch();
                    insertStmt.clearBatch();
                }
            }
            
            // Execute any remaining batch
            if (totalRows % 500 != 0) {
                insertStmt.executeBatch();
            }
            
            logger.info("Copied " + totalRows + " rows for table " + tableName);
        }
    }
    
    /**
     * Creates a new empty database with the basic schema
     */
    private static void createNewDatabase(String dbPath) throws SQLException, IOException {
        // Create a connection to create the new database file
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath)) {
            // Find the schema.sql file in the classpath
            String schemaPath = "src\\main\\java\\database\\Schema.sql";
            File schemaFile = new File(schemaPath);
            
            if (!schemaFile.exists()) {
                throw new IOException("Schema file not found at: " + schemaPath);
            }
            
            // Read the schema file
            StringBuilder schemaSql = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(schemaFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    schemaSql.append(line).append("\n");
                }
            }
            
            // Execute the schema SQL
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(schemaSql.toString());
                logger.info("Created new database with schema from " + schemaPath);
            }
        }
    }

    /**
     * Main method to initiate the database backup process.
     * 
     * @param args command-line arguments (not used here)
     */
    public static void main(String[] args) {
        String backupFilePath = "backups/BackUP.db"; // Specify the backup file path
        backupDatabase(backupFilePath); // Start the backup process
    }
}
