package database;

import java.util.HashMap;
import java.util.Map;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.mindrot.jbcrypt.BCrypt;

public class CreateManagerUser {
  public static void main(String[] args) {
    try {
      // Ensure database and tables are created
      DatabaseProvider.main(null);

      // Create a new QueryBuilder instance
      QueryBuilder queryBuilder = new QueryBuilder();

      // Manager user details
      String username = "manager";
      String password = "manager123"; // Will be hashed
      String email = "manager@example.com";

      // Hash the password using BCrypt
      String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));

      // Create values map for insertion
      Map<String, Object> values = new HashMap<>();
      values.put("username", username);
      values.put("password", hashedPassword);
      values.put("email", email);
      values.put("created_at", Timestamp.valueOf(LocalDateTime.now()));
      values.put("updated_at", Timestamp.valueOf(LocalDateTime.now()));

      // Insert into MANAGER table
      queryBuilder.insert("MANAGER", values);

      System.out.println("Manager user created successfully!");
      System.out.println("Username: " + username);
      System.out.println("Password: " + password);

      // Close the connection
      queryBuilder.closeConnection();

    } catch (Exception e) {
      System.err.println("Error creating manager user: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
