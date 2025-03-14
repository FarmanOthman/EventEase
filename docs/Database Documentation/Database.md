Got it! Here’s a similar style `.md` documentation for your `Database` class:

```markdown
# `Database` Class Documentation

## Overview
The `Database` class provides functionality to manage an SQLite database connection in the application. It loads the database URL from the configuration file and offers methods for establishing a connection to the database, retrieving the database path, and checking the connection status.

The class includes:
1. **Database URL** - Loaded from the `config.properties` file.
2. **Connection Management** - Establishes a connection to the SQLite database.
3. **Error Handling** - Logs and handles any errors related to database connection issues.

## Class Components

### 1. Logger Setup
The class uses a logger to track significant events, including the attempt to connect to the database and connection success or failure.

```java
private static final Logger logger = Logger.getLogger(Database.class.getName());
```

---

## Methods

### 1. `getConnection()`
**Description**:  
This method attempts to establish a connection to the SQLite database using the loaded database URL (`DB_URL`). If the connection is successful, it returns a `Connection` object that can be used for executing SQL queries. If the connection fails, it logs the error and returns `null`.

**Returns**:  
- `Connection`: A connection object for the SQLite database, or `null` if the connection fails.

**Usage**:
```java
Connection conn = Database.getConnection();
```

#### Method Workflow:
1. **Log Connection Attempt**:  
   Logs the attempt to connect to the database.
   
2. **Establish Connection**:  
   Uses the `DriverManager.getConnection(DB_URL)` to establish the database connection. If successful, returns the connection object.

3. **Error Logging**:  
   Logs any `SQLException` that occurs during the connection attempt.

---

### 2. `getDatabasePath()`
**Description**:  
This method retrieves the file path of the SQLite database by stripping the `jdbc:sqlite:` prefix from the `DB_URL`.

**Returns**:  
- `String`: The file path of the SQLite database, without the `jdbc:sqlite:` prefix.

**Usage**:
```java
String dbPath = Database.getDatabasePath();
```

#### Method Workflow:
1. **Extract Path**:  
   Strips the `jdbc:sqlite:` prefix from the `DB_URL` and returns the remaining path.

2. **Return Path**:  
   Returns the database file path for further use.

---

### 3. `connect()`
**Description**:  
This method attempts to establish a connection to the SQLite database and logs the result. If the connection is successful, it logs a success message; otherwise, it logs an error message.

**Returns**:  
- `void`

**Usage**:
```java
Database.connect();
```

#### Method Workflow:
1. **Log Attempt**:  
   Logs the attempt to connect to the database.

2. **Establish Connection**:  
   Uses the `getConnection()` method to try connecting to the database.

3. **Log Success/Failure**:  
   If successful, logs a success message; otherwise, logs the error and exits the application.

#### Error Handling:
- **SQLException**: Captured and logged if the connection fails.

---

## Logging
The `Database` class uses the `Logger` object to track significant actions and errors:
- **Connection Attempt**: Logs the attempt to connect to the database.
- **Connection Status**: Logs the success or failure of the database connection.
- **Error Logging**: If an error occurs (e.g., invalid database file, connection failure), the logger logs the error message.

Example log messages:
```
INFO: Attempting to connect to database: jdbc:sqlite:/path/to/database.db
SEVERE: Database connection failed: Unable to establish connection.
INFO: Database connection successful.
```

---

## Error Handling
The `Database` class handles the following errors:
- **Database File Not Found**: If the `config.properties` file cannot be found or read.
- **Connection Failure**: If the database URL is incorrect or the connection cannot be established.
- **SQLException**: Handles errors related to database interactions.

Errors are logged, and the program continues execution without crashing.

---

## How to Use

### 1. Setup the Database
Ensure that the `config.properties` file is placed in the `resources` folder and contains the `db.url` property with the SQLite connection URL.

Example of `config.properties`:
```
db.url=jdbc:sqlite:/path/to/database.db
```

### 2. Call the Methods
You can use the `Database.getConnection()` or `Database.connect()` methods to establish a connection to the database.

Example:
```java
Connection conn = Database.getConnection();
if (conn != null) {
    // Perform database operations
}
```

### 3. Verify the Database Path
You can also retrieve the database file path using the `Database.getDatabasePath()` method.

Example:
```java
String dbPath = Database.getDatabasePath();
System.out.println("Database Path: " + dbPath);
```

---

## Example Usage

Here’s an example of how to use the `Database` class in your application:

```java
package database;

public class DatabaseExample {
    public static void main(String[] args) {
        // Attempt to connect to the database
        Database.connect();

        // Retrieve the database path
        String dbPath = Database.getDatabasePath();
        System.out.println("Database path: " + dbPath);

        // Get a database connection
        Connection conn = Database.getConnection();
        if (conn != null) {
            System.out.println("Connection successful!");
        } else {
            System.err.println("Connection failed.");
        }
    }
}
```

---

## Additional Notes:
- Ensure the `config.properties` file is properly set up with the correct database URL.
- If the database connection fails, ensure the SQLite file path is correct and accessible.
- The `connect()` method also logs the success or failure of the connection, helping you identify issues.

---

## Conclusion
The `Database` class is a simple utility for managing the SQLite database connection in your application. It provides methods for connecting to the database, retrieving the database file path, and logging connection events. By following the usage instructions, developers can easily integrate this class into their projects for efficient database handling.
