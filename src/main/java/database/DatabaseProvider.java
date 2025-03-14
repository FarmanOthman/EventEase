package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseProvider {
  public static void main(String[] args) {
    String sqlFilePath = "src\\main\\java\\database\\Schema.sql";
    String dbFilePath = "src\\main\\resources\\EventEase.db";

    Connection connection = null;
    Statement statement = null;

    try {
      File dbFile = new File(dbFilePath);
      if (!dbFile.exists()) {
        // Establish a database connection
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        statement = connection.createStatement();

        // Read SQL file
        BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath));
        String line;
        StringBuilder sqlCommands = new StringBuilder();

        while ((line = reader.readLine()) != null) {
          sqlCommands.append(line).append("\n");
        }
        reader.close();

        // Debugging output
        System.out.println("Executing SQL commands:\n" + sqlCommands.toString());

        // Execute SQL commands
        statement.executeUpdate(sqlCommands.toString());
        System.out.println("Database created successfully.");
      } else {
        System.out.println("Database file already exists. No action needed.");
      }
    } catch (SQLException | IOException e) {
      e.printStackTrace();
      // Delete the .db file if an error occurs
      File dbFile = new File(dbFilePath);
      if (dbFile.exists()) {
        if (dbFile.delete()) {
          System.out.println("Database file deleted due to error.");
        } else {
          System.out.println("Failed to delete the database file.");
        }
      }
    } finally {
      // Close resources
      try {
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
