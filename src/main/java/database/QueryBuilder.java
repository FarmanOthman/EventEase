package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;

public class QueryBuilder {
    private StringBuilder query;
    private List<Object> parameters;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(QueryBuilder.class.getName());

    // Allowed tables and columns based on EventEase schema
    private static final List<String> ALLOWED_TABLES = List.of(
            "CUSTOMER", "TEAM", "EVENT", "BOOKING_EVENT", "BOOKING_TICKET",
            "TICKET_CATEGORY", "TICKET", "ADMIN", "MANAGER", "ROLE",
            "REPORT", "NOTIFICATION");

    private static final List<String> ALLOWED_COLUMNS = List.of(
            "id", "name", "email", "booking_date", "ticket_id",
            "team_id", "event_id", "price", "status",
            "created_at", "updated_at", "first_name", "last_name",
            "password", "category_name", "total_price", "message",
            "notification_type", "is_read", "receipt_number");

    public QueryBuilder() {
        this.query = new StringBuilder();
        this.parameters = new ArrayList<>();
        this.connection = connectToDatabase();
    }

    private Connection connectToDatabase() {
        String dbUrl = "jdbc:sqlite:src/main/resources/EventEase.db";
        try {
            return DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to the database", e);
            throw new RuntimeException("Database connection error", e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Database connection closed.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing the database connection", e);
            }
        }
    }

    public QueryBuilder select(String... columns) {
        query.append("SELECT ");
        if (columns.length == 0) {
            query.append("*");
        } else {
            for (String column : columns) {
                validateColumn(column);
            }
            query.append(String.join(", ", columns));
        }
        return this;
    }

    public QueryBuilder from(String table) {
        validateTable(table);
        query.append(" FROM ").append(table);
        return this;
    }

    private void validateTable(String table) {
        if (!ALLOWED_TABLES.contains(table)) {
            throw new IllegalArgumentException("Invalid table name: " + table);
        }
    }

    private void validateColumn(String column) {
        if (!ALLOWED_COLUMNS.contains(column)) {
            throw new IllegalArgumentException("Invalid column name: " + column);
        }
    }

    public QueryBuilder where(String condition, Object... params) {
        query.append(" WHERE ").append(condition);
        for (Object param : params) {
            parameters.add(param);
        }
        return this;
    }

    private boolean isUpdateOrDeleteQuery() {
        String queryString = query.toString().toUpperCase();
        return queryString.startsWith("UPDATE") || queryString.startsWith("DELETE");
    }

    private void confirmDangerousQuery() {
        if (isUpdateOrDeleteQuery() && !query.toString().contains("WHERE")) {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println(
                        "Warning: You are about to run an UPDATE/DELETE query without a WHERE clause, which could affect the entire table.");
                System.out.print("Do you want to proceed? (yes/no): ");
                String response = scanner.nextLine();
                if (!response.equalsIgnoreCase("yes")) {
                    throw new IllegalStateException("Query aborted by user.");
                }
            }
        }
    }

    public void executeUpdate() {
        try {
            confirmDangerousQuery();
            try (PreparedStatement stmt = connection.prepareStatement(build())) {
                for (int i = 0; i < parameters.size(); i++) {
                    stmt.setObject(i + 1, parameters.get(i));
                }
                stmt.executeUpdate();
                LOGGER.info("Query executed successfully: " + build());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: " + build() + " with parameters: " + parameters, e);
            throw new RuntimeException("Error executing query", e);
        }
    }

    public ResultSet executeQuery() {
        try (PreparedStatement stmt = connection.prepareStatement(build())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            return stmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error executing query: " + build() + " with parameters: " + parameters, e);
            throw new RuntimeException("Error executing query", e);
        }
    }

    public String build() {
        return query.toString();
    }

    public QueryBuilder andCondition(String condition, Object... params) {
        query.append(" AND ").append(condition);
        for (Object param : params) {
            parameters.add(param);
        }
        return this;
    }

    public QueryBuilder orCondition(String condition, Object... params) {
        query.append(" OR ").append(condition);
        for (Object param : params) {
            parameters.add(param);
        }
        return this;
    }

    public QueryBuilder inCondition(String column, List<Object> values) {
        query.append(" AND ").append(column).append(" IN (");
        for (int i = 0; i < values.size(); i++) {
            query.append("?");
            if (i < values.size() - 1) {
                query.append(", ");
            }
            parameters.add(values.get(i));
        }
        query.append(")");
        return this;
    }

    public QueryBuilder join(String table, String onCondition) {
        validateTable(table);
        query.append(" JOIN ").append(table).append(" ON ").append(onCondition);
        return this;
    }

    public QueryBuilder leftJoin(String table, String onCondition) {
        validateTable(table);
        query.append(" LEFT JOIN ").append(table).append(" ON ").append(onCondition);
        return this;
    }

    public QueryBuilder rightJoin(String table, String onCondition) {
        validateTable(table);
        query.append(" RIGHT JOIN ").append(table).append(" ON ").append(onCondition);
        return this;
    }

    public QueryBuilder aggregate(String function, String column) {
        query.append(" ").append(function).append("(").append(column).append(")");
        return this;
    }

    public QueryBuilder whereSubquery(String column, String operator, QueryBuilder subquery) {
        query.append(" WHERE ").append(column).append(" ").append(operator).append(" (").append(subquery.build())
                .append(")");
        parameters.addAll(subquery.parameters);
        return this;
    }

    public QueryBuilder limit(int limit) {
        query.append(" LIMIT ").append(limit);
        return this;
    }

    public QueryBuilder offset(int offset) {
        query.append(" OFFSET ").append(offset);
        return this;
    }

    public void close() {
        closeConnection();
    }

    public static void main(String[] args) {
        String dbUrl = "jdbc:sqlite:src/main/resources/EventEase.db";

        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            QueryBuilder qb = new QueryBuilder();
            qb.select().from("CUSTOMER");

            System.out.println("Constructed Query: " + qb.build());
            System.out.println("Parameters: " + qb.parameters);

            ResultSet rs = qb.executeQuery();
            while (rs.next()) {
                System.out.println("Name: " + rs.getString("name") + ", Email: " + rs.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
