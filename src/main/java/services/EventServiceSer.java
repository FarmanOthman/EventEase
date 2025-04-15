package services;

import database.QueryBuilder;
import server.EventService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Service layer class that mediates between UI and server-side code
 * Handles all database and business logic operations related to events
 */
public class EventServiceSer {
  private QueryBuilder queryBuilder;
  private EventService eventService;

  public EventServiceSer() {
    this.queryBuilder = new QueryBuilder();
    this.eventService = new EventService();
  }

  /**
   * Gets event types from the database based on category
   * If database query fails, returns default values
   */
  public List<String> getEventTypes(String category) {
    try {
      // Query the database for event types
      // For now, we're returning fixed values based on database schema
      return Arrays.asList("Match", "Event");
    } catch (Exception e) {
      System.out.println("Error fetching event types: " + e.getMessage());
      return Arrays.asList("Match", "Event"); // Fallback to default values
    }
  }

  /**
   * Gets event categories from the database
   * If database query fails, returns default values
   */
  public List<String> getEventCategories() {
    try {
      // Query the database for event categories
      // For now, we're returning fixed values based on database schema
      return Arrays.asList("Regular", "VIP");
    } catch (Exception e) {
      System.out.println("Error fetching event categories: " + e.getMessage());
      return Arrays.asList("Regular", "VIP"); // Fallback to default values
    }
  }

  /**
   * Gets a list of all events from the database
   */
  public List<Map<String, Object>> getAllEvents() {
    try {
      // Select all events from the database
      return queryBuilder.select("Event", "event_id", "event_name", "event_date", "category", "event_type", "team_a",
          "team_b");
    } catch (Exception e) {
      System.out.println("Error fetching events: " + e.getMessage());
      return new ArrayList<>(); // Return empty list on error
    }
  }

  /**
   * Gets a list of events filtered by category
   */
  public List<Map<String, Object>> getEventsByCategory(String category) {
    try {
      // Create filter for category
      Map<String, Object> filters = new HashMap<>();
      filters.put("category", category);

      // Select events with the specified category
      return queryBuilder.selectWithFilters("Event", filters,
          new String[] { "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b" });
    } catch (Exception e) {
      System.out.println("Error fetching events by category: " + e.getMessage());
      return new ArrayList<>(); // Return empty list on error
    }
  }

  /**
   * Add a new event to the system
   * This method acts as an intermediary between the UI and server-side
   * EventService
   */
  public boolean addEvent(String eventName, String eventDate, String teamA, String teamB,
      String eventDescription, String eventCategory, String eventType) {
    try {
      // Call the server-side service to add the event
      eventService.addEvent(eventName, eventDate, teamA, teamB, eventDescription, eventCategory, eventType);
      return true;
    } catch (Exception e) {
      System.out.println("Error adding event: " + e.getMessage());
      return false;
    }
  }

  /**
   * Get the error message from the last operation if it failed
   */
  public String getLastErrorMessage() {
    return "An error occurred while processing your request. Please try again.";
  }
}