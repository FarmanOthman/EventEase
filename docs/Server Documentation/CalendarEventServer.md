# CalendarEventServer

## Overview
The `CalendarEventServer` class is responsible for managing calendar events within the application. It provides functionality for retrieving, filtering, and creating events based on date criteria, and also handles event notifications.

## Class Structure

### Main Class: CalendarEventServer
This class handles calendar event operations and interacts with the database using the QueryBuilder.

#### Fields
- `queryBuilder`: An instance of QueryBuilder used for database operations
- `lastErrorMessage`: Stores the last error message for error reporting

#### Constructor
- `CalendarEventServer()`: Initializes a new CalendarEventServer with a QueryBuilder instance and an empty error message.

#### Methods

##### getEventsForMonth
```java
public List<Map<String, Object>> getEventsForMonth(int year, int month)
```
Retrieves all events for a specific month and year from the database.

- **Parameters**:
  - `year`: The year to query (e.g., 2023)
  - `month`: The month to query (1-12)
- **Returns**: A List of Maps, where each Map represents an event with its properties
- **Implementation Details**:
  - Creates a SQL date range for the month
  - Uses a custom SQL query with prepared statements for security
  - Connects directly to the SQLite database
  - Converts SQL results to a List of Map objects
  - Handles errors and returns an empty list on failure

##### getVipEventsForMonth
```java
public List<Map<String, Object>> getVipEventsForMonth(int year, int month)
```
Retrieves all VIP events for a specific month and year.

- **Parameters**:
  - `year`: The year to query
  - `month`: The month to query (1-12)
- **Returns**: A List of Maps, where each Map represents a VIP event
- **Implementation Details**:
  - Calls `getEventsForMonth` to get all events
  - Filters the results to include only events with a "VIP" category
  - Handles errors and returns an empty list on failure

##### getEventsForDate
```java
public List<Map<String, Object>> getEventsForDate(LocalDate date)
```
Retrieves all events scheduled for a specific date.

- **Parameters**:
  - `date`: The LocalDate to query
- **Returns**: A List of Maps, where each Map represents an event on the specified date
- **Implementation Details**:
  - Converts the LocalDate to a string in YYYY-MM-DD format
  - Uses a SQL LIKE query with prepared statements
  - Connects directly to the SQLite database
  - Converts SQL results to a List of Map objects
  - Handles errors and returns an empty list on failure

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
- **Returns**: `true` if the event was added successfully, `false` otherwise
- **Implementation Details**:
  - Creates a HashMap with all event details
  - Sets timestamps for created_at and updated_at
  - Inserts the event into the database using the QueryBuilder
  - Sends notifications to admin and manager users about the new event
  - Sends a system notification visible to any logged-in user

##### getLastErrorMessage
```java
public String getLastErrorMessage()
```
Gets the last error message that occurred during an operation.

- **Returns**: The last error message as a String

## Usage Example
```java
CalendarEventServer calendarServer = new CalendarEventServer();

// Get events for October 2023
List<Map<String, Object>> octoberEvents = calendarServer.getEventsForMonth(2023, 10);

// Get VIP events for December 2023
List<Map<String, Object>> decemberVipEvents = calendarServer.getVipEventsForMonth(2023, 12);

// Get events for a specific date
LocalDate today = LocalDate.now();
List<Map<String, Object>> todaysEvents = calendarServer.getEventsForDate(today);

// Add a new event
boolean success = calendarServer.addEvent(
    "Football Championship",
    "2023-11-20 18:00:00",
    "Sports",
    "Football",
    "Team Eagles",
    "Team Lions",
    "Annual championship final match"
);

if (!success) {
    String errorMessage = calendarServer.getLastErrorMessage();
    System.out.println("Failed to add event: " + errorMessage);
}
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `java.time.LocalDate`: Used for date operations
- `java.util.ArrayList`: Used for list operations
- `java.util.HashMap`: Used for storing key-value pairs
- `java.util.List`: Used for handling lists of data
- `java.util.Map`: Used for handling key-value mappings
- `java.sql.Connection`: Used for database connectivity
- `java.sql.PreparedStatement`: Used for SQL query preparation
- `java.sql.ResultSet`: Used for SQL query results
- `server.notification.NotificationType`: Used for categorizing notifications
- `server.NotificationManager`: Used for sending notifications
