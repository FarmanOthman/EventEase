# EventServiceSer

## Overview
The `EventServiceSer` class serves as a mediator between the UI and server-side code, handling all database and business logic operations related to events. It provides functionality for retrieving, filtering, adding, editing, and deleting events in the application.

## Class Structure

### Main Class: EventServiceSer
This class handles event operations and acts as a bridge between the UI and server layer.

#### Fields
- `eventService`: An instance of EventServer used for event operations
- `upcomingEventService`: An instance of UpcomingEventServer used for upcoming event operations
- `lastErrorMessage`: Stores the last error message for error reporting

#### Constructor
- `EventServiceSer()`: Initializes a new EventServiceSer with the required dependencies.

#### Methods

##### getEventTypes
```java
public List<String> getEventTypes(String category)
```
Gets event types from the database based on category.

- **Parameters**:
  - `category`: The event category to filter by
- **Returns**: A list of event types
- **Implementation Details**:
  - Currently returns fixed values ("Match", "Event") based on database schema
  - Includes fallback to default values if database query fails

##### getEventCategories
```java
public List<String> getEventCategories()
```
Gets event categories from the database.

- **Returns**: A list of event categories
- **Implementation Details**:
  - Uses upcomingEventService to fetch categories from the database
  - Falls back to default values ("Regular", "VIP") if no categories are found or an error occurs

##### getAllEvents
```java
public List<Map<String, Object>> getAllEvents()
```
Gets a list of all events from the database.

- **Returns**: A list of all events
- **Implementation Details**:
  - Uses upcomingEventService to fetch all events
  - Updates lastErrorMessage if an error occurs
  - Returns an empty list on error

##### getEventsByCategory
```java
public List<Map<String, Object>> getEventsByCategory(String category)
```
Gets a list of events filtered by category.

- **Parameters**:
  - `category`: The event category to filter by
- **Returns**: A list of events matching the category
- **Implementation Details**:
  - Uses upcomingEventService to fetch events filtered by category
  - Updates lastErrorMessage if an error occurs
  - Returns an empty list on error

##### addEvent
```java
public boolean addEvent(String eventName, String eventDate, String teamA, String teamB, String eventDescription, String eventCategory, String eventType)
```
Adds a new event to the system.

- **Parameters**:
  - `eventName`: Name of the event
  - `eventDate`: Date of the event
  - `teamA`: First team name
  - `teamB`: Second team name
  - `eventDescription`: Description of the event
  - `eventCategory`: Category of the event
  - `eventType`: Type of the event
- **Returns**: True if the event was added successfully, false otherwise
- **Implementation Details**:
  - Uses eventService to add the event to the database
  - Sends notifications about the new event through NotificationManager
  - Updates lastErrorMessage if an error occurs

##### editEvent
```java
public boolean editEvent(int eventId, String eventName, String eventDate, String teamA, String teamB, String eventDescription, String eventCategory, String eventType)
```
Edits an existing event.

- **Parameters**:
  - `eventId`: ID of the event to edit
  - `eventName`: Updated name of the event
  - `eventDate`: Updated date of the event
  - `teamA`: Updated first team name
  - `teamB`: Updated second team name
  - `eventDescription`: Updated description of the event
  - `eventCategory`: Updated category of the event
  - `eventType`: Updated type of the event
- **Returns**: True if the event was updated successfully, false otherwise
- **Implementation Details**:
  - Uses upcomingEventService to update the event in the database
  - Sends notifications about the updated event through NotificationManager
  - Updates lastErrorMessage if an error occurs

##### deleteEvent
```java
public boolean deleteEvent(int eventId)
```
Deletes an event from the system.

- **Parameters**:
  - `eventId`: ID of the event to delete
- **Returns**: True if the event was deleted successfully, false otherwise
- **Implementation Details**:
  - Uses upcomingEventService to delete the event from the database
  - Sends notifications about the deleted event through NotificationManager
  - Updates lastErrorMessage if an error occurs

##### getEventById
```java
public Map<String, Object> getEventById(int eventId)
```
Gets event details by ID.

- **Parameters**:
  - `eventId`: ID of the event to retrieve
- **Returns**: Map containing the event details or null if not found
- **Implementation Details**:
  - Uses upcomingEventService to fetch event details
  - Updates lastErrorMessage if an error occurs or event is not found

##### getLastErrorMessage
```java
public String getLastErrorMessage()
```
Gets the last error message.

- **Returns**: The last error message string

## Usage Example
```java
EventServiceSer eventService = new EventServiceSer();

// Get event types for a category
List<String> eventTypes = eventService.getEventTypes("Sports");

// Get all event categories
List<String> categories = eventService.getEventCategories();

// Get all events
List<Map<String, Object>> allEvents = eventService.getAllEvents();

// Get events by category
List<Map<String, Object>> sportsEvents = eventService.getEventsByCategory("Sports");

// Add a new event
boolean addSuccess = eventService.addEvent(
    "Football Championship",   // Event name
    "2023-06-15 18:00:00",    // Event date
    "Eagles",                 // Team A
    "Lions",                  // Team B
    "Annual championship match", // Description
    "Sports",                 // Category
    "Match"                   // Event type
);

// Edit an existing event
boolean editSuccess = eventService.editEvent(
    101,                      // Event ID
    "Football Championship Final", // Updated name
    "2023-06-16 19:00:00",   // Updated date
    "Eagles",                // Team A
    "Lions",                 // Team B
    "Championship final match", // Updated description
    "Sports",                // Category
    "Match"                  // Event type
);

// Delete an event
boolean deleteSuccess = eventService.deleteEvent(101);

// Get event details by ID
Map<String, Object> event = eventService.getEventById(102);

if (event != null) {
    System.out.println("Event: " + event.get("event_name"));
} else {
    String error = eventService.getLastErrorMessage();
    System.out.println("Error: " + error);
}
```

## Dependencies
- `server.EventServer`: Server component for event operations
- `server.NotificationManager`: For sending notifications
- `server.UpcomingEventServer`: Server component for upcoming event operations
- `server.notification.NotificationType`: For categorizing notifications
- `java.util.ArrayList`: For list operations
- `java.util.Arrays`: For array operations
- `java.util.List`: For handling lists of data
- `java.util.Map`: For handling key-value mappings
