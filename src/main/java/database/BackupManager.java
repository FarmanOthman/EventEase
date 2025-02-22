package database;

import java.io.*;
import java.sql.*;
import java.util.logging.*;

public class BackupManager {
    private static final Logger logger = Logger.getLogger(BackupManager.class.getName());

    public static void backupDatabase(String backupFilePath) {
        // Log the database path
        String dbPath = Database.DB_URL.replace("jdbc:sqlite:", "");
        logger.info("Attempting to back up database at: " + dbPath);

        // Check if the database file exists
        File dbFile = new File(dbPath);
        if (!dbFile.exists() || !dbFile.isFile()) {
            logger.severe("Database file does not exist or is not a valid file: " + dbFile.getAbsolutePath());
            return;
        }

        // Create a connection to the SQLite database
        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                logger.severe("Failed to establish connection to the database.");
                return;
            }

            // Create backup file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFilePath))) {
                // Start writing to the backup file
                writer.write("-- SQLite Database Backup\n");
                writer.write("-- Generated on: " + new java.util.Date() + "\n\n");

                // Get all tables in the database
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    logger.info("Backing up table: " + tableName);

                    // Write the table schema (CREATE statement)
                    writeTableSchema(tableName, conn, writer);

                    // Write the table data (INSERT statements)
                    writeTableData(tableName, conn, writer);
                }

                logger.info("Database backup completed successfully.");
            } catch (IOException e) {
                logger.severe("Failed to write to the backup file: " + e.getMessage());
            }
        } catch (SQLException e) {
            logger.severe("Failed to establish a connection or SQL error: " + e.getMessage());
        }
    }

    private static void writeTableSchema(String tableName, Connection conn, BufferedWriter writer)
            throws SQLException, IOException {
        String sql = "SELECT sql FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                writer.write("-- Schema for table: " + tableName + "\n");
                writer.write(rs.getString("sql") + ";\n\n");
            }
        }
    }

    private static void writeTableData(String tableName, Connection conn, BufferedWriter writer)
            throws SQLException, IOException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write data as INSERT statements
            while (rs.next()) {
                StringBuilder insertStatement = new StringBuilder("INSERT INTO " + tableName + " VALUES(");
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value == null) {
                        insertStatement.append("NULL");
                    } else {
                        insertStatement.append("'").append(value.replace("'", "''")).append("'");
                    }
                    if (i < columnCount) {
                        insertStatement.append(", ");
                    }
                }
                insertStatement.append(");\n");
                writer.write(insertStatement.toString());
            }
            writer.write("\n");
        }
    }

    public static void main(String[] args) {
        String backupFilePath = "backups/BackUP.db";
        backupDatabase(backupFilePath);
    }
}
