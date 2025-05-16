# NotificationManager

## Overview
The `NotificationManager` class is responsible for managing notifications within the application. It implements the Observer pattern for real-time notification delivery and provides a singleton instance for centralized notification management.

## Class Structure

### Main Class: NotificationManager
This class implements the `NotificationService` interface and manages the creation, storage, and distribution of notifications throughout the application.

#### Fields
- `instance`: Static instance for the singleton pattern
- `repository`: Storage for notifications
- `observers`: List of notification observers to be notified when new notifications arrive

#### Constructor
- `NotificationManager()`: Private constructor that initializes observers list and repository, and creates sample notifications.

#### Methods

##### getInstance
```java
public static synchronized NotificationManager getInstance()
```
Gets the singleton instance of NotificationManager.

- **Returns**: The NotificationManager singleton instance
- **Implementation Details**:
  - Uses lazy initialization to create the instance only when needed
  - Thread-safe with synchronized keyword

##### setRepository
```java
public void setRepository(NotificationRepository repository)
```
Sets the notification repository.

- **Parameters**:
  - `repository`: The repository implementation to use

##### createSampleNotifications (private)
```java
private void createSampleNotifications()
```
Creates sample notifications for testing purposes.

- **Implementation Details**:
  - Creates system-wide notifications
  - Creates role-specific notifications for admins and managers

##### createNotification
```java
@Override
public String createNotification(String userId, String message, NotificationType type, String relatedEntityId)
```
Creates a new notification and stores it.

- **Parameters**:
  - `userId`: The user ID to whom the notification belongs
  - `message`: The notification message
  - `type`: The type of notification
  - `relatedEntityId`: ID of the entity related to the notification (optional)
- **Returns**: The ID of the created notification
- **Implementation Details**:
  - Creates a new Notification object
  - Saves it to the repository

##### sendNotification
```java
@Override
public void sendNotification(String userId, String message, NotificationType type, String relatedEntityId)
```
Creates a notification and notifies all observers.

- **Parameters**:
  - `userId`: The user ID to whom the notification belongs
  - `message`: The notification message
  - `type`: The type of notification
  - `relatedEntityId`: ID of the entity related to the notification (optional)
- **Implementation Details**:
  - Creates a notification using createNotification
  - Retrieves the notification from the repository
  - Notifies all registered observers

##### getNotificationsForUser
```java
@Override
public List<Notification> getNotificationsForUser(String userId, NotificationType filterType, boolean onlyUnread)
```
Gets notifications for a specific user with filtering options.

- **Parameters**:
  - `userId`: The user ID whose notifications to retrieve
  - `filterType`: The notification type to filter by (optional)
  - `onlyUnread`: Whether to only get unread notifications
- **Returns**: A list of filtered notifications for the user

##### markAsRead
```java
@Override
public boolean markAsRead(String notificationId)
```
Marks a notification as read.

- **Parameters**:
  - `notificationId`: The ID of the notification to mark as read
- **Returns**: True if the operation was successful, false otherwise

##### markAsUnread
```java
@Override
public boolean markAsUnread(String notificationId)
```
Marks a notification as unread.

- **Parameters**:
  - `notificationId`: The ID of the notification to mark as unread
- **Returns**: True if the operation was successful, false otherwise

##### markAllAsRead
```java
@Override
public int markAllAsRead(String userId)
```
Marks all notifications for a user as read.

- **Parameters**:
  - `userId`: The user ID whose notifications to mark as read
- **Returns**: The number of notifications that were marked as read

##### deleteNotification
```java
@Override
public boolean deleteNotification(String notificationId)
```
Deletes a notification.

- **Parameters**:
  - `notificationId`: The ID of the notification to delete
- **Returns**: True if the deletion was successful, false otherwise

##### getUnreadCount
```java
@Override
public int getUnreadCount(String userId)
```
Gets the count of unread notifications for a user.

- **Parameters**:
  - `userId`: The user ID whose unread notification count to get
- **Returns**: The number of unread notifications

##### registerObserver
```java
@Override
public void registerObserver(NotificationObserver observer)
```
Registers an observer to receive notification updates.

- **Parameters**:
  - `observer`: The observer to register
- **Implementation Details**:
  - Prevents duplicate registrations with contains check

##### unregisterObserver
```java
@Override
public void unregisterObserver(NotificationObserver observer)
```
Unregisters an observer from receiving notification updates.

- **Parameters**:
  - `observer`: The observer to unregister

##### sendSystemNotification
```java
public void sendSystemNotification(String message, NotificationType type)
```
Sends a system-wide notification to all users.

- **Parameters**:
  - `message`: The notification message
  - `type`: The type of notification
- **Implementation Details**:
  - Creates a notification with "system" as the user ID
  - Notifies all registered observers

## Usage Example
```java
// Get the notification manager instance
NotificationManager notificationManager = NotificationManager.getInstance();

// Send a notification to a specific user
notificationManager.sendNotification(
    "admin", 
    "New booking confirmed for Event #123", 
    NotificationType.BOOKING_CONFIRMED, 
    "booking123"
);

// Send a system-wide notification
notificationManager.sendSystemNotification(
    "System maintenance scheduled for tonight at 11 PM",
    NotificationType.INFO
);

// Get notifications for a specific user
List<Notification> notifications = notificationManager.getNotificationsForUser(
    "admin", 
    NotificationType.BOOKING_CONFIRMED, 
    true  // Only unread notifications
);

// Register an observer to receive real-time notification updates
notificationManager.registerObserver(new NotificationObserver() {
    @Override
    public void onNotificationReceived(Notification notification) {
        // Handle the new notification
        System.out.println("New notification: " + notification.getMessage());
    }
});
```

## Dependencies
- `server.notification.*`: Notification-related classes and interfaces
- `java.util.ArrayList`: Used for list operations
- `java.util.List`: Used for handling lists of data
