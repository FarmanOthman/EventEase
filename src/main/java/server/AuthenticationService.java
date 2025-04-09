package server;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

import database.QueryBuilder;

public class AuthenticationService {

    public static boolean authenticate(String username, String password) {
        // Validate inputs
        if (!InputValidator.isValidString(username) || !InputValidator.isValidString(password)) {
            return false;
        }

        QueryBuilder queryBuilder = new QueryBuilder();
        Map<String, Object> filters = new HashMap<>();
        filters.put("username", username);

        try {
            List<Map<String, Object>> results = queryBuilder.selectWithFilters("ADMIN", filters,
                    new String[] { "password" });

            if (!results.isEmpty()) {
                String hashedPassword = (String) results.get(0).get("password"); // Get hashed password from DB

                // Ensure valid bcrypt hash before checking
                if (hashedPassword != null && hashedPassword.startsWith("$2a$")) {
                    return BCrypt.checkpw(password, hashedPassword);
                }
                return false; // Return false if the hash is invalid
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            queryBuilder.closeConnection();
        }
        return false;
    }

    public static boolean register(String username, String password, int roleId, String email) {
        // Validate all inputs
        if (!InputValidator.isValidString(username) ||
                !InputValidator.isStrongPassword(password) ||
                !InputValidator.isValidEmail(email)) {
            return false;
        }

        // Hash the password using BCrypt
        String hashedPassword = hashPassword(password);

        QueryBuilder queryBuilder = new QueryBuilder();
        Map<String, Object> values = new HashMap<>();
        values.put("username", username);
        values.put("password", hashedPassword);
        values.put("email", email);
        values.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
        values.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));

        try {
            queryBuilder.insert("ADMIN", values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            queryBuilder.closeConnection();
        }
    }

    // Helper method to hash a password using BCrypt
    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // 12 rounds for security
    }
}
