package services;

import server.AuthenticationServer;
import server.AuthenticationServer.UserRole;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import database.Database;
import database.QueryBuilder;

/**
 * Service class to connect the UI with the authentication server.
 * Provides methods for user management operations.
 */
public class UserService {
  private static UserService instance;

  /**
   * Private constructor for singleton pattern
   */
  private UserService() {
    // Private constructor for singleton
  }

  /**
   * Get singleton instance of UserService
   * 
   * @return The UserService instance
   */
  public static synchronized UserService getInstance() {
    if (instance == null) {
      instance = new UserService();
    }
    return instance;
  }

  /**
   * Get all users based on role
   * 
   * @param role The role to filter by (ADMIN or MANAGER)
   * @return List of user data as maps
   */
  public List<Map<String, Object>> getAllUsers(UserRole role) {
    String tableName = role == UserRole.ADMIN ? "ADMIN" : "MANAGER";
    QueryBuilder queryBuilder = new QueryBuilder();

    try {
      List<Map<String, Object>> users = queryBuilder.select(tableName, "username", "email", "created_at", "updated_at");
      // Add role information to each user
      for (Map<String, Object> user : users) {
        user.put("role", role.toString());
      }
      return users;
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    } finally {
      queryBuilder.closeConnection();
    }
  }

  /**
   * Add a new user to the database
   * 
   * @param username Username
   * @param password Plain text password
   * @param email    Email address
   * @param role     User role (ADMIN or MANAGER)
   * @return True if operation was successful
   */
  public boolean addUser(String username, String password, String email, UserRole role) {
    if (username == null || password == null || email == null || role == null) {
      return false;
    }

    String tableName = role == UserRole.ADMIN ? "ADMIN" : "MANAGER";
    QueryBuilder queryBuilder = new QueryBuilder();

    try {
      // Check if username already exists in either table
      if (usernameExists(username)) {
        return false;
      }

      // Hash the password
      String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

      // Set up values for insertion
      Map<String, Object> values = new HashMap<>();
      values.put("username", username);
      values.put("password", hashedPassword);
      values.put("email", email);
      values.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
      values.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));

      // Insert the user
      queryBuilder.insert(tableName, values);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      queryBuilder.closeConnection();
    }
  }

  /**
   * Update user information
   * 
   * @param username    Username to update
   * @param newEmail    New email (null to leave unchanged)
   * @param newPassword New password (null to leave unchanged)
   * @param role        User role
   * @return True if operation was successful
   */
  public boolean updateUser(String username, String newEmail, String newPassword, UserRole role) {
    String tableName = role == UserRole.ADMIN ? "ADMIN" : "MANAGER";
    QueryBuilder queryBuilder = new QueryBuilder();

    try {
      // Check if user exists first
      Map<String, Object> filters = new HashMap<>();
      filters.put("username", username);
      List<Map<String, Object>> users = queryBuilder.selectWithFilters(tableName, filters, new String[] { "username" });

      if (users.isEmpty()) {
        return false;
      }

      // Set up values to update
      Map<String, Object> updateValues = new HashMap<>();

      if (newEmail != null && !newEmail.isEmpty()) {
        updateValues.put("email", newEmail);
      }

      if (newPassword != null && !newPassword.isEmpty()) {
        // Hash the new password
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12));
        updateValues.put("password", hashedPassword);
      }

      // Only update if there's something to update
      if (!updateValues.isEmpty()) {
        updateValues.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));
        queryBuilder.update(tableName, updateValues, "username", username);
        return true;
      }

      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      queryBuilder.closeConnection();
    }
  }

  /**
   * Delete a user from the database
   * 
   * @param username Username to delete
   * @param role     User role
   * @return True if operation was successful
   */
  public boolean deleteUser(String username, UserRole role) {
    String tableName = role == UserRole.ADMIN ? "ADMIN" : "MANAGER";
    QueryBuilder queryBuilder = new QueryBuilder();

    try {
      queryBuilder.delete(tableName, "username", username);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    } finally {
      queryBuilder.closeConnection();
    }
  }

  /**
   * Check if a username exists in either ADMIN or MANAGER table
   * 
   * @param username Username to check
   * @return True if username exists
   */
  public boolean usernameExists(String username) {
    try (Connection connection = Database.getConnection()) {
      // Check ADMIN table
      String query = "SELECT COUNT(*) FROM ADMIN WHERE username = ?";
      try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next() && resultSet.getInt(1) > 0) {
          return true;
        }
      }

      // Check MANAGER table
      query = "SELECT COUNT(*) FROM MANAGER WHERE username = ?";
      try (PreparedStatement statement = connection.prepareStatement(query)) {
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next() && resultSet.getInt(1) > 0) {
          return true;
        }
      }

      return false;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Get current user info
   * 
   * @return Map containing current user info or null if not logged in
   */
  public Map<String, Object> getCurrentUserInfo() {
    String username = AuthenticationServer.getCurrentUsername();
    UserRole role = AuthenticationServer.getCurrentUserRole();

    if (username == null || role == UserRole.UNKNOWN) {
      return null;
    }

    String tableName = role == UserRole.ADMIN ? "ADMIN" : "MANAGER";
    QueryBuilder queryBuilder = new QueryBuilder();

    try {
      Map<String, Object> filters = new HashMap<>();
      filters.put("username", username);
      List<Map<String, Object>> results = queryBuilder.selectWithFilters(
          tableName,
          filters,
          new String[] { "username", "email", "created_at", "updated_at" });

      if (!results.isEmpty()) {
        Map<String, Object> userInfo = results.get(0);
        userInfo.put("role", role.toString());
        return userInfo;
      }

      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      queryBuilder.closeConnection();
    }
  }
}