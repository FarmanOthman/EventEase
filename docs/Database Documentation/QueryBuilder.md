Here's a detailed markdown file based on the `QueryBuilder` class you've provided. The file includes an overview of the class, methods, and examples for usage.

---

# QueryBuilder Class Documentation

## Overview

The `QueryBuilder` class is a utility to dynamically construct and execute SQL queries. It supports various SQL operations like `INSERT`, `UPDATE`, `DELETE`, and `SELECT`, including clauses such as `WHERE`, `JOIN`, `GROUP BY`, `ORDER BY`, and `LIMIT`. This class allows developers to build SQL queries programmatically and execute them through a `PreparedStatement`.

The class is designed for use in database interaction within Java applications.

## Features

- Supports constructing SQL queries with dynamic parameters.
- Enables SQL operations: `INSERT`, `UPDATE`, `DELETE`, `SELECT`.
- Includes support for advanced query clauses such as `JOIN`, `GROUP BY`, `ORDER BY`, and `LIMIT`.
- Handles SQL parameter binding with appropriate data types.
- Logger support to track query execution success or failure.

## Enum: `QueryType`

The `QueryType` enum defines the supported SQL query types:

```java
public enum QueryType {
    SELECT, INSERT, UPDATE, DELETE
}
```

## Constructor

```java
public QueryBuilder()
```

- Initializes an empty query builder with a `StringBuilder` for query construction and a `List<Object>` for query parameters.

## Methods

### `insert(String table)`

Begins an `INSERT` query for the specified table.

- **Parameters:**
  - `table`: The name of the table to insert into.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `update(String table)`

Begins an `UPDATE` query for the specified table.

- **Parameters:**
  - `table`: The name of the table to update.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `delete(String table)`

Begins a `DELETE` query for the specified table.

- **Parameters:**
  - `table`: The name of the table to delete from.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `select(String table)`

Begins a `SELECT` query for the specified table.

- **Parameters:**
  - `table`: The name of the table to select from.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `set(String setClause)`

Adds a `SET` clause for `UPDATE` and `INSERT` queries.

- **Parameters:**
  - `setClause`: The `SET` clause (e.g., `"column1 = ?, column2 = ?"`).
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `where(String whereClause)`

Adds a `WHERE` clause to the query.

- **Parameters:**
  - `whereClause`: The `WHERE` clause (e.g., `"column1 = ?"`)
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `whereIn(String column, List<Object> values)`

Adds a `WHERE IN` clause to the query.

- **Parameters:**
  - `column`: The column to use in the `IN` clause.
  - `values`: A list of values for the `IN` clause.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `join(String table, String onClause)`

Adds a `JOIN` clause to the query.

- **Parameters:**
  - `table`: The table to join with.
  - `onClause`: The `ON` clause for the `JOIN` (e.g., `"a.id = b.id"`).
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `groupBy(String groupByClause)`

Adds a `GROUP BY` clause to the query.

- **Parameters:**
  - `groupByClause`: The column to group by (e.g., `"column1"`).
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `orderBy(String orderByClause)`

Adds an `ORDER BY` clause to the query.

- **Parameters:**
  - `orderByClause`: The `ORDER BY` clause (e.g., `"column1 ASC"`).
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `limit(int limit)`

Adds a `LIMIT` clause to the query.

- **Parameters:**
  - `limit`: The number of rows to limit the query to.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `addParameters(Object... params)`

Adds parameters to the query.

- **Parameters:**
  - `params`: The values to add as parameters.
  
- **Returns:** 
  - The current `QueryBuilder` instance for method chaining.

### `build()`

Builds the final SQL query string.

- **Returns:** 
  - The constructed SQL query string.

- **Throws:**
  - `IllegalStateException` if the query type is not set.

### `executeUpdate()`

Executes the `INSERT`, `UPDATE`, or `DELETE` query.

- **Throws:**
  - `SQLException` if an SQL error occurs during query execution.

### `executeRead()`

Executes a `SELECT` query and returns the results as a list of maps (column name → value).

- **Returns:** 
  - A list of maps representing the query result.

- **Throws:**
  - `SQLException` if an SQL error occurs during query execution.

### `setParameters(PreparedStatement pstmt)`

Sets parameters for the `PreparedStatement`.

- **Parameters:**
  - `pstmt`: The `PreparedStatement` to set parameters on.

- **Throws:**
  - `SQLException` if an error occurs while setting parameters.

### `validateTableName(String table)`

Validates if the table name is valid (non-null and non-empty).

- **Parameters:**
  - `table`: The table name to validate.

- **Throws:**
  - `IllegalArgumentException` if the table name is null or empty.

### `validateClause(String clause)`

Validates if a SQL clause (such as `WHERE`, `SET`) is valid (non-null and non-empty).

- **Parameters:**
  - `clause`: The SQL clause to validate.

- **Throws:**
  - `IllegalArgumentException` if the clause is null or empty.

## Example Usage

### Insert Example

```java
QueryBuilder insertQuery = new QueryBuilder();
insertQuery.insert("CUSTOMER")
    .addParameters(1, "John", "Doe", "123-456-7890", "H1PdI@example.com")
    .set("customer_id, first_name, last_name, contact_number, email")
    .executeUpdate();
```

### Select Example

```java
QueryBuilder selectQuery = new QueryBuilder();
selectQuery.select("CUSTOMER")
    .where("customer_id = ?")
    .addParameters(1);
List<Map<String, Object>> results = selectQuery.executeRead();
System.out.println(results);
```

## Conclusion

The `QueryBuilder` class provides a flexible and reusable way to construct SQL queries dynamically, making it easier to work with databases in Java. By supporting various SQL operations and advanced query features, it simplifies complex database interactions and reduces the likelihood of SQL injection vulnerabilities.