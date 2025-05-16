# AuthenticationServer

## Overview
The `AuthenticationServer` class is responsible for handling user authentication and authorization in the application. It provides functionality for user login, registration, and role management.

## Class Structure

### Main Class: AuthenticationServer
This class handles all authentication and authorization operations, interacting with the database to validate user credentials and manage user sessions.

### Enum: UserRole
An enum that defines the possible user roles within the system.

- `ADMIN`: Administrator role with full privileges
- `MANAGER`: Manager role with limited privileges
- `UNKNOWN`: Default role for unauthenticated users

### Static Fields
- `currentUsername`: Stores the name of the currently logged-in user
- `currentUserRole`: Stores the role of the currently logged-in user (defaults to UNKNOWN)

### Methods

#### authenticate
```java
public static boolean authenticate(String username, String password)
```
Authenticates a user by checking their credentials against both the ADMIN and MANAGER tables in the database.

- **Parameters**:
  - `username`: The username to authenticate
  - `password`: The password to verify
- **Returns**: `true` if authentication succeeds, `false` otherwise
- **Implementation Details**:
  - First tries to authenticate against the ADMIN table
  - If unsuccessful, tries to authenticate against the MANAGER table
  - If successful in either case, updates currentUsername and currentUserRole
  - Uses BCrypt for password verification

#### authenticateUser (private)
```java
private static boolean authenticateUser(String username, String password, String tableName)
```
Helper method that performs the actual authentication against a specific table.

- **Parameters**:
  - `username`: The username to authenticate
  - `password`: The password to verify
  - `tableName`: The database table to check (ADMIN or MANAGER)
- **Returns**: `true` if authentication succeeds, `false` otherwise
- **Implementation Details**:
  - Uses SQL query to find the user by username
  - Retrieves the hashed password from the database
  - Validates the hashed password format (BCrypt)
  - Uses BCrypt's checkpw method to verify the password

#### getCurrentUserRole
```java
public static UserRole getCurrentUserRole()
```
Gets the role of the currently logged-in user.

- **Returns**: The UserRole enum value representing the current user's role

#### getCurrentUsername
```java
public static String getCurrentUsername()
```
Gets the username of the currently logged-in user.

- **Returns**: The current username or null if no user is logged in

#### logout
```java
public static void logout()
```
Logs out the current user by resetting the currentUsername and currentUserRole.

#### register
```java
public static boolean register(String username, String password, int roleId, String email)
```
Registers a new administrator user in the system.

- **Parameters**:
  - `username`: The username for the new user
  - `password`: The password for the new user (will be hashed)
  - `roleId`: The role ID for the new user
  - `email`: The email address for the new user
- **Returns**: `true` if registration succeeds, `false` otherwise
- **Implementation Details**:
  - Hashes the password using BCrypt
  - Inserts a new record into the ADMIN table
  - Sets timestamps for created_at and updated_at

#### hashPassword (private)
```java
private static String hashPassword(String password)
```
Helper method to hash a password using BCrypt.

- **Parameters**:
  - `password`: The plaintext password to hash
- **Returns**: A BCrypt hash of the password with 12 rounds of salting

## Usage Example
```java
// User login
boolean loginSuccessful = AuthenticationServer.authenticate("admin", "password123");
if (loginSuccessful) {
    // Get current user's role
    AuthenticationServer.UserRole role = AuthenticationServer.getCurrentUserRole();
    
    // Check if user is an admin
    if (role == AuthenticationServer.UserRole.ADMIN) {
        // Perform admin-specific operations
    }
    
    // Log out the user when done
    AuthenticationServer.logout();
}

// Register a new admin user
boolean registrationSuccessful = AuthenticationServer.register("newadmin", "StrongPassword123", 1, "admin@example.com");
```

## Dependencies
- `java.sql.Connection`: For database connections
- `java.sql.PreparedStatement`: For prepared SQL statements
- `java.sql.ResultSet`: For query results
- `java.sql.SQLException`: For SQL exception handling
- `java.sql.Timestamp`: For timestamp generation
- `java.time.LocalDateTime`: For date/time operations
- `org.mindrot.jbcrypt.BCrypt`: For password hashing and verification
- `database.Database`: For database connection management
