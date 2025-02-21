package database;

import java.sql.*;
import java.util.*;
import java.util.logging.*;
import java.io.*; // Import for File and IO operations
import java.util.concurrent.locks.*; // Import for ReentrantLock // Adjust the import based on your package structure

/**
 * QueryBuilder is a utility class to construct and execute SQL queries
 * dynamically.
 * It supports INSERT, UPDATE, DELETE, and SELECT queries, including JOINs,
 * GROUP BY, ORDER BY, and more.
 */
public class QueryBuilder {
    private static final Logger logger = Logger.getLogger(QueryBuilder.class.getName());
    private StringBuilder query;
    private List<Object> parameters;
    private QueryType queryType;
    private final ReentrantLock lock = new ReentrantLock(); // Ensures thread safety during backup

    // Enum for Query Types (INSERT, UPDATE, DELETE, SELECT)
    public enum QueryType {
        SELECT, INSERT, UPDATE, DELETE
    }

    // Constructor initializes query string and parameters list
    public QueryBuilder() {
        query = new StringBuilder();
        parameters = new ArrayList<>();
    }

    /**
     * Begins an INSERT query for a specified table.
     * 
     * @param table The name of the table to insert into.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder insert(String table) {
        validateTableName(table);
        queryType = QueryType.INSERT;
        query.append("INSERT INTO ").append(table).append(" ");
        return this;
    }

    /**
     * Begins an UPDATE query for a specified table.
     * 
     * @param table The name of the table to update.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder update(String table) {
        validateTableName(table);
        queryType = QueryType.UPDATE;
        query.append("UPDATE ").append(table).append(" ");
        return this;
    }

    /**
     * Begins a DELETE query for a specified table.
     * 
     * @param table The name of the table to delete from.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder delete(String table) {
        validateTableName(table);
        queryType = QueryType.DELETE;
        query.append("DELETE FROM ").append(table).append(" ");
        return this;
    }

    /**
     * Begins a SELECT query for a specified table.
     * 
     * @param table The name of the table to select from.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder select(String table) {
        validateTableName(table);
        queryType = QueryType.SELECT;
        query.append("SELECT * FROM ").append(table).append(" ");
        return this;
    }

    /**
     * Adds a SET clause for UPDATE and INSERT queries.
     * 
     * @param setClause The SET clause (e.g., "column1 = ?, column2 = ?").
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder set(String setClause) {
        validateClause(setClause);
        query.append("SET ").append(setClause).append(" ");
        return this;
    }

    /**
     * Adds a WHERE clause to the query.
     * 
     * @param whereClause The WHERE clause (e.g., "column1 = ?").
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder where(String whereClause) {
        validateClause(whereClause);
        query.append("WHERE ").append(whereClause).append(" ");
        return this;
    }

    /**
     * Adds a WHERE IN clause to the query.
     * 
     * @param column The column to use in the IN clause.
     * @param values The list of values for the IN clause.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder whereIn(String column, List<Object> values) {
        query.append("WHERE ").append(column).append(" IN (");
        for (int i = 0; i < values.size(); i++) {
            query.append("?");
            if (i < values.size() - 1)
                query.append(", ");
        }
        query.append(") ");
        parameters.addAll(values);
        return this;
    }

    /**
     * Adds a JOIN clause to the query.
     * 
     * @param table    The table to join with.
     * @param onClause The ON clause for the JOIN (e.g., "a.id = b.id").
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder join(String table, String onClause) {
        query.append("JOIN ").append(table).append(" ON ").append(onClause).append(" ");
        return this;
    }

    /**
     * Adds a GROUP BY clause to the query.
     * 
     * @param groupByClause The GROUP BY clause (e.g., "column1").
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder groupBy(String groupByClause) {
        query.append("GROUP BY ").append(groupByClause).append(" ");
        return this;
    }

    /**
     * Adds an ORDER BY clause to the query.
     * 
     * @param orderByClause The ORDER BY clause (e.g., "column1 ASC").
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder orderBy(String orderByClause) {
        query.append("ORDER BY ").append(orderByClause).append(" ");
        return this;
    }

    /**
     * Adds a LIMIT clause to the query.
     * 
     * @param limit The number of rows to limit the query to.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder limit(int limit) {
        query.append("LIMIT ").append(limit).append(" ");
        return this;
    }

    /**
     * Adds values to the query parameters.
     * 
     * @param params The values to add.
     * @return The current QueryBuilder instance for method chaining.
     */
    public QueryBuilder addParameters(Object... params) {
        for (Object param : params) {
            parameters.add(param);
        }
        return this;
    }

    /**
     * Builds the final SQL query string.
     * 
     * @return The constructed SQL query string.
     */
    public String build() {
        if (queryType == null) {
            throw new IllegalStateException(
                    "Query type is not set. Please specify a query type (INSERT, UPDATE, SELECT, DELETE).");
        }
        return query.toString().trim();
    }

    /**
     * Executes the update query (INSERT, UPDATE, DELETE).
     * 
     * @throws SQLException If an SQL error occurs during query execution.
     */
    public void executeUpdate() throws SQLException {
        String sql = build();
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt);
            pstmt.executeUpdate();
            logger.info("✅ Query executed successfully: " + sql + " with parameters: " + parameters);
        } catch (SQLException e) {
            logger.severe("❌ Query failed: " + sql + " with parameters: " + parameters);
            throw new SQLException("Error executing query: " + sql, e);
        }
    }

    /**
     * Executes the read query (SELECT).
     * 
     * @return The results of the SELECT query as a list of maps (column name ->
     *         value).
     * @throws SQLException If an SQL error occurs during query execution.
     */
    public List<Map<String, Object>> executeRead() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = build();
        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setParameters(pstmt);
            ResultSet rs = pstmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }
            logger.info("✅ Query executed successfully: " + sql + " with parameters: " + parameters);
        } catch (SQLException e) {
            logger.severe("❌ Query failed: " + sql + " with parameters: " + parameters);
            throw new SQLException("Error executing query: " + sql, e);
        }
        return result;
    }

    /**
     * Sets parameters for the PreparedStatement.
     * 
     * @param pstmt The PreparedStatement to set parameters on.
     * @throws SQLException If an error occurs while setting parameters.
     */
    private void setParameters(PreparedStatement pstmt) throws SQLException {
        int index = 1;
        for (Object param : parameters) {
            if (param instanceof String) {
                pstmt.setString(index++, (String) param);
            } else if (param instanceof Integer) {
                pstmt.setInt(index++, (Integer) param);
            } else if (param instanceof Boolean) {
                pstmt.setBoolean(index++, (Boolean) param);
            } else if (param instanceof java.sql.Date) {
                pstmt.setDate(index++, (java.sql.Date) param);
            } else {
                pstmt.setObject(index++, param);
            }
        }
    }

    /**
     * Validates if the table name is not null or empty.
     * 
     * @param table The table name to validate.
     */
    private void validateTableName(String table) {
        if (table == null || table.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        }
    }

    /**
     * Validates the clauses (SET, WHERE, etc.) are not null or empty.
     * 
     * @param clause The clause to validate.
     */
    private void validateClause(String clause) {
        if (clause == null || clause.isEmpty()) {
            throw new IllegalArgumentException("Clause cannot be null or empty.");
        }
    }

    /**
     * Custom exception for query-related errors.
     */
    public static class QueryBuilderException extends RuntimeException {
        public QueryBuilderException(String message) {
            super(message);
        }

        public QueryBuilderException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Main method for testing the QueryBuilder class.
     * 
     * @param args Unused.
     */
    public static void main(String[] args) {
        try {
            QueryBuilder queryBuilder = new QueryBuilder();
            queryBuilder.select("CUSTOMER").limit(5);
            List<Map<String, Object>> results = queryBuilder.executeRead();
            for (Map<String, Object> row : results) {
                System.out.println(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Backs up the SQLite database to a specified file.
     * 
     * @param backupFile The file where the backup will be stored.
     * @throws BackupException If an error occurs during the backup process.
     */
    public void backupDatabase(File backupFile) throws BackupException {
        lock.lock(); // Ensure that backup is thread-safe
        try {
            if (backupFile == null) {
                throw new IllegalArgumentException("Backup file cannot be null.");
            }

            // Create a connection to the SQLite database
            try (Connection conn = Database.getConnection();
                    FileWriter fileWriter = new FileWriter(backupFile);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                // Start by writing the header for the SQL file
                bufferedWriter.write("-- SQLite Database Backup\n");
                bufferedWriter.write("-- Generated on: " + new java.util.Date() + "\n\n");

                // Get all tables in the database
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });
                int tableCount = getTableCount(tables);
                int currentTableIndex = 0;

                // Reset the cursor for tables and back up each one
                tables.beforeFirst();
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    currentTableIndex++;
                    logger.info("Backing up table: " + tableName);
                    backupTable(tableName, bufferedWriter, conn);

                    // Report progress
                    int progress = (int) (((double) currentTableIndex / tableCount) * 100);
                    logger.info("Progress: " + progress + "% - Table " + currentTableIndex + " of " + tableCount);
                }

                logger.info("Database backup completed successfully.");
            } catch (SQLException | IOException e) {
                logger.severe("Error during backup: " + e.getMessage());
                logger.severe("Stack trace: ");
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.severe(element.toString());
                }
                throw new BackupException("Backup failed due to SQL or IO error.", e);
            }
        } finally {
            lock.unlock(); // Release lock after backup is complete
        }
    }

    /**
     * Counts the total number of tables in the database.
     * 
     * @param tables The ResultSet from the database metadata query.
     * @return The total number of tables.
     * @throws SQLException If an SQL error occurs.
     */
    private int getTableCount(ResultSet tables) throws SQLException {
        int count = 0;
        while (tables.next()) {
            count++;
        }
        return count;
    }

    /**
     * Backs up a single table by writing its schema and data to the backup file.
     * 
     * @param tableName The name of the table to back up.
     * @param writer    The writer to which the SQL statements are written.
     * @param conn      The connection to the database.
     * @throws SQLException If an SQL error occurs during the backup process.
     * @throws IOException  If an I/O error occurs while writing to the file.
     */
    private void backupTable(String tableName, BufferedWriter writer, Connection conn)
            throws SQLException, IOException {
        // Write the table creation script
        writeTableSchema(tableName, writer, conn);

        // Write the table data insert statements
        writeTableData(tableName, writer, conn);
    }

    /**
     * Writes the CREATE TABLE statement to the backup file.
     * 
     * @param tableName The name of the table.
     * @param writer    The writer to which the SQL statements are written.
     * @param conn      The connection to the database.
     * @throws SQLException If an SQL error occurs.
     * @throws IOException  If an I/O error occurs.
     */
    private void writeTableSchema(String tableName, BufferedWriter writer, Connection conn)
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

    /**
     * Writes all data from a table to the backup file in INSERT INTO statements.
     * 
     * @param tableName The name of the table.
     * @param writer    The writer to which the SQL statements are written.
     * @param conn      The connection to the database.
     * @throws SQLException If an SQL error occurs.
     * @throws IOException  If an I/O error occurs.
     */
    private void writeTableData(String tableName, BufferedWriter writer, Connection conn)
            throws SQLException, IOException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

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

    public static class BackupException extends Exception {
        public BackupException(String message) {
            super(message);
        }

        public BackupException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
