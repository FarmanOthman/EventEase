package server.notification;

import java.util.List;

/**
 * Interface for the Notification Service that provides methods for managing
 * notifications.
 * Follows the Observer pattern to broadcast notifications to registered
 * observers.
 */
public interface NotificationService {

  /**
   * Creates a new notification and saves it to the database
   * 
   * @param userId          The ID of the user the notification is for
   * @param message         The notification message
   * @param type            The type of notification
   * @param relatedEntityId Optional ID of the related entity (event, booking,
   *                        etc.)
   * @return The ID of the newly created notification
   */
  String createNotification(String userId, String message, NotificationType type, String relatedEntityId);

  /**
   * Sends a notification to all registered observers and saves it to the database
   * 
   * @param userId          The ID of the user the notification is for
   * @param message         The notification message
   * @param type            The type of notification
   * @param relatedEntityId Optional ID of the related entity (event, booking,
   *                        etc.)
   */
  void sendNotification(String userId, String message, NotificationType type, String relatedEntityId);

  /**
   * Gets all notifications for a user
   * 
   * @param userId     The ID of the user
   * @param filterType Optional filter by notification type
   * @param onlyUnread If true, only returns unread notifications
   * @return List of notifications for the user
   */
  List<Notification> getNotificationsForUser(String userId, NotificationType filterType, boolean onlyUnread);

  /**
   * Marks a notification as read
   * 
   * @param notificationId The ID of the notification
   * @return true if successful, false otherwise
   */
  boolean markAsRead(String notificationId);

  /**
   * Marks a notification as unread
   * 
   * @param notificationId The ID of the notification
   * @return true if successful, false otherwise
   */
  boolean markAsUnread(String notificationId);

  /**
   * Marks all notifications for a user as read
   * 
   * @param userId The ID of the user
   * @return Number of notifications marked as read
   */
  int markAllAsRead(String userId);

  /**
   * Deletes a notification
   * 
   * @param notificationId The ID of the notification
   * @return true if successful, false otherwise
   */
  boolean deleteNotification(String notificationId);

  /**
   * Gets the count of unread notifications for a user
   * 
   * @param userId The ID of the user
   * @return The count of unread notifications
   */
  int getUnreadCount(String userId);

  /**
   * Registers an observer to receive notifications
   * 
   * @param observer The observer to register
   */
  void registerObserver(NotificationObserver observer);

  /**
   * Unregisters an observer
   * 
   * @param observer The observer to unregister
   */
  void unregisterObserver(NotificationObserver observer);
}