# QueryBuilder - A Guide for Developers

## Overview
The `QueryBuilder` class provides a simple interface to interact with an SQLite database using **jOOQ (Java Object Oriented Querying)**. This class supports **CRUD (Create, Read, Update, Delete)** operations, making it easy for developers to perform database queries programmatically.

---

## Features
- **Connects** to an SQLite database automatically.
- **Performs CRUD operations** (Insert, Select, Update, Delete).
- Uses **jOOQ** for SQL query construction.
- **Handles exceptions** gracefully.
- **Ensures reusability** for future database operations.

---

## Project Structure
```
project-root/
├── src/main/java/database/QueryBuilder.java   # The QueryBuilder class
├── src/main/resources/EventEase.db            # SQLite database file
```

---

## 1. Database Connection
The constructor initializes the database connection and creates a `DSLContext` object to execute SQL queries.

```java
private static final String DB_URL = "jdbc:sqlite:src/main/resources/EventEase.db";

public QueryBuilder() {
    this.connection = connectToDatabase();
    this.create = DSL.using(connection, SQLDialect.SQLITE);
}
```

### **Connecting to SQLite**
```java
private Connection connectToDatabase() {
    try {
        return DriverManager.getConnection(DB_URL);
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to connect to the database", e);
    }
}
```

### **Closing the Connection**
```java
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
```

---

## 2. Insert Data
Inserts a new record into a specified table.

```java
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
```

### **Usage Example:**
```java
Map<String, Object> insertValues = new HashMap<>();
insertValues.put("first_name", "Farman");
insertValues.put("last_name", "Othman");
insertValues.put("contact_number", "07500000000");
insertValues.put("email", "james1234@gmail.com");
qb.insert("Customer", insertValues);
```

---

## 3. Select Data
Retrieves data from a table based on specified columns.

```java
public void select(String table, String... columns) {
    Table<?> targetTable = DSL.table(DSL.name(table));
    List<Field<?>> fieldList = new ArrayList<>();

    for (String column : columns) {
        fieldList.add(DSL.field(DSL.name(column)));
    }

    try {
        Result<Record> result = create.select(fieldList).from(targetTable).fetch();
        for (Record record : result) {
            System.out.println("Fetched Record: " + record);
        }
    } catch (Exception e) {
        e.printStackTrace();
        System.err.println("Error selecting data from table: " + table);
    }
}
```

### **Usage Example:**
```java
qb.select("Customer", "first_name", "last_name", "email");
```

---

## 4. Update Data
Modifies existing records in a table.

```java
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
```

### **Usage Example:**
```java
Map<String, Object> updateValues = new HashMap<>();
updateValues.put("email", "newemail@example.com");
qb.update("Customer", updateValues, "first_name", "Farman");
```

---

## 5. Delete Data
Removes records from a table based on a condition.

```java
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
```

### **Usage Example:**
```java
qb.delete("Customer", "first_name", "Farman");
```

---

## 6. Main Method (Testing the QueryBuilder)
```java
public static void main(String[] args) {
    QueryBuilder qb = new QueryBuilder();

    // Insert Example
    qb.insert("Customer", insertValues);

    // Select Example
    qb.select("Customer", "first_name", "last_name", "email");

    // Update Example
    qb.update("Customer", updateValues, "first_name", "Farman");

    // Delete Example
    qb.delete("Customer", "first_name", "Farman");

    // Close Connection
    qb.closeConnection();
}
```

In **jOOQ**, you can use SQL keywords like `WHERE`, `LIMIT`, `IN`, and many others by leveraging its fluent API. Below are the essential **SQL clauses** you might need when working with `QueryBuilder` in **jOOQ**.

---

## **1. WHERE Clause**
The `WHERE` clause filters records based on conditions.

### **Example (Simple WHERE)**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .where(DSL.field("email").eq("james1234@gmail.com"))
    .fetch();
```

### **Example (WHERE with Multiple Conditions)**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .where(DSL.field("first_name").eq("Farman")
        .and(DSL.field("last_name").eq("Othman")))
    .fetch();
```

---

## **2. LIMIT Clause**
Limits the number of rows returned.

### **Example**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .limit(5) // Get only 5 records
    .fetch();
```

---

## **3. IN Clause**
Filters records where a column matches any value in a given list.

### **Example**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .where(DSL.field("email").in("james1234@gmail.com", "newemail@example.com"))
    .fetch();
```

---

## **4. ORDER BY Clause**
Sorts results in ascending (`ASC`) or descending (`DESC`) order.

### **Example**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .orderBy(DSL.field("first_name").asc()) // Sort by first_name in ascending order
    .fetch();
```

---

## **5. GROUP BY Clause**
Groups results based on column values.

### **Example**
```java
Result<Record> result = create.select(DSL.field("last_name"), DSL.count())
    .from(DSL.table("Customer"))
    .groupBy(DSL.field("last_name"))
    .fetch();
```

---

## **6. JOIN Clause**
Used to combine rows from multiple tables.

### **Example (INNER JOIN)**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .join(DSL.table("Booking"))
    .on(DSL.field("Customer.id").eq(DSL.field("Booking.customer_id")))
    .fetch();
```

### **Example (LEFT JOIN)**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .leftJoin(DSL.table("Booking"))
    .on(DSL.field("Customer.id").eq(DSL.field("Booking.customer_id")))
    .fetch();
```

---

## **7. BETWEEN Clause**
Filters records within a range.

### **Example**
```java
Result<Record> result = create.select()
    .from(DSL.table("Ticket"))
    .where(DSL.field("price").between(50).and(100))
    .fetch();
```

---

## **8. EXISTS Clause**
Checks if a subquery returns any results.

### **Example**
```java
boolean exists = create.fetchExists(
    create.selectOne()
        .from(DSL.table("Customer"))
        .where(DSL.field("email").eq("james1234@gmail.com"))
);
```

---

## **9. LIKE Clause**
Filters records using pattern matching (`%` for any characters, `_` for a single character).

### **Example**
```java
Result<Record> result = create.select()
    .from(DSL.table("Customer"))
    .where(DSL.field("email").like("%gmail.com")) // Ends with gmail.com
    .fetch();
```

---

## **10. DISTINCT Clause**
Returns unique values.

### **Example**
```java
Result<Record> result = create.selectDistinct(DSL.field("email"))
    .from(DSL.table("Customer"))
    .fetch();
```

---

## **11. COUNT Function**
Counts the number of rows.

### **Example**
```java
int count = create.fetchCount(DSL.table("Customer"));
```

---

## **12. EXISTS with Subquery**
Check if a value exists in another table.

### **Example**
```java
boolean exists = create.fetchExists(
    create.selectOne()
        .from(DSL.table("Booking"))
        .where(DSL.field("customer_id").eq(1))
);
```

---

## **13. CASE WHEN (Conditional Querying)**
Used for conditional expressions.

### **Example**
```java
Result<Record> result = create.select(
    DSL.field("first_name"),
    DSL.when(DSL.field("age").gt(18), "Adult")
        .otherwise("Minor").as("status")
).from(DSL.table("Customer")).fetch();
```

---

## **14. UNION and UNION ALL**
Combines results from multiple queries.

### **Example**
```java
Result<Record> result = create.select(DSL.field("first_name"))
    .from(DSL.table("Customer"))
    .union(
        create.select(DSL.field("manager_name"))
            .from(DSL.table("Manager"))
    ).fetch();
```

---

## **15. DELETE Query**
Deletes rows based on a condition.

### **Example**
```java
create.deleteFrom(DSL.table("Customer"))
    .where(DSL.field("email").eq("james1234@gmail.com"))
    .execute();
```

---

## **16. UPDATE Query**
Updates specific values in a row.

### **Example**
```java
create.update(DSL.table("Customer"))
    .set(DSL.field("email"), "newemail@example.com")
    .where(DSL.field("first_name").eq("Farman"))
    .execute();
```

