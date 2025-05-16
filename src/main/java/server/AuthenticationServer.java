package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import org.mindrot.jbcrypt.BCrypt;

import database.Database;

public class AuthenticationServer {

    // Enum to represent user roles
    public enum UserRole {
        ADMIN,
        MANAGER,
        UNKNOWN
    }

    // Stores the currently logged in username
    private static String currentUsername = null;
    // Stores the currently logged in user's role
    private static UserRole currentUserRole = UserRole.UNKNOWN;

    public static boolean authenticate(String username, String password) {
        // First check in ADMIN table
        if (authenticateUser(username, password, "ADMIN")) {
            currentUsername = username;
            currentUserRole = UserRole.ADMIN;
            return true;
        }

        // Then check in MANAGER table if not found in ADMIN
        if (authenticateUser(username, password, "MANAGER")) {
            currentUsername = username;
            currentUserRole = UserRole.MANAGER;
            return true;
        }

        // Authentication failed
        currentUsername = null;
        currentUserRole = UserRole.UNKNOWN;
        return false;
    }

    private static boolean authenticateUser(String username, String password, String tableName) {
        String query = "SELECT password FROM " + tableName + " WHERE username = ?";

        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password"); // Get hashed password from DB

                // Ensure valid bcrypt hash before checking
                if (hashedPassword != null && hashedPassword.startsWith("$2a$")) {
                    return BCrypt.checkpw(password, hashedPassword);
                }
                return false; // Return false if the hash is invalid
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get current user's role
    public static UserRole getCurrentUserRole() {
        return currentUserRole;
    }

    // Get current username
    public static String getCurrentUsername() {
        return currentUsername;
    }

    // Logout method to reset current user
    public static void logout() {
        currentUsername = null;
        currentUserRole = UserRole.UNKNOWN;
    }

    public static boolean register(String username, String password, int roleId, String email) {
        String query = "INSERT INTO ADMIN (username, password, email, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        // Hash the password using BCrypt
        String hashedPassword = hashPassword(password);

        try (Connection connection = Database.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, hashedPassword); // Store the hashed password
            statement.setString(3, email);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now())); // created_at
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // updated_at

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Helper method to hash a password using BCrypt
    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // 12 rounds for security
    }
}

