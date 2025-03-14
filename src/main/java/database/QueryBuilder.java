package database;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.*;
import java.util.*;

public class QueryBuilder {

    private DSLContext create;
    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/EventEase.db";

    // Constructor initializes the database connection
    public QueryBuilder() {
        this.connection = connectToDatabase();
        this.create = DSL.using(connection, SQLDialect.SQLITE);
    }

    // Establish connection to the SQLite database
    private Connection connectToDatabase() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    // Close the database connection
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert data into a table
    public void insert(String table, Map<String, Object> values) {
        Table<?> targetTable = DSL.table(DSL.name(table));

        List<Field<?>> columns = new ArrayList<>();
        List<Object> insertValues = new ArrayList<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            columns.add(DSL.field(DSL.name(entry.getKey()), SQLDataType.VARCHAR));
            insertValues.add(entry.getValue());
        }

        try {
            create.insertInto(targetTable, columns.toArray(new Field[0]))
                    .values(insertValues.toArray())
                    .execute();
            System.out.println("Record inserted into table: " + table);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error inserting data into table: " + table);
        }
    }

    // Select data from a table
    public List<Map<String, Object>> select(String table, String... columns) {
        Table<?> targetTable = DSL.table(DSL.name(table));
        List<Field<?>> fieldList = new ArrayList<>();

        for (String column : columns) {
            fieldList.add(DSL.field(DSL.name(column)));
        }

        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            Result<Record> result = create.select(fieldList).from(targetTable).fetch();
            for (Record record : result) {
                Map<String, Object> row = new HashMap<>();
                for (Field<?> field : fieldList) {
                    row.put(field.getName(), record.get(field));
                }
                resultList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error selecting data from table: " + table);
        }

        return resultList; // Return the list of maps
    }

    // Update data in a table
    public void update(String table, Map<String, Object> values, String conditionColumn, Object conditionValue) {
        Table<?> targetTable = DSL.table(DSL.name(table));
        UpdateSetFirstStep<?> updateQuery = create.update(targetTable);

        UpdateSetMoreStep<?> finalQuery = null;

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (finalQuery == null) {
                finalQuery = updateQuery.set(DSL.field(DSL.name(entry.getKey())), entry.getValue());
            } else {
                finalQuery = finalQuery.set(DSL.field(DSL.name(entry.getKey())), entry.getValue());
            }
        }

        if (finalQuery != null) {
            finalQuery.where(DSL.field(DSL.name(conditionColumn)).eq(conditionValue)).execute();
            System.out.println("Record updated in table: " + table);
        }
    }

    // Delete data from a table
    public void delete(String table, String conditionColumn, Object conditionValue) {
        Table<?> targetTable = DSL.table(DSL.name(table));

        try {
            create.deleteFrom(targetTable)
                    .where(DSL.field(DSL.name(conditionColumn)).eq(conditionValue))
                    .execute();
            System.out.println("Record deleted from table: " + table);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error deleting data from table: " + table);
        }
    }

}