package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryBuilder {
  public static final String DB_URL = Database.DB_URL; // Accessing DB_URL from Database class

  // Create
  public static void insertData(String name, int age) {
    String sql = "INSERT INTO users(name, age) VALUES(?, ?)";
    try (Connection conn = Database.getConnection(); // Use getConnection method
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, name);
      pstmt.setInt(2, age);
      pstmt.executeUpdate();
      System.out.println("✅ Data inserted successfully!");
    } catch (SQLException e) {
      System.err.println("❌ Insert failed!");
      e.printStackTrace();
    }
  }

  // Read
  public static void readData() {
    String sql = "SELECT * FROM users";
    try (Connection conn = Database.getConnection(); // Use getConnection method
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {
      while (rs.next()) {
        System.out.println("User: " + rs.getString("name") + ", Age: " + rs.getInt("age"));
      }
    } catch (SQLException e) {
      System.err.println("❌ Read failed!");
      e.printStackTrace();
    }
  }

  // Update
  public static void updateData(String name, int age) {
    String sql = "UPDATE users SET age = ? WHERE name = ?";
    try (Connection conn = Database.getConnection(); // Use getConnection method
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setInt(1, age);
      pstmt.setString(2, name);
      pstmt.executeUpdate();
      System.out.println("✅ Data updated successfully!");
    } catch (SQLException e) {
      System.err.println("❌ Update failed!");
      e.printStackTrace();
    }
  }

  // Delete
  public static void deleteData(String name) {
    String sql = "DELETE FROM users WHERE name = ?";
    try (Connection conn = Database.getConnection(); // Use getConnection method
        PreparedStatement pstmt = conn.prepareStatement(sql)) {
      pstmt.setString(1, name);
      pstmt.executeUpdate();
      System.out.println("✅ Data deleted successfully!");
    } catch (SQLException e) {
      System.err.println("❌ Delete failed!");
      e.printStackTrace();
    }
  }
}
