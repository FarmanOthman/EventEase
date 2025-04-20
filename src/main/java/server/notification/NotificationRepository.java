package server.notification;

import java.util.List;

/**
 * Interface for database operations related to notifications.
 */
public interface NotificationRepository {

  /**
   * Saves a notification to the database
   * 
   * @param notification The notification to save
   * @return The ID of the saved notification
   */
  String save(Notification notification);

  /**
   * Finds a notification by its ID
   * 
   * @param id The ID of the notification
   * @return The notification or null if not found
   */
  Notification findById(String id);

  /**
   * Finds all notifications for a user
   * 
   * @param userId The ID of the user
   * @return List of notifications for the user
   */
  List<Notification> findByUserId(String userId);

  /**
   * Finds all notifications for a user with optional filtering
   * 
   * @param userId     The ID of the user
   * @param type       The type of notification to filter by (can be null)
   * @param onlyUnread If true, only returns unread notifications
   * @return Filtered list of notifications
   */
  List<Notification> findByUserIdAndFilters(String userId, NotificationType type, boolean onlyUnread);

  /**
   * Updates a notification in the database
   * 
   * @param notification The notification to update
   * @return true if successful, false otherwise
   */
  boolean update(Notification notification);

  /**
   * Deletes a notification from the database
   * 
   * @param id The ID of the notification to delete
   * @return true if successful, false otherwise
   */
  boolean delete(String id);

  /**
   * Gets the count of unread notifications for a user
   * 
   * @param userId The ID of the user
   * @return The count of unread notifications
   */
  int getUnreadCount(String userId);

  /**
   * Marks all notifications for a user as read
   * 
   * @param userId The ID of the user
   * @return Number of notifications marked as read
   */
  int markAllAsRead(String userId);
}