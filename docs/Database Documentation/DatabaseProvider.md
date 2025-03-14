# DatabaseProvider.java Guide

## Overview
The `DatabaseProvider` class is responsible for setting up an SQLite database for the EventEase project. It checks whether the database file exists and, if not, creates it and executes the schema defined in `Schema.sql`.

## Features
- Checks if the database file exists.
- If the database does not exist:
  - Creates a new SQLite database.
  - Reads SQL commands from `Schema.sql`.
  - Executes SQL commands to initialize the database schema.
- If an error occurs during execution, deletes the database file to maintain integrity.
- Closes all resources properly to prevent memory leaks.

## File Structure
```
EventEase/
│── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── database/
│   │   │   │   ├── DatabaseProvider.java
│   │   │   │   ├── Schema.sql
│   ├── resources/
│       ├── EventEase.db (created automatically)
```

## Prerequisites
Ensure you have the following before running the program:
- Java Development Kit (JDK) installed.
- SQLite JDBC driver (included in JDK 8+).
- The `Schema.sql` file in the correct path (`src/main/java/database/`).

## Code Walkthrough

### 1. Defining File Paths
```java
String sqlFilePath = "src\\main\\java\\database\\Schema.sql";
String dbFilePath = "src\\main\\resources\\EventEase.db";
```
- Specifies the location of the database schema file and database file.

### 2. Checking Database Existence
```java
File dbFile = new File(dbFilePath);
if (!dbFile.exists()) {
```
- Checks if the database file already exists to prevent overwriting existing data.

### 3. Establishing Connection and Executing SQL
```java
connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
statement = connection.createStatement();
```
- Establishes a connection to the SQLite database.
- Creates a `Statement` object to execute SQL commands.

### 4. Reading and Executing SQL Commands
```java
BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath));
StringBuilder sqlCommands = new StringBuilder();

while ((line = reader.readLine()) != null) {
    sqlCommands.append(line).append("\n");
}
reader.close();
statement.executeUpdate(sqlCommands.toString());
```
- Reads the SQL schema file line by line.
- Executes the combined SQL commands to create the required tables.

### 5. Error Handling & Cleanup
```java
} catch (SQLException | IOException e) {
    e.printStackTrace();
    if (dbFile.exists() && dbFile.delete()) {
        System.out.println("Database file deleted due to error.");
    }
```
- Handles potential errors and deletes the database file if the setup fails.

### 6. Closing Resources
```java
finally {
    if (statement != null) statement.close();
    if (connection != null) connection.close();
}
```
- Ensures proper resource management by closing the database connection and statement.

## Running the Program
1. Navigate to the project root directory.
2. Compile and run `DatabaseProvider.java`:
   ```sh
   javac src/main/java/database/DatabaseProvider.java
   java -cp src/main/java database.DatabaseProvider
   ```
3. If successful, `EventEase.db` will be created in `src/main/resources/`.
4. If the database file already exists, no changes will be made.

## Expected Output
- If the database is created successfully:
  ```
  Executing SQL commands:
  (SQL commands from Schema.sql)
  Database created successfully.
  ```
- If the database already exists:
  ```
  Database file already exists. No action needed.
  ```
- If an error occurs:
  ```
  java.sql.SQLException: (error details)
  Database file deleted due to error.
  ```

## Troubleshooting
- Ensure `Schema.sql` exists and contains valid SQL statements.
- Verify the file paths are correct.
- Check if you have the necessary permissions to create files in the specified directory.

## Conclusion
The `DatabaseProvider` class efficiently sets up and manages the SQLite database for EventEase. It ensures that the database schema is only executed when necessary and handles errors gracefully by removing incomplete files. This makes it a reliable way to initialize the database for the project.

