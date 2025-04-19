package server;

import server.notification.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager class for handling notifications in the system.
 * Implements the Observer pattern for real-time notification delivery.
 */
public class NotificationManager implements NotificationService {

  private static NotificationManager instance;
  private NotificationRepository repository;
  private List<NotificationObserver> observers;

  /**
   * Private constructor for singleton pattern
   */
  private NotificationManager() {
    observers = new ArrayList<>();

    // Initialize repository
    repository = new InMemoryNotificationRepository();

    // Add some sample notifications
    createSampleNotifications();
  }

  /**
   * Get the singleton instance of NotificationManager
   * 
   * @return The NotificationManager instance
   */
  public static synchronized NotificationManager getInstance() {
    if (instance == null) {
      instance = new NotificationManager();
    }
    return instance;
  }

  /**
   * Set the notification repository
   * 
   * @param repository The repository implementation to use
   */
  public void setRepository(NotificationRepository repository) {
    this.repository = repository;
  }

  /**
   * Create some sample notifications for testing
   */
  private void createSampleNotifications() {
    // Add sample notifications for system user
    createNotification("system", "Welcome to EventEase! Your all-in-one event management system",
        NotificationType.INFO, null);

    // Add sample notifications for admin user
    createNotification("admin", "New booking request needs approval",
        NotificationType.BOOKING_CONFIRMED, "booking123");
    createNotification("admin", "VIP tickets for Summer Concert are sold out",
        NotificationType.TICKET_SOLD_OUT, "event456");
    createNotification("admin", "Monthly report is ready for review",
        NotificationType.SUCCESS, "report789");

    // Add sample notifications for manager user
    createNotification("manager", "New event 'Tech Conference 2023' has been created",
        NotificationType.EVENT_UPCOMING, "event567");
    createNotification("manager", "Your booking for 'Annual Gala' has been confirmed",
        NotificationType.BOOKING_CONFIRMED, "booking234");
  }

  @Override
  public String createNotification(String userId, String message, NotificationType type, String relatedEntityId) {
    Notification notification = new Notification(userId, message, type, relatedEntityId);
    return repository.save(notification);
  }

  @Override
  public void sendNotification(String userId, String message, NotificationType type, String relatedEntityId) {
    String notificationId = createNotification(userId, message, type, relatedEntityId);
    Notification notification = repository.findById(notificationId);

    // Notify all observers
    for (NotificationObserver observer : observers) {
      observer.onNotificationReceived(notification);
    }
  }

  @Override
  public List<Notification> getNotificationsForUser(String userId, NotificationType filterType, boolean onlyUnread) {
    return repository.findByUserIdAndFilters(userId, filterType, onlyUnread);
  }

  @Override
  public boolean markAsRead(String notificationId) {
    Notification notification = repository.findById(notificationId);
    if (notification != null) {
      notification.setRead(true);
      return repository.update(notification);
    }
    return false;
  }

  @Override
  public boolean markAsUnread(String notificationId) {
    Notification notification = repository.findById(notificationId);
    if (notification != null) {
      notification.setRead(false);
      return repository.update(notification);
    }
    return false;
  }

  @Override
  public int markAllAsRead(String userId) {
    return repository.markAllAsRead(userId);
  }

  @Override
  public boolean deleteNotification(String notificationId) {
    return repository.delete(notificationId);
  }

  @Override
  public int getUnreadCount(String userId) {
    return repository.getUnreadCount(userId);
  }

  @Override
  public void registerObserver(NotificationObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  @Override
  public void unregisterObserver(NotificationObserver observer) {
    observers.remove(observer);
  }

  /**
   * Send a system notification to all users
   * 
   * @param message The notification message
   * @param type    The type of notification
   */
  public void sendSystemNotification(String message, NotificationType type) {
    // System notifications don't have a specific user ID or related entity
    Notification notification = new Notification("system", message, type);
    repository.save(notification);

    // Notify all observers
    for (NotificationObserver observer : observers) {
      observer.onNotificationReceived(notification);
    }
  }
}
