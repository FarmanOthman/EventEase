package services;

import server.NotificationManager;
import server.notification.*;
import ui.pages.NotificationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.awt.Color;

/**
 * Service class to connect the UI with the notification system.
 * Acts as an adapter between the UI representation of notifications and the
 * server-side notification system.
 */
public class NotificationService implements NotificationObserver {

  private static NotificationService instance;
  private NotificationManager notificationManager;
  private List<NotificationView> registeredViews;
  private String currentUserId;

  /**
   * Private constructor for singleton pattern
   */
  private NotificationService() {
    notificationManager = NotificationManager.getInstance();
    notificationManager.registerObserver(this);
    registeredViews = new ArrayList<>();

    // Default to system user until login happens
    currentUserId = "system";
  }

  /**
   * Get the singleton instance of NotificationService
   * 
   * @return The NotificationService instance
   */
  public static synchronized NotificationService getInstance() {
    if (instance == null) {
      instance = new NotificationService();
    }
    return instance;
  }

  /**
   * Set the current user ID after login
   * 
   * @param userId The current user ID
   */
  public void setCurrentUserId(String userId) {
    this.currentUserId = userId;
  }

  /**
   * Register a NotificationView to receive updates
   * 
   * @param view The view to register
   */
  public void registerView(NotificationView view) {
    if (!registeredViews.contains(view)) {
      registeredViews.add(view);
    }
  }

  /**
   * Unregister a NotificationView
   * 
   * @param view The view to unregister
   */
  public void unregisterView(NotificationView view) {
    registeredViews.remove(view);
  }

  /**
   * Get all notifications for the current user
   * 
   * @param filterType Optional filter by notification type (can be null)
   * @param onlyUnread If true, only returns unread notifications
   * @return List of UI notifications
   */
  public List<UINotification> getNotifications(NotificationType filterType, boolean onlyUnread) {
    List<Notification> serverNotifications = notificationManager.getNotificationsForUser(
        currentUserId, filterType, onlyUnread);

    return convertToUINotifications(serverNotifications);
  }

  /**
   * Get the count of unread notifications for the current user
   * 
   * @return The count of unread notifications
   */
  public int getUnreadCount() {
    return notificationManager.getUnreadCount(currentUserId);
  }

  /**
   * Mark a notification as read
   * 
   * @param notificationId The ID of the notification
   * @return true if successful, false otherwise
   */
  public boolean markAsRead(String notificationId) {
    return notificationManager.markAsRead(notificationId);
  }

  /**
   * Mark a notification as unread
   * 
   * @param notificationId The ID of the notification
   * @return true if successful, false otherwise
   */
  public boolean markAsUnread(String notificationId) {
    return notificationManager.markAsUnread(notificationId);
  }

  /**
   * Mark all notifications for the current user as read
   * 
   * @return Number of notifications marked as read
   */
  public int markAllAsRead() {
    return notificationManager.markAllAsRead(currentUserId);
  }

  /**
   * Delete a notification
   * 
   * @param notificationId The ID of the notification
   * @return true if successful, false otherwise
   */
  public boolean deleteNotification(String notificationId) {
    return notificationManager.deleteNotification(notificationId);
  }

  @Override
  public void onNotificationReceived(Notification notification) {
    // Update all registered views
    for (NotificationView view : registeredViews) {
      view.refreshNotifications();
    }
  }

  /**
   * Convert server notifications to UI notifications
   * 
   * @param serverNotifications List of server notifications
   * @return List of UI notifications
   */
  private List<UINotification> convertToUINotifications(List<Notification> serverNotifications) {
    List<UINotification> uiNotifications = new ArrayList<>();

    for (Notification notification : serverNotifications) {
      UINotification uiNotification = new UINotification(
          notification.getId(),
          notification.getMessage(),
          convertToUIType(notification.getType()),
          notification.getTimestamp(),
          notification.isRead());
      uiNotifications.add(uiNotification);
    }

    return uiNotifications;
  }

  /**
   * Convert server notification type to UI notification type
   * 
   * @param serverType Server notification type
   * @return UI notification type
   */
  private UINotificationType convertToUIType(NotificationType serverType) {
    switch (serverType) {
      case BOOKING_CONFIRMED:
      case BOOKING_UPDATED:
      case BOOKING_CANCELLED:
        return UINotificationType.BOOKING_CONFIRMED;

      case EVENT_UPCOMING:
      case EVENT_UPDATED:
      case EVENT_CANCELLED:
        return UINotificationType.EVENT_UPCOMING;

      case TICKET_SOLD_OUT:
      case TICKET_AVAILABLE:
        return UINotificationType.TICKET_SOLD_OUT;

      case SUCCESS:
        return UINotificationType.SUCCESS;

      case ERROR:
        return UINotificationType.ERROR;

      case WARNING:
        return UINotificationType.WARNING;

      case INFO:
      default:
        return UINotificationType.INFO;
    }
  }

  /**
   * UI representation of a notification
   */
  public static class UINotification {
    private String id;
    private String message;
    private UINotificationType type;
    private Date timestamp;
    private boolean read;

    public UINotification(String id, String message, UINotificationType type, Date timestamp, boolean read) {
      this.id = id;
      this.message = message;
      this.type = type;
      this.timestamp = timestamp;
      this.read = read;
    }

    public String getId() {
      return id;
    }

    public String getMessage() {
      return message;
    }

    public UINotificationType getType() {
      return type;
    }

    public Date getTimestamp() {
      return timestamp;
    }

    public boolean isRead() {
      return read;
    }
  }

  /**
   * UI representation of notification types with styling information
   */
  public enum UINotificationType {
    BOOKING_CONFIRMED(new Color(220, 255, 220), new Color(240, 255, 240), new Color(200, 240, 200),
        new Color(40, 167, 69)),
    TICKET_SOLD_OUT(new Color(255, 220, 220), new Color(255, 240, 240), new Color(245, 200, 200),
        new Color(220, 53, 69)),
    EVENT_UPCOMING(new Color(255, 243, 205), new Color(255, 250, 230), new Color(250, 230, 180),
        new Color(255, 193, 7)),
    SUCCESS(new Color(220, 255, 240), new Color(240, 255, 250), new Color(200, 245, 220), new Color(40, 167, 120)),
    ERROR(new Color(255, 200, 200), new Color(255, 230, 230), new Color(245, 180, 180), new Color(220, 20, 60)),
    WARNING(new Color(255, 250, 210), new Color(255, 253, 235), new Color(250, 245, 190), new Color(255, 180, 0)),
    INFO(new Color(220, 240, 255), new Color(240, 250, 255), new Color(200, 230, 245), new Color(23, 162, 184));

    final Color backgroundColor;
    final Color backgroundColorRead;
    final Color backgroundColorHover;
    final Color borderColor;

    UINotificationType(Color backgroundColor, Color backgroundColorRead, Color backgroundColorHover,
        Color borderColor) {
      this.backgroundColor = backgroundColor;
      this.backgroundColorRead = backgroundColorRead;
      this.backgroundColorHover = backgroundColorHover;
      this.borderColor = borderColor;
    }

    public Color getBackgroundColor() {
      return backgroundColor;
    }

    public Color getBackgroundColorRead() {
      return backgroundColorRead;
    }

    public Color getBackgroundColorHover() {
      return backgroundColorHover;
    }

    public Color getBorderColor() {
      return borderColor;
    }
  }
}