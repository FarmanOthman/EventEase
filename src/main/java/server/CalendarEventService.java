package server;

import database.QueryBuilder;
import java.time.LocalDate;
import java.util.*;

/**
 * Server-side service to handle calendar event business logic.
 * This class interacts directly with the database and performs data operations.
 */
public class CalendarEventService {
  private QueryBuilder queryBuilder;
  private String lastErrorMessage;

  public CalendarEventService() {
    queryBuilder = new QueryBuilder();
    lastErrorMessage = "";
  }

  /**
   * Get all events for a specific month and year from the database
   */
  public List<Map<String, Object>> getEventsForMonth(int year, int month) {
    try {
      // Create a SQL date range for the month
      String startDate = String.format("%04d-%02d-01", year, month);
      String endDate;

      // Handle December as a special case
      if (month == 12) {
        endDate = String.format("%04d-%02d-31", year, month);
      } else {
        endDate = String.format("%04d-%02d-01", year, month + 1);
      }

      // Use custom SQL query to get events between dates
      String sql = "SELECT * FROM Event WHERE event_date >= ? AND event_date < ?";

      // Execute query through connection
      List<Map<String, Object>> events = new ArrayList<>();

      try (
          java.sql.Connection conn = java.sql.DriverManager
              .getConnection("jdbc:sqlite:src/main/resources/EventEase.db");
          java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, startDate);
        stmt.setString(2, endDate);

        java.sql.ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
          Map<String, Object> event = new HashMap<>();
          event.put("event_id", rs.getInt("event_id"));
          event.put("event_name", rs.getString("event_name"));
          event.put("event_date", rs.getString("event_date"));
          event.put("event_description", rs.getString("event_description"));
          event.put("category", rs.getString("category"));
          event.put("event_type", rs.getString("event_type"));
          event.put("team_a", rs.getString("team_a"));
          event.put("team_b", rs.getString("team_b"));
          events.add(event);
        }
      }

      return events;
    } catch (Exception e) {
      lastErrorMessage = "Error fetching events: " + e.getMessage();
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Get all VIP events for a specific month and year
   */
  public List<Map<String, Object>> getVipEventsForMonth(int year, int month) {
    try {
      List<Map<String, Object>> allEvents = getEventsForMonth(year, month);
      List<Map<String, Object>> vipEvents = new ArrayList<>();

      // Filter for VIP events only
      for (Map<String, Object> event : allEvents) {
        if ("VIP".equalsIgnoreCase((String) event.get("category"))) {
          vipEvents.add(event);
        }
      }

      return vipEvents;
    } catch (Exception e) {
      lastErrorMessage = "Error fetching VIP events: " + e.getMessage();
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Get all events for a specific date
   */
  public List<Map<String, Object>> getEventsForDate(LocalDate date) {
    try {
      String dateStr = date.toString(); // Format: YYYY-MM-DD

      // Execute custom query to handle LIKE operator for date matching
      String sql = "SELECT * FROM Event WHERE event_date LIKE ?";
      List<Map<String, Object>> events = new ArrayList<>();

      try (
          java.sql.Connection conn = java.sql.DriverManager
              .getConnection("jdbc:sqlite:src/main/resources/EventEase.db");
          java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, dateStr + "%"); // Use LIKE for partial matching

        java.sql.ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
          Map<String, Object> event = new HashMap<>();
          event.put("event_id", rs.getInt("event_id"));
          event.put("event_name", rs.getString("event_name"));
          event.put("event_date", rs.getString("event_date"));
          event.put("event_description", rs.getString("event_description"));
          event.put("category", rs.getString("category"));
          event.put("event_type", rs.getString("event_type"));
          event.put("team_a", rs.getString("team_a"));
          event.put("team_b", rs.getString("team_b"));
          events.add(event);
        }
      }

      return events;
    } catch (Exception e) {
      lastErrorMessage = "Error fetching events for date: " + e.getMessage();
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * Add a new event to the calendar
   */
  public boolean addEvent(String eventName, String eventDate, String category,
      String eventType, String teamA, String teamB, String description) {
    try {
      // Create a map for the new event data
      Map<String, Object> eventData = new HashMap<>();
      eventData.put("event_name", eventName);
      eventData.put("event_date", eventDate);
      eventData.put("category", category);
      eventData.put("event_type", eventType);
      eventData.put("team_a", teamA);
      eventData.put("team_b", teamB);
      eventData.put("event_description", description);
      eventData.put("created_at", new java.sql.Timestamp(System.currentTimeMillis()));
      eventData.put("updated_at", new java.sql.Timestamp(System.currentTimeMillis()));

      // Insert into database
      queryBuilder.insert("Event", eventData);
      return true;
    } catch (Exception e) {
      lastErrorMessage = "Error adding event: " + e.getMessage();
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Get the last error message
   */
  public String getLastErrorMessage() {
    return lastErrorMessage;
  }
}