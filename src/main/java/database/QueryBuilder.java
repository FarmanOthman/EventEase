package database;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

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
            System.err.println("Error inserting data into table: " + table + " with values: " + values);
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
            System.err.println(
                    "Error selecting data from table: " + table + " with columns: " + Arrays.toString(columns));
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
            try {
                finalQuery.where(DSL.field(DSL.name(conditionColumn)).eq(conditionValue)).execute();
                System.out.println("Record updated in table: " + table);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error updating data in table: " + table + " with condition: " +
                        conditionColumn + " = " + conditionValue);
            }
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
            System.err.println("Error deleting data from table: " + table + " with condition: " +
                    conditionColumn + " = " + conditionValue);
        }
    }

    // Select data with filtering
    public List<Map<String, Object>> selectWithFilters(String table, Map<String, Object> filters, String[] columns) {
        return selectWithFilterAndSort(table, filters, null, true, columns, null, null);
    }

    // Select data with filtering and sorting
    public List<Map<String, Object>> selectWithFilterAndSort(String table, Map<String, Object> filters,
            String sortColumn, boolean ascending, String[] columns) {
        return selectWithFilterAndSort(table, filters, sortColumn, ascending, columns, null, null);
    }

    // Select data with filtering, sorting and pagination
    public List<Map<String, Object>> selectWithFilterAndSort(String table, Map<String, Object> filters,
            String sortColumn, boolean ascending,
            String[] columns, Integer limit, Integer offset) {
        Table<?> targetTable = DSL.table(DSL.name(table));
        List<Field<?>> fieldList = buildFieldList(columns);

        SortField<?> sortField = null;
        if (sortColumn != null) {
            Field<?> field = DSL.field(DSL.name(sortColumn));
            sortField = ascending ? field.asc() : field.desc();
        }

        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            Result<?> result = executeQuery(targetTable, fieldList, filters, null, sortField, limit, offset);

            for (Record record : result) {
                Map<String, Object> row = extractRecordData(record, fieldList, columns);
                resultList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error selecting data from table: " + table + " with filters: " + filters);
        }

        return resultList;
    }

    // Select with complex conditions (AND + OR)
    public List<Map<String, Object>> selectWithComplexFilters(String table, Map<String, Object> andFilters,
            Map<String, Object> orFilters, String[] columns, String sortColumn, boolean ascending,
            Integer limit, Integer offset) {
        Table<?> targetTable = DSL.table(DSL.name(table));
        List<Field<?>> fieldList = buildFieldList(columns);

        SortField<?> sortField = null;
        if (sortColumn != null) {
            Field<?> field = DSL.field(DSL.name(sortColumn));
            sortField = ascending ? field.asc() : field.desc();
        }

        // Build combined condition
        Condition finalCondition = buildComplexCondition(andFilters, orFilters);

        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            Result<?> result = executeQuery(targetTable, fieldList, null, finalCondition, sortField, limit, offset);

            for (Record record : result) {
                Map<String, Object> row = extractRecordData(record, fieldList, columns);
                resultList.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing complex query on table: " + table +
                    " with AND filters: " + andFilters + " OR filters: " + orFilters);
        }

        return resultList;
    }

    // Aggregate functions enum
    public enum AggregateFunction {
        COUNT(field -> DSL.count(field)),
        SUM(field -> {
            if (field.getDataType().isNumeric()) {
                @SuppressWarnings("unchecked")
                Field<? extends Number> numField = (Field<? extends Number>) field;
                return DSL.sum(numField);
            }
            return DSL.field("SUM({0})", field.getDataType(), field);
        }),
        AVG(field -> {
            if (field.getDataType().isNumeric()) {
                @SuppressWarnings("unchecked")
                Field<? extends Number> numField = (Field<? extends Number>) field;
                return DSL.avg(numField);
            }
            return DSL.field("AVG({0})", field.getDataType(), field);
        }),
        MAX(field -> DSL.max(field)),
        MIN(field -> DSL.min(field));

        private final Function<Field<?>, Field<?>> function;

        AggregateFunction(Function<Field<?>, Field<?>> function) {
            this.function = function;
        }

        public Field<?> apply(Field<?> field) {
            return function.apply(field);
        }
    }

    // Get aggregate value (count, sum, avg, etc.)
    public Object getAggregateValue(String table, String column, AggregateFunction function,
            Map<String, Object> filters) {
        Table<?> targetTable = DSL.table(DSL.name(table));
        Field<?> field = DSL.field(DSL.name(column));
        Field<?> aggregateField = function.apply(field);

        try {
            SelectJoinStep<?> baseQuery = create.select(aggregateField).from(targetTable);

            if (filters != null && !filters.isEmpty()) {
                Condition condition = buildAndCondition(filters);
                return baseQuery.where(condition).fetchOne(0);
            } else {
                return baseQuery.fetchOne(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error executing aggregate query on table: " + table +
                    " with function: " + function + " and column: " + column);
        }

        return null;
    }

    // Helper method to build field list
    private List<Field<?>> buildFieldList(String[] columns) {
        List<Field<?>> fieldList = new ArrayList<>();

        if (columns != null && columns.length > 0) {
            for (String column : columns) {
                fieldList.add(DSL.field(DSL.name(column)));
            }
        } else {
            fieldList.add(DSL.field("*"));
        }

        return fieldList;
    }

    // Helper method to extract data from a record
    private Map<String, Object> extractRecordData(Record record, List<Field<?>> fieldList, String[] columns) {
        Map<String, Object> row = new HashMap<>();

        if (columns != null && columns.length > 0) {
            for (Field<?> f : fieldList) {
                row.put(f.getName(), record.get(f));
            }
        } else {
            for (Field<?> f : record.fields()) {
                row.put(f.getName(), record.get(f));
            }
        }

        return row;
    }

    // Helper method to build AND condition
    private Condition buildAndCondition(Map<String, Object> filters) {
        Condition condition = null;

        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                if (condition == null) {
                    condition = DSL.field(DSL.name(entry.getKey())).eq(entry.getValue());
                } else {
                    condition = condition.and(DSL.field(DSL.name(entry.getKey())).eq(entry.getValue()));
                }
            }
        }

        return condition;
    }

    // Helper method to build complex condition (AND + OR)
    private Condition buildComplexCondition(Map<String, Object> andFilters, Map<String, Object> orFilters) {
        // Build AND conditions
        Condition andCondition = buildAndCondition(andFilters);

        // Build OR conditions
        Condition orCondition = null;
        if (orFilters != null && !orFilters.isEmpty()) {
            for (Map.Entry<String, Object> entry : orFilters.entrySet()) {
                if (orCondition == null) {
                    orCondition = DSL.field(DSL.name(entry.getKey())).eq(entry.getValue());
                } else {
                    orCondition = orCondition.or(DSL.field(DSL.name(entry.getKey())).eq(entry.getValue()));
                }
            }
        }

        // Combine AND and OR conditions
        Condition finalCondition = null;
        if (andCondition != null && orCondition != null) {
            finalCondition = andCondition.and(orCondition);
        } else if (andCondition != null) {
            finalCondition = andCondition;
        } else if (orCondition != null) {
            finalCondition = orCondition;
        }

        return finalCondition;
    }

    // Helper method to execute query with filters, sorting and pagination
    private Result<?> executeQuery(Table<?> targetTable, List<Field<?>> fieldList,
            Map<String, Object> filters, Condition customCondition,
            SortField<?> sortField, Integer limit, Integer offset) {
        try {
            // Start with building the select part
            SelectSelectStep<?> select = create.select(fieldList);

            // Add the from part
            SelectJoinStep<?> fromStep = select.from(targetTable);

            // Build the condition if needed
            Condition condition = null;
            if (customCondition != null) {
                condition = customCondition;
            } else if (filters != null && !filters.isEmpty()) {
                condition = buildAndCondition(filters);
            }

            // Build and execute the final query
            if (condition != null) {
                // With where condition
                if (sortField != null) {
                    // With sorting
                    if (limit != null) {
                        // With limit
                        if (offset != null) {
                            // With offset
                            return fromStep.where(condition).orderBy(sortField).limit(limit).offset(offset).fetch();
                        } else {
                            // Without offset
                            return fromStep.where(condition).orderBy(sortField).limit(limit).fetch();
                        }
                    } else {
                        // Without limit
                        return fromStep.where(condition).orderBy(sortField).fetch();
                    }
                } else {
                    // Without sorting
                    if (limit != null) {
                        // With limit
                        if (offset != null) {
                            // With offset
                            return fromStep.where(condition).limit(limit).offset(offset).fetch();
                        } else {
                            // Without offset
                            return fromStep.where(condition).limit(limit).fetch();
                        }
                    } else {
                        // Without limit
                        return fromStep.where(condition).fetch();
                    }
                }
            } else {
                // Without where condition
                if (sortField != null) {
                    // With sorting
                    if (limit != null) {
                        // With limit
                        if (offset != null) {
                            // With offset
                            return fromStep.orderBy(sortField).limit(limit).offset(offset).fetch();
                        } else {
                            // Without offset
                            return fromStep.orderBy(sortField).limit(limit).fetch();
                        }
                    } else {
                        // Without limit
                        return fromStep.orderBy(sortField).fetch();
                    }
                } else {
                    // Without sorting
                    if (limit != null) {
                        // With limit
                        if (offset != null) {
                            // With offset
                            return fromStep.limit(limit).offset(offset).fetch();
                        } else {
                            // Without offset
                            return fromStep.limit(limit).fetch();
                        }
                    } else {
                        // Without limit
                        return fromStep.fetch();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error executing query", e);
        }
    }
}