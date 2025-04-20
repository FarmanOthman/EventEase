package server.notification;

/**
 * Interface for observers of the notification system.
 * Classes that implement this interface will receive updates when new
 * notifications are sent.
 */
public interface NotificationObserver {

  /**
   * Method called when a new notification is available
   * 
   * @param notification The notification that was sent
   */
  void onNotificationReceived(Notification notification);
}