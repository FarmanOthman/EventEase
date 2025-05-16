# EventCalendarService

## Overview
The `EventCalendarService` class serves as a connector between the GUI and server components for calendar-specific event data retrieval. It provides methods to fetch and display events based on different date criteria and to add new events to the calendar.

## Class Structure

### Main Class: EventCalendarService
This class handles calendar event operations and mediates between the UI and server layer.

#### Fields
- `calendarEventService`: An instance of CalendarEventServer used for calendar event operations
- `lastErrorMessage`: Stores the last error message for error reporting

#### Constructor
- `EventCalendarService()`: Initializes a new EventCalendarService with a CalendarEventServer instance.

#### Methods

##### getEventsForMonth
```java
public List<Map<String, Object>> getEventsForMonth(int year, int month)
```
Gets all events for a specific month and year.

- **Parameters**:
  - `year`: The year to get events for
  - `month`: The month to get events for (1-12)
- **Returns**: List of events for the specified month and year
- **Implementation Details**:
  - Calls the server-side service to fetch events
  - Updates lastErrorMessage if no events are found
  - Returns an empty list on error

##### getVipEventsForMonth
```java
public List<Map<String, Object>> getVipEventsForMonth(int year, int month)
```
Gets all VIP events for a specific month and year.

- **Parameters**:
  - `year`: The year to get VIP events for
  - `month`: The month to get VIP events for (1-12)
- **Returns**: List of VIP events for the specified month and year
- **Implementation Details**:
  - Calls the server-side service to fetch VIP events
  - Updates lastErrorMessage on error
  - Returns an empty list on error

##### getEventsForDate
```java
public List<Map<String, Object>> getEventsForDate(LocalDate date)
```
Gets all events for a specific date.

- **Parameters**:
  - `date`: The LocalDate to get events for
- **Returns**: List of events for the specified date
- **Implementation Details**:
  - Calls the server-side service to fetch events for the date
  - Updates lastErrorMessage if no events are found
  - Returns an empty list on error

##### addEvent
```java
public boolean addEvent(String eventName, String eventDate, String category, String eventType, String teamA, String teamB, String description)
```
Adds a new event to the calendar.

- **Parameters**:
  - `eventName`: Name of the event
  - `eventDate`: Date of the event
  - `category`: Category of the event
  - `eventType`: Type of the event
  - `teamA`: Name of the first team
  - `teamB`: Name of the second team
  - `description`: Description of the event
- **Returns**: True if the event was added successfully, false otherwise
- **Implementation Details**:
  - Calls the server-side service to add the event
  - If successful, sends notification for the new event using NotificationManager
  - Sends notifications to admin and manager users
  - Sends a system notification visible to any logged-in user
  - Updates lastErrorMessage if the operation fails

##### getLastErrorMessage
```java
public String getLastErrorMessage()
```
Gets the last error message.

- **Returns**: The last error message string

## Usage Example
```java
EventCalendarService calendarService = new EventCalendarService();

// Get events for May 2023
List<Map<String, Object>> mayEvents = calendarService.getEventsForMonth(2023, 5);

// Get VIP events for December 2023
List<Map<String, Object>> decVipEvents = calendarService.getVipEventsForMonth(2023, 12);

// Get events for today
List<Map<String, Object>> todayEvents = calendarService.getEventsForDate(LocalDate.now());

// Add a new event to the calendar
boolean success = calendarService.addEvent(
    "Football Championship",  // Event name
    "2023-06-15 18:00:00",   // Event date
    "Sports",                // Category
    "Match",                 // Event type
    "Eagles",                // Team A
    "Lions",                 // Team B
    "Annual championship match" // Description
);

if (!success) {
    String error = calendarService.getLastErrorMessage();
    System.out.println("Failed to add event: " + error);
}
```

## Dependencies
- `server.CalendarEventServer`: Server component for calendar event operations
- `server.NotificationManager`: For sending notifications
- `server.notification.NotificationType`: For categorizing notifications
- `java.time.LocalDate`: For date handling
- `java.util.*`: For collections and data structures
