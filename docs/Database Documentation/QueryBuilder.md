---

# EventEase Project: QueryBuilder Developer Guide

## Overview
This guide will help developers understand how to use and extend the `QueryBuilder` class for managing SQL queries in the EventEase project. It includes best practices, instructions on how to work with the class, and what precautions should be taken to prevent errors, especially when executing `UPDATE` and `DELETE` queries.

---

## Table of Contents
1. [General Instructions](#general-instructions)
2. [Working with QueryBuilder Methods](#working-with-querybuilder-methods)
3. [Security Considerations](#security-considerations)
4. [Error Handling and Logging](#error-handling-and-logging)
5. [Extending the QueryBuilder](#extending-the-querybuilder)
6. [Safeguards for UPDATE and DELETE Queries](#safeguards-for-update-and-delete-queries)
7. [How to Test QueryBuilder Queries](#how-to-test-querybuilder-queries)
8. [Common Pitfalls and How to Avoid Them](#common-pitfalls-and-how-to-avoid-them)
9. [Conclusion](#conclusion)

---

## General Instructions

- **Use `QueryBuilder` for all SQL queries**: All database interactions within EventEase should be done via the `QueryBuilder` to ensure that queries are built in a safe, consistent, and reusable way.
  
- **Do not write raw SQL queries directly**: Writing raw SQL queries directly bypasses the safeguards and validations that the `QueryBuilder` offers, especially against SQL injection.

---

## Working with QueryBuilder Methods

1. **Building Queries**:
    - Start by using the `select()`, `from()`, `where()`, and other methods to build your SQL query.
    - Example:
      ```java
      QueryBuilder qb = new QueryBuilder();
      qb.select("id", "name")
        .from("CUSTOMER")
        .where("status = ?", "active");
      ```

2. **Joining Tables**:
    - Use `join()`, `leftJoin()`, or `rightJoin()` to join tables. Ensure you're joining valid tables as per the schema.
    - Example:
      ```java
      qb.leftJoin("BOOKING", "CUSTOMER.id = BOOKING.customer_id");
      ```

3. **Adding Conditions**:
    - Use `andCondition()` and `orCondition()` to add `AND` or `OR` clauses to your query.
    - Example:
      ```java
      qb.andCondition("age > ?", 18);
      qb.orCondition("name LIKE ?", "%John%");
      ```

4. **Limit and Offset**:
    - Use `limit()` and `offset()` methods for pagination in queries.
    - Example:
      ```java
      qb.limit(10).offset(0);
      ```

5. **Aggregation**:
    - Use `aggregate()` to perform SQL aggregate functions like `COUNT()`, `SUM()`, etc.
    - Example:
      ```java
      qb.aggregate("COUNT", "id");
      ```

---

## Security Considerations

1. **Table and Column Validation**:
    - The `QueryBuilder` class validates table names and column names to prevent SQL injection.
    - Always make sure that table and column names are valid by referencing the pre-approved lists in the `QueryBuilder` class.
    - Example: `validateTable("CUSTOMER")` ensures that only valid tables are used.

2. **SQL Injection Prevention**:
    - Use parameterized queries via `PreparedStatement` to safely insert user input into SQL queries.
    - Avoid concatenating user inputs directly into SQL strings.
    - Example:
      ```java
      qb.where("status = ?", "active");
      ```

3. **Prepared Statements**:
    - Always use `PreparedStatement` for executing queries to automatically handle escaping of input data.

---

## Error Handling and Logging

1. **Error Handling**:
    - The `QueryBuilder` class includes logging of SQL exceptions in the `executeUpdate()` and `executeQuery()` methods.
    - Make sure to catch and log errors properly when executing queries to ensure that issues are logged and easy to debug.

2. **Logging**:
    - Log every executed query (both successful and failed) for auditing and troubleshooting purposes.
    - The logger provides details on the executed query and its parameters for transparency.

---

## Extending the QueryBuilder

1. **Adding Custom Methods**:
    - If your feature requires new SQL functionalities, feel free to add custom methods to the `QueryBuilder`.
    - Always validate input (tables, columns, etc.) before appending them to the query to prevent invalid data and SQL injection.
    
2. **Using Subqueries**:
    - You can add subqueries inside your `WHERE` clauses using `whereSubquery()`.
    - Example:
      ```java
      QueryBuilder subQuery = new QueryBuilder();
      subQuery.select("id").from("EVENT").where("status = ?", "active");
      qb.whereSubquery("event_id", "IN", subQuery);
      ```

---

## Safeguards for UPDATE and DELETE Queries

1. **Confirmation for Dangerous Queries**:
    - Both `UPDATE` and `DELETE` queries need confirmation before running if they don't contain a `WHERE` clause.
    - The `confirmDangerousQuery()` method ensures that the developer is prompted with a warning message.
    
2. **Ensure `WHERE` Clauses Are Present**:
    - Always include a `WHERE` clause in `UPDATE` or `DELETE` queries to avoid accidental updates or deletions across the entire table.
    - Example:
      ```java
      qb.update("CUSTOMER")
        .set("status", "inactive")
        .where("status = ?", "active");
      ```

3. **Run Confirmation on Critical Queries**:
    - If a query is likely to affect a large number of records (or the entire table), the user will be asked for confirmation.
    - Example:
      ```java
      System.out.println("You are about to delete all records from CUSTOMER. Are you sure? (yes/no)");
      ```

---

## How to Test QueryBuilder Queries

1. **Unit Testing**:
    - Write unit tests for all database queries using mock databases (e.g., H2) to ensure queries are correct.
    - Focus on testing edge cases, like empty results, no `WHERE` clauses in `UPDATE` or `DELETE`, and invalid table/column names.

2. **Testing Safeguards**:
    - Test the confirmation prompts for dangerous queries to ensure they are triggered correctly.
    - Ensure no query can execute without proper validation.

---

## Common Pitfalls and How to Avoid Them

1. **Missing WHERE Clause**:
    - Always ensure that you include a `WHERE` clause in `UPDATE` and `DELETE` queries to avoid modifying or deleting the entire table.

2. **Invalid Table/Column Names**:
    - Be mindful of the valid table and column names defined in the `QueryBuilder`. Invalid names can lead to `IllegalArgumentException`.

3. **Hardcoding SQL Queries**:
    - Do not write SQL queries manually. Always use `QueryBuilder` to ensure queries are built securely and consistently.

4. **Not Handling Exceptions**:
    - Always handle SQL exceptions properly and log them for easy troubleshooting.

---

## Conclusion

By following the guidelines and best practices in this document, developers will be able to interact with the `QueryBuilder` class in a safe and consistent manner. Always remember to validate inputs, use prepared statements, and avoid running risky queries without proper checks.

If you have any questions or need further clarification, feel free to reach out!

---

**End of Guide**