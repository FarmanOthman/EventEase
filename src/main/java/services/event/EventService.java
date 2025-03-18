package services.event;

import java.util.Arrays;
import java.util.List;
import java.sql.*;

public class EventService {
  private Connection connection;

  public EventService() {
    try {
      // Initialize database connection
      connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/event_management", "root", "");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public boolean createEvent(String eventName, String team1, String team2, String date,
      String type, String category, String details) throws SQLException {
    String sql = "INSERT INTO events (event_name, team1, team2, event_date, event_type, " +
        "category, details, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setString(1, eventName);
      stmt.setString(2, team1);
      stmt.setString(3, team2);
      stmt.setString(4, date);
      stmt.setString(5, type);
      stmt.setString(6, category);
      stmt.setString(7, details);

      return stmt.executeUpdate() > 0;
    }
  }

  public List<String> getEventTypes(String category) {
    // TODO: Replace with database query
    return Arrays.asList("Regular", "VIP", "Special");
  }

  public List<String> getEventCategories() {
    // TODO: Replace with database query
    return Arrays.asList("Sports", "Music", "Theater", "Conference");
  }
}