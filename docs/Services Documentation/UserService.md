# UserService

## Overview
The `UserService` class connects the UI with the authentication server and provides methods for user management operations. It implements the singleton pattern to ensure a single instance throughout the application and handles user authentication, registration, profile management, and role-based access control.

## Class Structure

### Main Class: UserService
This class provides user management functionality and acts as a bridge between the UI and authentication server.

#### Fields
- `instance`: Static instance for the singleton pattern

#### Constructor
- `UserService()`: Private constructor for the singleton pattern.

#### Methods

##### getInstance
```java
public static synchronized UserService getInstance()
```
Gets the singleton instance of UserService.

- **Returns**: The UserService singleton instance
- **Implementation Details**:
  - Uses lazy initialization to create the instance only when needed
  - Thread-safe with synchronized keyword

##### getAllUsers
```java
public List<Map<String, Object>> getAllUsers(UserRole role)
```
Gets all users based on role.

- **Parameters**:
  - `role`: The role to filter by (ADMIN or MANAGER)
- **Returns**: List of user data as maps
- **Implementation Details**:
  - Selects the appropriate table based on role
  - Uses QueryBuilder to fetch users from the database
  - Adds role information to each user record
  - Properly closes the database connection

##### addUser
```java
public boolean addUser(String username, String password, String email, UserRole role)
```
Adds a new user to the database.

- **Parameters**:
  - `username`: Username for the new user
  - `password`: Plain text password (will be hashed)
  - `email`: Email address for the new user
  - `role`: User role (ADMIN or MANAGER)
- **Returns**: True if user was added successfully, false otherwise
- **Implementation Details**:
  - Validates input parameters
  - Checks if username already exists
  - Hashes the password using BCrypt
  - Sets up values for database insertion
  - Adds timestamps for created_at and updated_at

##### deleteUser
```java
public boolean deleteUser(String username, UserRole role)
```
Deletes a user from the database.

- **Parameters**:
  - `username`: Username of the user to delete
  - `role`: Role of the user to delete
- **Returns**: True if user was deleted successfully, false otherwise
- **Implementation Details**:
  - Selects the appropriate table based on role
  - Uses QueryBuilder to delete the user
  - Verifies that the user exists before deletion
  - Prevents deletion of the last admin user

##### updateUser
```java
public boolean updateUser(String username, String newEmail, String newPassword, UserRole role)
```
Updates a user's information.

- **Parameters**:
  - `username`: Username of the user to update
  - `newEmail`: New email address (null if unchanged)
  - `newPassword`: New password (null if unchanged)
  - `role`: Role of the user
- **Returns**: True if user was updated successfully, false otherwise
- **Implementation Details**:
  - Selects the appropriate table based on role
  - Creates a map of values to update
  - Updates password only if provided (with hashing)
  - Updates email only if provided
  - Sets updated_at timestamp

##### authenticate
```java
public boolean authenticate(String username, String password)
```
Authenticates a user with username and password.

- **Parameters**:
  - `username`: Username to authenticate
  - `password`: Password to verify
- **Returns**: True if authentication succeeds, false otherwise
- **Implementation Details**:
  - Delegates to AuthenticationServer.authenticate
  - Returns the authentication result

##### getCurrentUser
```java
public String getCurrentUser()
```
Gets the currently logged-in username.

- **Returns**: The current username or null if not logged in
- **Implementation Details**:
  - Delegates to AuthenticationServer.getCurrentUsername

##### getCurrentUserRole
```java
public UserRole getCurrentUserRole()
```
Gets the role of the currently logged-in user.

- **Returns**: The current user's role
- **Implementation Details**:
  - Delegates to AuthenticationServer.getCurrentUserRole

##### logout
```java
public void logout()
```
Logs out the current user.

- **Implementation Details**:
  - Delegates to AuthenticationServer.logout

##### getUserInfo
```java
public Map<String, Object> getUserInfo(String username, UserRole role)
```
Gets detailed information for a specific user.

- **Parameters**:
  - `username`: Username of the user to fetch
  - `role`: Role of the user
- **Returns**: Map containing user information or null if not found
- **Implementation Details**:
  - Selects the appropriate table based on role
  - Creates a filter map with the username
  - Fetches user details including email and timestamps

##### changePassword
```java
public boolean changePassword(String username, String currentPassword, String newPassword, UserRole role)
```
Changes a user's password.

- **Parameters**:
  - `username`: Username of the user
  - `currentPassword`: Current password for verification
  - `newPassword`: New password to set
  - `role`: Role of the user
- **Returns**: True if password was changed successfully, false otherwise
- **Implementation Details**:
  - Verifies the current password
  - Hashes the new password
  - Updates the password in the database
  - Sets the updated_at timestamp

##### usernameExists
```java
public boolean usernameExists(String username)
```
Checks if a username already exists in either the ADMIN or MANAGER table.

- **Parameters**:
  - `username`: Username to check
- **Returns**: True if username exists, false otherwise
- **Implementation Details**:
  - Checks the ADMIN table for the username
  - If not found, checks the MANAGER table
  - Uses direct SQL queries with proper resource management

##### register
```java
public boolean register(String username, String password, int roleId, String email)
```
Registers a new user.

- **Parameters**:
  - `username`: Username for the new user
  - `password`: Password for the new user
  - `roleId`: Role ID for the new user
  - `email`: Email address for the new user
- **Returns**: True if registration succeeds, false otherwise
- **Implementation Details**:
  - Delegates to AuthenticationServer.register

##### getUserCount
```java
public int getUserCount(UserRole role)
```
Gets the count of users for a specific role.

- **Parameters**:
  - `role`: The role to count users for
- **Returns**: The number of users with the specified role
- **Implementation Details**:
  - Selects the appropriate table based on role
  - Uses QueryBuilder to count users
  - Properly handles exceptions and connection closing

## Usage Example
```java
// Get the UserService instance
UserService userService = UserService.getInstance();

// Authenticate a user
boolean loginSuccess = userService.authenticate("admin", "password123");
if (loginSuccess) {
    // Get current user's information
    String username = userService.getCurrentUser();
    UserRole role = userService.getCurrentUserRole();
    
    System.out.println("Logged in as: " + username);
    System.out.println("Role: " + role);
    
    // Get all admin users
    List<Map<String, Object>> admins = userService.getAllUsers(UserRole.ADMIN);
    System.out.println("Total admins: " + admins.size());
    
    // Add a new manager
    boolean addSuccess = userService.addUser(
        "newmanager", 
        "Manager123", 
        "manager@example.com", 
        UserRole.MANAGER
    );
    
    // Update user email
    boolean updateSuccess = userService.updateUser(
        "newmanager",
        "updated.manager@example.com",
        null,  // Don't change password
        UserRole.MANAGER
    );
    
    // Change user password
    boolean passwordChanged = userService.changePassword(
        "newmanager",
        "Manager123",  // Current password
        "NewPassword456",  // New password
        UserRole.MANAGER
    );
    
    // Log out
    userService.logout();
}

// Register a new user
boolean registerSuccess = userService.register(
    "newuser",
    "Password123",
    2,  // Role ID for manager
    "newuser@example.com"
);
```

## Dependencies
- `server.AuthenticationServer`: Server component for authentication
- `server.AuthenticationServer.UserRole`: Enum for user roles
- `org.mindrot.jbcrypt.BCrypt`: For password hashing
- `java.sql.*`: For database operations
- `java.time.LocalDateTime`: For timestamp operations
- `java.util.*`: For collections and data structures
- `database.Database`: For database connectivity
- `database.QueryBuilder`: For database queries
