package server.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the NotificationRepository interface.
 * This provides a temporary storage for notifications until a proper database
 * is implemented.
 */
public class InMemoryNotificationRepository implements NotificationRepository {

  private final Map<String, Notification> notifications = new HashMap<>();
  private final Map<String, List<String>> userNotificationIds = new HashMap<>();

  @Override
  public String save(Notification notification) {
    notifications.put(notification.getId(), notification);

    // Add to user's notification list
    String userId = notification.getUserId();
    if (!userNotificationIds.containsKey(userId)) {
      userNotificationIds.put(userId, new ArrayList<>());
    }
    userNotificationIds.get(userId).add(notification.getId());

    return notification.getId();
  }

  @Override
  public Notification findById(String id) {
    return notifications.get(id);
  }

  @Override
  public List<Notification> findByUserId(String userId) {
    List<String> ids = userNotificationIds.getOrDefault(userId, new ArrayList<>());
    return ids.stream()
        .map(notifications::get)
        .collect(Collectors.toList());
  }

  @Override
  public List<Notification> findByUserIdAndFilters(String userId, NotificationType type, boolean onlyUnread) {
    List<Notification> userNotifications = findByUserId(userId);

    return userNotifications.stream()
        .filter(notification -> type == null || notification.getType() == type)
        .filter(notification -> !onlyUnread || !notification.isRead())
        .collect(Collectors.toList());
  }

  @Override
  public boolean update(Notification notification) {
    if (notifications.containsKey(notification.getId())) {
      notifications.put(notification.getId(), notification);
      return true;
    }
    return false;
  }

  @Override
  public boolean delete(String id) {
    Notification notification = notifications.remove(id);
    if (notification != null) {
      String userId = notification.getUserId();
      List<String> userIds = userNotificationIds.get(userId);
      if (userIds != null) {
        userIds.remove(id);
      }
      return true;
    }
    return false;
  }

  @Override
  public int getUnreadCount(String userId) {
    List<Notification> userNotifications = findByUserId(userId);
    return (int) userNotifications.stream()
        .filter(notification -> !notification.isRead())
        .count();
  }

  @Override
  public int markAllAsRead(String userId) {
    List<Notification> userNotifications = findByUserId(userId);
    int count = 0;

    for (Notification notification : userNotifications) {
      if (!notification.isRead()) {
        notification.setRead(true);
        notifications.put(notification.getId(), notification);
        count++;
      }
    }

    return count;
  }
}