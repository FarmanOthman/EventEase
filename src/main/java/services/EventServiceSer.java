package services;

import server.EventService;
import server.UpcomingEventService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service layer class that mediates between UI and server-side code
 * Handles all database and business logic operations related to events
 */
public class EventServiceSer {
  private EventService eventService;
  private UpcomingEventService upcomingEventService;
  private String lastErrorMessage = "";

  public EventServiceSer() {
    this.eventService = new EventService();
    this.upcomingEventService = new UpcomingEventService();
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
      // Try to get categories from the UpcomingEventService
      List<String> categories = upcomingEventService.getEventCategories();
      if (categories.isEmpty()) {
        // Fallback to default values if none found
        return Arrays.asList("Regular", "VIP");
      }
      return categories;
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
      // Get events from the UpcomingEventService
      return upcomingEventService.getAllEvents();
    } catch (Exception e) {
      System.out.println("Error fetching events: " + e.getMessage());
      lastErrorMessage = e.getMessage();
      return new ArrayList<>(); // Return empty list on error
    }
  }

  /**
   * Gets a list of events filtered by category
   */
  public List<Map<String, Object>> getEventsByCategory(String category) {
    try {
      // Get filtered events from the UpcomingEventService
      return upcomingEventService.getEventsByCategory(category);
    } catch (Exception e) {
      System.out.println("Error fetching events by category: " + e.getMessage());
      lastErrorMessage = e.getMessage();
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
      lastErrorMessage = e.getMessage();
      return false;
    }
  }

  /**
   * Get the details of a specific event
   * 
   * @param eventId The ID of the event to retrieve
   * @return A map containing the event details, or null if not found
   */
  public Map<String, Object> getEventDetails(int eventId) {
    try {
      // Get event details from the UpcomingEventService
      return upcomingEventService.getEventDetails(eventId);
    } catch (Exception e) {
      System.out.println("Error fetching event details: " + e.getMessage());
      lastErrorMessage = e.getMessage();
      return null;
    }
  }

  /**
   * Edit an existing event
   * 
   * @param eventId          The ID of the event to edit
   * @param eventName        New name for the event
   * @param eventDate        New date for the event
   * @param teamA            New team A
   * @param teamB            New team B
   * @param eventDescription New description
   * @param eventCategory    New category
   * @param eventType        New event type
   * @param location         New location (can be null if location is not
   *                         supported)
   * @return true if successful, false otherwise
   */
  public boolean editEvent(int eventId, String eventName, String eventDate,
      String teamA, String teamB, String eventDescription,
      String eventCategory, String eventType, String location) {
    try {
      // If location is provided but not supported, use the overloaded method without
      // location
      if (location != null) {
        try {
          // Try with location first
          boolean result = upcomingEventService.editEvent(eventId, eventName, eventDate,
              teamA, teamB, eventDescription, eventCategory, eventType, location);
          return result;
        } catch (Exception e) {
          // If location column doesn't exist, try without it
          if (e.getMessage() != null && e.getMessage().contains("no such column: location")) {
            // Try the edit without location
            return upcomingEventService.editEvent(eventId, eventName, eventDate,
                teamA, teamB, eventDescription, eventCategory, eventType, null);
          }
          throw e; // Re-throw if it's a different error
        }
      } else {
        // Location not provided, use base method
        return upcomingEventService.editEvent(eventId, eventName, eventDate,
            teamA, teamB, eventDescription, eventCategory, eventType, null);
      }
    } catch (Exception e) {
      System.out.println("Error editing event: " + e.getMessage());
      lastErrorMessage = e.getMessage();
      return false;
    }
  }

  /**
   * Delete an event
   * 
   * @param eventId The ID of the event to delete
   * @return true if successful, false otherwise
   */
  public boolean deleteEvent(int eventId) {
    try {
      // Call the server-side UpcomingEventService to delete the event
      boolean result = upcomingEventService.deleteEvent(eventId);
      if (!result) {
        lastErrorMessage = "Failed to delete event. Please try again.";
      }
      return result;
    } catch (Exception e) {
      System.out.println("Error deleting event: " + e.getMessage());
      lastErrorMessage = e.getMessage();
      return false;
    }
  }

  /**
   * Get the error message from the last operation if it failed
   */
  public String getLastErrorMessage() {
    return lastErrorMessage.isEmpty() ? "An error occurred while processing your request. Please try again."
        : lastErrorMessage;
  }
}