package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
  public static final String DB_URL;

  static {
    String url = null;
    try (InputStream input = Database.class.getClassLoader().getResourceAsStream("config.properties")) {
      if (input == null) {
        throw new RuntimeException("❌ config.properties file not found in resources.");
      }
      Properties prop = new Properties();
      prop.load(input);
      url = prop.getProperty("db.url");
    } catch (Exception e) {
      System.err.println("❌ Failed to load database configuration.");
      e.printStackTrace();
      System.exit(1); // Stop execution if config fails to load
    }
    DB_URL = url; // Assign after successful loading
  }

  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(DB_URL);
    } catch (SQLException e) {
      System.err.println("❌ Failed to establish connection!");
      e.printStackTrace();
      return null;
    }
  }

  public static void connect() {
    try (Connection conn = getConnection()) {
      if (conn != null) {
        System.out.println("✅ Connection to SQLite has been established.");
      }
    } catch (SQLException e) {
      System.err.println("❌ Failed to connect to SQLite.");
      e.printStackTrace();
    }
  }

  // Main method for testing
}
