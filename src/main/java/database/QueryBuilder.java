package database;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * QueryBuilder is a utility class to construct and execute SQL queries
 * dynamically.
 * It supports INSERT, UPDATE, DELETE, and SELECT queries, including JOINs,
 * GROUP BY,
 * ORDER BY, and more.
 */
public class QueryBuilder {
    private static final Logger logger = Logger.getLogger(QueryBuilder.class.getName());
    private StringBuilder query;
    private List<Object> parameters;
    private QueryType queryType;

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
            if (i < values.size() - 1) {
                query.append(", ");
            }
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
        parameters.addAll(Arrays.asList(params));
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
     * Validates if a table name is valid (non-null and non-empty).
     *
     * @param table The table name to validate.
     */
    private void validateTableName(String table) {
        if (table == null || table.isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty.");
        }
    }

    /**
     * Validates if a SQL clause (such as WHERE, SET) is valid (non-null and
     * non-empty).
     *
     * @param clause The SQL clause to validate.
     */
    private void validateClause(String clause) {
        if (clause == null || clause.isEmpty()) {
            throw new IllegalArgumentException("Clause cannot be null or empty.");
        }
    }

    // Additional helper methods can be added for more complex queries or actions.


    
    // Example usage of QueryBuilder class and every thing works fine

    // public static void main(String[] args) {
    //     try {
    //         // Example: Execute an INSERT query for ADMIN
    //         QueryBuilder insertAdminQuery = new QueryBuilder();
    //         insertAdminQuery.insert("ADMIN")
    //                 .addParameters(1, "admin", "admin123", "admin@example.com", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis())).query
    //                 .append(" (admin_id, username, password, email, role_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)");
    //         insertAdminQuery.executeUpdate();

    //         // Example: Execute an INSERT query
    //         QueryBuilder insertQuery = new QueryBuilder();
    //         insertQuery.insert("CUSTOMER")
    //                 .addParameters(1, "John", "Doe", "123-456-7890", "H1PdI@example.com").query
    //                 .append(" (customer_id, first_name, last_name, contact_number, email) VALUES (?, ?, ?, ?, ?, ?, ?)");
    //         insertQuery.executeUpdate();

    //         // Example: Execute a SELECT query
    //         QueryBuilder selectQuery = new QueryBuilder();
    //         selectQuery.select("CUSTOMER")
    //                 .where("customer_id = ?")
    //                 .addParameters(1);
    //         List<Map<String, Object>> results = selectQuery.executeRead();
    //         System.out.println(results);
    //     } catch (SQLException e) {
    //         logger.severe("Error: " + e.getMessage());
    //     }
    // }
}
