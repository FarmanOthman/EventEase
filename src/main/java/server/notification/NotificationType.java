package server.notification;

/**
 * Enum representing the different types of notifications in the system.
 */
public enum NotificationType {
  BOOKING_CONFIRMED("Booking Confirmed", "Your booking has been confirmed"),
  BOOKING_CANCELLED("Booking Cancelled", "Your booking has been cancelled"),
  BOOKING_UPDATED("Booking Updated", "Your booking has been updated"),

  EVENT_UPCOMING("Event Upcoming", "You have an upcoming event"),
  EVENT_CANCELLED("Event Cancelled", "An event has been cancelled"),
  EVENT_UPDATED("Event Updated", "Event details have been updated"),

  TICKET_SOLD_OUT("Tickets Sold Out", "Tickets for an event are sold out"),
  TICKET_AVAILABLE("Tickets Available", "Tickets are now available"),

  SUCCESS("Success", "Operation completed successfully"),
  ERROR("Error", "An error occurred"),
  WARNING("Warning", "Warning notification"),
  INFO("Information", "General information");

  private final String title;
  private final String defaultMessage;

  NotificationType(String title, String defaultMessage) {
    this.title = title;
    this.defaultMessage = defaultMessage;
  }

  public String getTitle() {
    return title;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }

  /**
   * Get the appropriate notification type based on the context
   * 
   * @param context String describing the context (e.g., "booking_created")
   * @return The appropriate NotificationType
   */
  public static NotificationType fromContext(String context) {
    if (context == null)
      return INFO;

    switch (context.toLowerCase()) {
      case "booking_created":
      case "booking_confirmed":
        return BOOKING_CONFIRMED;

      case "booking_cancelled":
        return BOOKING_CANCELLED;

      case "booking_updated":
        return BOOKING_UPDATED;

      case "event_upcoming":
        return EVENT_UPCOMING;

      case "event_cancelled":
        return EVENT_CANCELLED;

      case "event_updated":
        return EVENT_UPDATED;

      case "ticket_sold_out":
        return TICKET_SOLD_OUT;

      case "ticket_available":
        return TICKET_AVAILABLE;

      case "success":
        return SUCCESS;

      case "error":
        return ERROR;

      case "warning":
        return WARNING;

      default:
        return INFO;
    }
  }
}