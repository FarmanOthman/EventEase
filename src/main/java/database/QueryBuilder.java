package database;

import java.sql.*;
import java.util.*;

public class QueryBuilder {
  public static Connection getConnection() throws SQLException {
    return Database.getConnection(); // Use existing Database connection method
  }

  // Insert Data Dynamically
  public static void insert(String table, Map<String, Object> data) {
    String columns = String.join(", ", data.keySet());
    String placeholders = String.join(", ", Collections.nCopies(data.size(), "?"));
    String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + placeholders + ")";
    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      setParameters(pstmt, data);
      pstmt.executeUpdate();
      System.out.println("✅ Insert successful!");
    } catch (SQLException e) {
      System.err.println("❌ Insert failed!");
      e.printStackTrace();
    }
  }

  // Read Data
  public static void read(String table, String condition, Object... params) {
    String sql = "SELECT * FROM " + table + (condition != null ? " WHERE " + condition : "");
    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      setParameters(pstmt, params);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
          System.out.print(metaData.getColumnName(i) + ": " + rs.getObject(i) + "  ");
        }
        System.out.println();
      }
    } catch (SQLException e) {
      System.err.println("❌ Read failed!");
      e.printStackTrace();
    }
  }

  // Update Data
  public static void update(String table, Map<String, Object> data, String condition, Object... params) {
    String setClause = String.join(" = ?, ", data.keySet()) + " = ?";
    String sql = "UPDATE " + table + " SET " + setClause + (condition != null ? " WHERE " + condition : "");
    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      setParameters(pstmt, data, params);
      pstmt.executeUpdate();
      System.out.println("✅ Update successful!");
    } catch (SQLException e) {
      System.err.println("❌ Update failed!");
      e.printStackTrace();
    }
  }

  // Delete Data
  public static void delete(String table, String condition, Object... params) {
    String sql = "DELETE FROM " + table + (condition != null ? " WHERE " + condition : "");
    try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
      setParameters(pstmt, params);
      pstmt.executeUpdate();
      System.out.println("✅ Delete successful!");
    } catch (SQLException e) {
      System.err.println("❌ Delete failed!");
      e.printStackTrace();
    }
  }

  // Helper method to set parameters dynamically
  private static void setParameters(PreparedStatement pstmt, Map<String, Object> data, Object... extraParams)
      throws SQLException {
    int index = 1;
    for (Object value : data.values()) {
      pstmt.setObject(index++, value);
    }
    for (Object param : extraParams) {
      pstmt.setObject(index++, param);
    }
  }

  private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
    int index = 1;
    for (Object param : params) {
      pstmt.setObject(index++, param);
    }
  }

  // Main method for testing
  public static void main(String[] args) {
    // Example insert into CUSTOMER table
    Map<String, Object> customerData = new HashMap<>();
    customerData.put("first_name", "John");
    customerData.put("last_name", "Doe");
    customerData.put("contact_number", "123456789");
    customerData.put("email", "john.doe@example.com");
    insert("CUSTOMER", customerData);

    // Example read from CUSTOMER table
    read("CUSTOMER", "email = ?", "john.doe@example.com");
  }
}
