package database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

  // Database URL loaded from the config.properties file
  public static final String DB_URL;

  static {
    String url = null;
    try (InputStream input = Database.class.getClassLoader().getResourceAsStream("config.properties")) {
      // Attempt to load the config file containing database URL
      if (input == null) {
        throw new RuntimeException("❌ config.properties file not found in resources.");
      }
      Properties prop = new Properties();
      prop.load(input);
      url = prop.getProperty("db.url"); // Read the db.url property
    } catch (Exception e) {
      // Handle exceptions if the properties file can't be found or loaded
      System.err.println("❌ Failed to load database configuration.");
      e.printStackTrace();
      System.exit(1); // Exit the program if the configuration can't be loaded
    }
    DB_URL = url; // Assign the database URL after successful loading
  }

  /**
   * Establishes a connection to the database using the URL.
   * 
   * @return a Connection object to the database, or null if the connection fails.
   */
  public static Connection getConnection() throws SQLException {
    // Attempt to get a connection using the provided DB URL
    return DriverManager.getConnection(DB_URL);
  }

  /**
   * Extracts and returns the database path from the DB URL.
   * 
   * @return the database path as a string
   */
  public static String getDatabasePath() {
    // Remove the "jdbc:sqlite:" prefix to extract just the file path
    return DB_URL.replace("jdbc:sqlite:", "");
  }

  /**
   * Establishes a connection to the database and prints a success message if
   * connected.
   */
  public static void connect() {
    try (Connection conn = getConnection()) {
      // Attempt to get a connection
      if (conn != null) {
        // If the connection is successful, print confirmation
        System.out.println("✅ Connection to SQLite has been established.");
      }
    } catch (SQLException e) {
      // If an exception occurs during connection, log the error
      System.err.println("❌ Failed to connect to SQLite.");
      e.printStackTrace();
    }
  }
}
