package services;

import database.QueryBuilder;
import server.BookingServer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer class that mediates between UI and server-side code
 * Handles all booking operations and communicates with the BookingService in
 * the server layer
 */
public class BookingServiceSer {
  private BookingServer bookingService;
  private EventServiceSer eventServiceSer;
  private QueryBuilder queryBuilder;
  private String lastErrorMessage;

  public BookingServiceSer() {
    this.bookingService = new BookingServer();
    this.eventServiceSer = new EventServiceSer();
    this.queryBuilder = new QueryBuilder();
    this.lastErrorMessage = "";
  }

  /**
   * Load all events for the booking dropdown
   */
  public List<Map<String, Object>> getAllEvents() {
    return eventServiceSer.getAllEvents();
  }

  /**
   * Create a new booking
   */
  public boolean createBooking(String customerName, String selectedEvent, String selectedPriceCategory,
      int customerId, int eventId, String ticketType) {
    try {
      boolean success = bookingService.addBooking(
          customerName, selectedEvent, selectedPriceCategory,
          customerId, eventId, ticketType);

      if (!success) {
        lastErrorMessage = "Failed to create booking. Please try again.";
      }
      return success;
    } catch (Exception e) {
      lastErrorMessage = e.getMessage();
      return false;
    }
  }

  /**
   * Get pricing options available for booking
   * The options map to the ticket types defined in the database schema (Regular,
   * VIP)
   */
  public String[] getPricingOptions() {
    return new String[] {
        "Select Price Category",
        "VIP - $25",
        "Regular - Premium - $15",
        "Regular - Standard - $10"
    };
  }

  /**
   * Determine the ticket type based on selected price category
   * Maps price categories to the allowed ticket types in the database schema
   */
  public String getTicketTypeFromPriceCategory(String priceCategory) {
    if (priceCategory.contains("VIP")) {
      return "VIP";
    } else if (priceCategory.contains("Regular") || priceCategory.contains("Premium")
        || priceCategory.contains("Standard")) {
      return "Regular";
    }

    // Default fallback (should never happen with validation)
    return "Regular";
  }

  /**
   * Get the last error message
   */
  public String getLastErrorMessage() {
    return lastErrorMessage;
  }

  /**
   * Get customer by ID
   */
  public Map<String, Object> getCustomerById(int customerId) {
    try {
      Map<String, Object> filters = new HashMap<>();
      filters.put("customer_id", customerId);

      List<Map<String, Object>> customers = queryBuilder.selectWithFilters("Customer", filters,
          new String[] { "customer_id", "first_name", "last_name", "email", "contact_number" });

      if (customers.isEmpty()) {
        return null;
      }

      return customers.get(0);
    } catch (Exception e) {
      lastErrorMessage = "Error fetching customer information: " + e.getMessage();
      return null;
    }
  }

  /**
   * Get event by ID
   */
  public Map<String, Object> getEventById(int eventId) {
    try {
      Map<String, Object> filters = new HashMap<>();
      filters.put("event_id", eventId);

      List<Map<String, Object>> events = queryBuilder.selectWithFilters("Event", filters,
          new String[] { "event_id", "event_name", "event_date", "category", "team_a", "team_b" });

      if (events.isEmpty()) {
        return null;
      }

      return events.get(0);
    } catch (Exception e) {
      lastErrorMessage = "Error fetching event information: " + e.getMessage();
      return null;
    }
  }

  /**
   * Get event details by event ID
   * 
   * @param eventId The ID of the event to retrieve
   * @return Map containing event details or null if not found
   */
  public Map<String, Object> getEventDetails(int eventId) {
    try {
      // Create filter for event ID
      Map<String, Object> filters = new HashMap<>();
      filters.put("event_id", eventId);

      // Select event with the specified ID
      List<Map<String, Object>> events = queryBuilder.selectWithFilters("Event", filters,
          new String[] { "event_id", "event_name", "event_date", "event_description",
              "category", "event_type", "team_a", "team_b" });

      if (events.isEmpty()) {
        lastErrorMessage = "Event not found";
        return null;
      }

      return events.get(0);
    } catch (Exception e) {
      lastErrorMessage = "Error fetching event details: " + e.getMessage();
      return null;
    }
  }

  /**
   * Validate if a ticket can be created for the specified event
   * Takes into account the constraint that Match-type events can only have one
   * ticket
   */
  public boolean canCreateTicketForEvent(int eventId) {
    try {
      // Get event details
      Map<String, Object> event = getEventDetails(eventId);
      if (event == null) {
        return false;
      }

      // If event is a Match, check if any tickets exist already
      if ("Match".equals(event.get("event_type"))) {
        Map<String, Object> filters = new HashMap<>();
        filters.put("event_id", eventId);

        List<Map<String, Object>> existingTickets = queryBuilder.selectWithFilters("Ticket", filters,
            new String[] { "ticket_id" });

        if (!existingTickets.isEmpty()) {
          lastErrorMessage = "Only one ticket can be created for a Match event";
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      lastErrorMessage = "Error validating ticket creation: " + e.getMessage();
      return false;
    }
  }
}