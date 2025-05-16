# NotificationService

## Overview
The `NotificationService` class acts as an adapter between the UI and the server-side notification system. It implements the Observer pattern to receive real-time notifications and relay them to registered UI components. The service also provides methods for retrieving, managing, and categorizing notifications.

## Class Structure

### Main Class: NotificationService
This class implements the NotificationObserver interface and serves as a bridge between the UI and notification system.

#### Fields
- `instance`: Static instance for the singleton pattern
- `notificationManager`: An instance of NotificationManager for handling notifications
- `registeredViews`: List of UI views that have registered to receive notification updates
- `currentUserId`: The ID of the currently logged-in user

#### Constructor
- `NotificationService()`: Private constructor that initializes the notification manager, registers this service as an observer, and sets the default user to "system".

#### Methods

##### getInstance
```java
public static synchronized NotificationService getInstance()
```
Gets the singleton instance of NotificationService.

- **Returns**: The NotificationService singleton instance
- **Implementation Details**:
  - Uses lazy initialization to create the instance only when needed
  - Thread-safe with synchronized keyword

##### setCurrentUserId
```java
public void setCurrentUserId(String userId)
```
Sets the current user ID after login.

- **Parameters**:
  - `userId`: The current user ID

##### registerView
```java
public void registerView(NotificationView view)
```
Registers a NotificationView to receive updates.

- **Parameters**:
  - `view`: The view to register
- **Implementation Details**:
  - Prevents duplicate registrations with contains check

##### unregisterView
```java
public void unregisterView(NotificationView view)
```
Unregisters a NotificationView.

- **Parameters**:
  - `view`: The view to unregister

##### getNotifications
```java
public List<UINotification> getNotifications(NotificationType filterType, boolean onlyUnread)
```
Gets all notifications for the current user.

- **Parameters**:
  - `filterType`: Optional filter by notification type (can be null)
  - `onlyUnread`: If true, only returns unread notifications
- **Returns**: List of UI notifications
- **Implementation Details**:
  - Gets user-specific notifications from the notification manager
  - Also gets system notifications visible to all users
  - Combines both lists and converts them to UI-friendly format

##### getUnreadCount
```java
public int getUnreadCount()
```
Gets the count of unread notifications for the current user.

- **Returns**: The number of unread notifications
- **Implementation Details**:
  - Combines both user-specific and system unread notifications

##### markAsRead
```java
public boolean markAsRead(String notificationId)
```
Marks a notification as read.

- **Parameters**:
  - `notificationId`: The ID of the notification to mark
- **Returns**: True if successful, false otherwise
- **Implementation Details**:
  - Delegates to notification manager
  - Updates all registered views if successful

##### markAsUnread
```java
public boolean markAsUnread(String notificationId)
```
Marks a notification as unread.

- **Parameters**:
  - `notificationId`: The ID of the notification to mark
- **Returns**: True if successful, false otherwise
- **Implementation Details**:
  - Delegates to notification manager
  - Updates all registered views if successful

##### markAllAsRead
```java
public int markAllAsRead()
```
Marks all notifications for the current user as read.

- **Returns**: The number of notifications marked as read
- **Implementation Details**:
  - Delegates to notification manager
  - Updates all registered views if any notifications were marked

##### deleteNotification
```java
public boolean deleteNotification(String notificationId)
```
Deletes a notification.

- **Parameters**:
  - `notificationId`: The ID of the notification to delete
- **Returns**: True if successful, false otherwise
- **Implementation Details**:
  - Delegates to notification manager
  - Updates all registered views if successful

##### sendNotification
```java
public String sendNotification(String message, NotificationType type, String relatedEntityId)
```
Sends a notification to the current user.

- **Parameters**:
  - `message`: The notification message
  - `type`: The type of notification
  - `relatedEntityId`: Optional ID of the related entity
- **Returns**: The ID of the created notification
- **Implementation Details**:
  - Delegates to notification manager
  - Uses currentUserId as the recipient

##### sendSystemNotification
```java
public void sendSystemNotification(String message, NotificationType type)
```
Sends a system notification visible to all users.

- **Parameters**:
  - `message`: The notification message
  - `type`: The type of notification
- **Implementation Details**:
  - Delegates to notification manager's sendSystemNotification method

##### onNotificationReceived
```java
@Override
public void onNotificationReceived(Notification notification)
```
Called when a new notification is received (from the NotificationObserver interface).

- **Parameters**:
  - `notification`: The received notification
- **Implementation Details**:
  - Converts the server notification to UI format
  - Determines if the notification should be displayed to the current user
  - Notifies all registered views about the new notification
  - Shows a toast or popup for high-priority notifications

##### convertToUINotifications (private)
```java
private List<UINotification> convertToUINotifications(List<Notification> notifications)
```
Converts server notifications to UI-friendly format.

- **Parameters**:
  - `notifications`: List of server notifications
- **Returns**: List of UI notifications
- **Implementation Details**:
  - Converts each server notification to a UINotification object
  - Sets appropriate icons, colors, and formatting based on notification type

##### notifyViews (private)
```java
private void notifyViews()
```
Notifies all registered views to refresh their notification displays.

- **Implementation Details**:
  - Calls onNotificationsUpdated on all registered views

##### showNotificationPopup (private)
```java
private void showNotificationPopup(UINotification notification)
```
Shows a popup for important notifications.

- **Parameters**:
  - `notification`: The notification to display
- **Implementation Details**:
  - Uses JOptionPane to show a popup for high-priority notifications
  - Customizes the display based on notification type

### Class: UINotification
Represents a notification formatted for the UI.

#### Fields
- `id`: The unique identifier of the notification
- `message`: The notification message
- `type`: The type of notification
- `timestamp`: When the notification was created
- `read`: Whether the notification has been read
- `icon`: Icon representing the notification type
- `color`: Color representing the notification priority
- `relatedEntityId`: Optional ID of the related entity

## Usage Example
```java
// Get the notification service instance
NotificationService notificationService = NotificationService.getInstance();

// Set the current user after login
notificationService.setCurrentUserId("admin");

// Register a UI component to receive notifications
notificationService.registerView(new NotificationView() {
    @Override
    public void onNotificationsUpdated() {
        // Refresh notification display
        refreshNotificationPanel();
    }
    
    @Override
    public void onNotificationReceived(UINotification notification) {
        // Handle new notification
        displayNewNotification(notification);
    }
});

// Get all notifications for the current user
List<UINotification> allNotifications = notificationService.getNotifications(null, false);

// Get only unread notifications
List<UINotification> unreadNotifications = notificationService.getNotifications(null, true);

// Get only event notifications
List<UINotification> eventNotifications = 
    notificationService.getNotifications(NotificationType.EVENT_UPCOMING, false);

// Get unread count
int unreadCount = notificationService.getUnreadCount();

// Mark a notification as read
boolean markSuccess = notificationService.markAsRead("notification-123");

// Mark all notifications as read
int markedCount = notificationService.markAllAsRead();

// Send a notification
String newNotificationId = notificationService.sendNotification(
    "New booking confirmed",
    NotificationType.BOOKING_CONFIRMED,
    "booking-456"
);

// Send a system notification
notificationService.sendSystemNotification(
    "System maintenance scheduled for tonight",
    NotificationType.INFO
);
```

## Dependencies
- `server.NotificationManager`: Server-side notification management
- `server.notification.*`: Notification classes and interfaces
- `ui.pages.NotificationView`: UI component interface for notifications
- `java.util.ArrayList`: For list operations
- `java.util.Date`: For timestamp handling
- `java.util.List`: For handling lists of data
- `java.awt.Color`: For notification color coding
- `javax.swing.JOptionPane`: For notification popups
