Below is the complete documentation for your `QueryBuilder` class, which uses the **jOOQ library** to interact with an SQLite database. This documentation is intended for other developers who will use your `QueryBuilder` class to perform database operations.

---

# QueryBuilder Documentation

The `QueryBuilder` class is a utility class designed to simplify database interactions using the **jOOQ library**. It provides methods for performing common database operations such as **insert**, **select**, **update**, and **delete** on an SQLite database.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Class Overview](#class-overview)
3. [Methods](#methods)
   - [Constructor](#constructor)
   - [insert](#insert)
   - [select](#select)
   - [update](#update)
   - [delete](#delete)
   - [closeConnection](#closeconnection)
4. [Usage Examples](#usage-examples)
5. [Error Handling](#error-handling)
6. [Best Practices](#best-practices)

---

## Prerequisites

Before using the `QueryBuilder` class, ensure the following:

1. **jOOQ Library**: Add the jOOQ dependency to your project. If you're using Maven, include the following in your `pom.xml`:

   ```xml
   <dependency>
       <groupId>org.jooq</groupId>
       <artifactId>jooq</artifactId>
       <version>3.18.7</version> <!-- Use the latest version -->
   </dependency>
   ```

2. **SQLite Database**: Ensure the SQLite database file (`EventEase.db`) exists in the `src/main/resources` directory.

3. **JDBC Driver**: Add the SQLite JDBC driver to your project. For Maven:

   ```xml
   <dependency>
       <groupId>org.xerial</groupId>
       <artifactId>sqlite-jdbc</artifactId>
       <version>3.43.0</version> <!-- Use the latest version -->
   </dependency>
   ```

---

## Class Overview

The `QueryBuilder` class provides the following features:

- **Database Connection Management**: Automatically connects to the SQLite database on initialization and provides a method to close the connection.
- **CRUD Operations**: Methods for inserting, selecting, updating, and deleting records.
- **Dynamic Query Building**: Uses jOOQ to dynamically build SQL queries based on input parameters.

---

## Methods

### Constructor

#### `public QueryBuilder()`

Initializes the `QueryBuilder` and establishes a connection to the SQLite database.

- **Database URL**: The database is located at `jdbc:sqlite:src/main/resources/EventEase.db`.
- **Connection**: The connection is stored in the `connection` field and used for all database operations.

---

### `insert`

#### `public void insert(String table, Map<String, Object> values)`

Inserts a new record into the specified table.

- **Parameters**:
  - `table`: The name of the table to insert into.
  - `values`: A `Map<String, Object>` where keys are column names and values are the data to insert.
- **Behavior**:
  - Dynamically builds an `INSERT` query using jOOQ.
  - Executes the query and prints a success message.
  - If an error occurs, it prints an error message and stack trace.

---

### `select`

#### `public void select(String table, String... columns)`

Retrieves records from the specified table.

- **Parameters**:
  - `table`: The name of the table to query.
  - `columns`: A varargs list of column names to retrieve.
- **Behavior**:
  - Dynamically builds a `SELECT` query using jOOQ.
  - Fetches and prints all matching records.
  - If an error occurs, it prints an error message and stack trace.

---

### `update`

#### `public void update(String table, Map<String, Object> values, String conditionColumn, Object conditionValue)`

Updates records in the specified table.

- **Parameters**:
  - `table`: The name of the table to update.
  - `values`: A `Map<String, Object>` where keys are column names and values are the new data.
  - `conditionColumn`: The column to use in the `WHERE` clause.
  - `conditionValue`: The value to match in the `WHERE` clause.
- **Behavior**:
  - Dynamically builds an `UPDATE` query using jOOQ.
  - Executes the query and prints a success message.
  - If an error occurs, it prints an error message and stack trace.

---

### `delete`

#### `public void delete(String table, String conditionColumn, Object conditionValue)`

Deletes records from the specified table.

- **Parameters**:
  - `table`: The name of the table to delete from.
  - `conditionColumn`: The column to use in the `WHERE` clause.
  - `conditionValue`: The value to match in the `WHERE` clause.
- **Behavior**:
  - Dynamically builds a `DELETE` query using jOOQ.
  - Executes the query and prints a success message.
  - If an error occurs, it prints an error message and stack trace.

---

### `closeConnection`

#### `public void closeConnection()`

Closes the database connection.

- **Behavior**:
  - Checks if the connection is open and closes it.
  - Prints a success message.
  - If an error occurs, it prints an error message and stack trace.

---

## Usage Examples

### Inserting Data

```java
Map<String, Object> insertValues = new HashMap<>();
insertValues.put("first_name", "John");
insertValues.put("last_name", "Doe");
insertValues.put("contact_number", "1234567890");
insertValues.put("email", "john.doe@example.com");

QueryBuilder qb = new QueryBuilder();
qb.insert("Customer", insertValues);
qb.closeConnection();
```

### Selecting Data

```java
QueryBuilder qb = new QueryBuilder();
qb.select("Customer", "first_name", "last_name", "email");
qb.closeConnection();
```

### Updating Data

```java
Map<String, Object> updateValues = new HashMap<>();
updateValues.put("email", "new.email@example.com");

QueryBuilder qb = new QueryBuilder();
qb.update("Customer", updateValues, "first_name", "John");
qb.closeConnection();
```

### Deleting Data

```java
QueryBuilder qb = new QueryBuilder();
qb.delete("Customer", "first_name", "John");
qb.closeConnection();
```

---

## Error Handling

- All methods catch exceptions and print error messages to the console.
- If a critical error occurs (e.g., connection failure), a `RuntimeException` is thrown.

---

## Best Practices

1. **Close Connections**: Always call `closeConnection()` after completing database operations to avoid resource leaks.
2. **Parameter Validation**: Validate input parameters (e.g., table names, column names) before passing them to the methods.
3. **Error Logging**: Replace `e.printStackTrace()` with a proper logging framework (e.g., Log4j or SLF4J) in production code.
4. **Transaction Management**: For complex operations, consider wrapping multiple queries in a transaction using jOOQ's transaction support.

---

## Example Main Class

```java
public class Main {
    public static void main(String[] args) {
        QueryBuilder qb = new QueryBuilder();

        try {
            // Insert example
            Map<String, Object> insertValues = new HashMap<>();
            insertValues.put("first_name", "Jane");
            insertValues.put("last_name", "Doe");
            insertValues.put("contact_number", "9876543210");
            insertValues.put("email", "jane.doe@example.com");
            qb.insert("Customer", insertValues);

            // Select example
            qb.select("Customer", "first_name", "last_name", "email");

            // Update example
            Map<String, Object> updateValues = new HashMap<>();
            updateValues.put("email", "updated.email@example.com");
            qb.update("Customer", updateValues, "first_name", "Jane");

            // Delete example
            qb.delete("Customer", "first_name", "Jane");

        } finally {
            qb.closeConnection();
        }
    }
}
```

---