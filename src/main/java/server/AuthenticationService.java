package server; // You need to add package 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import org.mindrot.jbcrypt.BCrypt;


import database.Database;

public class AuthenticationService {

    public static boolean authenticate(String username, String password) {
        String query = "SELECT password FROM ADMIN WHERE username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password"); // Get hashed password from DB
                
                // Compare entered password with hashed password
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean register(String username, String password, int roleId, String email) {
        String query = "INSERT INTO ADMIN (username, password, role_id, email, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";

        // Hash the password using BCrypt
        String hashedPassword = hashPassword(password);
        
        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, hashedPassword); // Store the hashed password
            statement.setInt(3, roleId);
            statement.setString(4, email);
            statement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now())); // created_at
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now())); // updated_at

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