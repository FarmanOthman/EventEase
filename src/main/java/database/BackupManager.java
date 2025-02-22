package database;

import java.io.*;
import java.sql.*;
import java.util.logging.*;

public class BackupManager {

    // Logger for recording backup process details
    private static final Logger logger = Logger.getLogger(BackupManager.class.getName());

    /**
     * Backs up the database to the specified backup file path.
     * It includes both the schema (CREATE statements) and data (INSERT statements).
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
            return; // Stop if the database file doesn't exist
        }

        // Attempt to establish a connection to the SQLite database
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                logger.severe("Failed to establish connection to the database.");
                return; // Exit if connection to the database fails
            }

            // Create and write to the backup file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFilePath))) {
                // Start the backup file with some introductory comments
                writer.write("-- SQLite Database Backup\n");
                writer.write("-- Generated on: " + new java.util.Date() + "\n\n");

                // Get all tables in the database to back up their data and structure
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    logger.info("Backing up table: " + tableName);

                    // Write the schema (CREATE statement) for each table
                    writeTableSchema(tableName, conn, writer);

                    // Write the data (INSERT statements) for each table
                    writeTableData(tableName, conn, writer);
                }

                logger.info("Database backup completed successfully.");
            } catch (IOException e) {
                // Handle failure to write to the backup file
                logger.severe("Failed to write to the backup file: " + e.getMessage());
            }
        } catch (SQLException e) {
            // Handle SQL exceptions while connecting to the database or executing SQL
            // queries
            logger.severe("Failed to establish a connection or SQL error: " + e.getMessage());
        }
    }

    /**
     * Writes the schema (CREATE statement) for a specified table to the backup
     * file.
     * 
     * @param tableName the name of the table
     * @param conn      the connection to the database
     * @param writer    the writer for the backup file
     * @throws SQLException if a database access error occurs
     * @throws IOException  if an I/O error occurs while writing
     */
    private static void writeTableSchema(String tableName, Connection conn, BufferedWriter writer)
            throws SQLException, IOException {
        String sql = "SELECT sql FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName); // Set the table name in the query
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Write the CREATE statement to the backup file
                writer.write("-- Schema for table: " + tableName + "\n");
                writer.write(rs.getString("sql") + ";\n\n");
            }
        }
    }

    /**
     * Writes the data (INSERT statements) for a specified table to the backup file.
     * 
     * @param tableName the name of the table
     * @param conn      the connection to the database
     * @param writer    the writer for the backup file
     * @throws SQLException if a database access error occurs
     * @throws IOException  if an I/O error occurs while writing
     */
    private static void writeTableData(String tableName, Connection conn, BufferedWriter writer)
            throws SQLException, IOException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write each row of data as an INSERT statement
            while (rs.next()) {
                StringBuilder insertStatement = new StringBuilder("INSERT INTO " + tableName + " VALUES(");
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value == null) {
                        insertStatement.append("NULL");
                    } else {
                        insertStatement.append("'").append(value.replace("'", "''")).append("'"); // Escape single
                                                                                                  // quotes
                    }
                    if (i < columnCount) {
                        insertStatement.append(", ");
                    }
                }
                insertStatement.append(");\n");
                writer.write(insertStatement.toString()); // Write INSERT statement to the backup file
            }
            writer.write("\n");
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
