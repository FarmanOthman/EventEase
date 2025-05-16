package services;

import server.CalendarEventServer;
import server.NotificationManager;
import server.notification.NotificationType;
import java.time.LocalDate;
import java.util.*;

/**
 * Service to handle calendar-specific event data retrieval for the UI.
 * Acts as a connector between the GUI and server components.
 */
public class EventCalendarService {
  private CalendarEventServer calendarEventService;
  private String lastErrorMessage;

  public EventCalendarService() {
    calendarEventService = new CalendarEventServer();
    lastErrorMessage = "";
  }

  /**
   * Get all events for a specific month and year
   */
  public List<Map<String, Object>> getEventsForMonth(int year, int month) {
    try {
      // Call server-side service to handle the business logic and data fetching
      List<Map<String, Object>> events = calendarEventService.getEventsForMonth(year, month);

      if (events.isEmpty()) {
        lastErrorMessage = "No events found for the specified month.";
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
      // Call server-side service to handle the business logic and data fetching
      return calendarEventService.getVipEventsForMonth(year, month);
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
      // Call server-side service to handle the business logic and data fetching
      List<Map<String, Object>> events = calendarEventService.getEventsForDate(date);

      if (events.isEmpty()) {
        lastErrorMessage = "No events found for the specified date.";
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
   */  public boolean addEvent(String eventName, String eventDate, String category,
      String eventType, String teamA, String teamB, String description) {
    try {
      // Call server-side service to handle the business logic
      boolean success = calendarEventService.addEvent(eventName, eventDate, category,
          eventType, teamA, teamB, description);
          
      if (success) {
        // Send notification for the new calendar event
        NotificationManager notificationManager = NotificationManager.getInstance();
        String message = "New " + category + " " + eventType + " added to calendar: " + eventName + " on " + eventDate;
        
        // Send to admin and manager users
        notificationManager.sendNotification("admin", message, NotificationType.EVENT_UPCOMING, null);
        notificationManager.sendNotification("manager", message, NotificationType.EVENT_UPCOMING, null);
        
        // Also send a system notification that will be visible to any logged-in user
        notificationManager.sendSystemNotification(message, NotificationType.EVENT_UPCOMING);
      } else {
        lastErrorMessage = calendarEventService.getLastErrorMessage();
      }
      
      return success;
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
    // First check if there's a server error
    String serverError = calendarEventService.getLastErrorMessage();
    if (serverError != null && !serverError.isEmpty()) {
      return serverError;
    }
    // Otherwise return local error
    return lastErrorMessage;
  }
}