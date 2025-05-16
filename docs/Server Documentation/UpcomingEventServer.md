# UpcomingEventServer

## Overview
The `UpcomingEventServer` class is responsible for handling all upcoming event-related operations in the application. It provides comprehensive functionality for fetching, filtering, manipulating, and managing event data in the database.

## Class Structure

### Main Class: UpcomingEventServer
This class handles operations related to upcoming events and interacts with the database using the QueryBuilder.

#### Fields
- `queryBuilder`: An instance of QueryBuilder used for database operations

#### Constructor
- `UpcomingEventServer()`: Initializes a new UpcomingEventServer with a QueryBuilder instance.

#### Methods

##### Event Retrieval

###### getAllEvents
```java
public List<Map<String, Object>> getAllEvents()
```
Fetches all events from the database.

- **Returns**: List of all event records
- **Implementation Details**:
  - Uses queryBuilder to select all events with specified columns from the Event table

###### getEventsByCategory
```java
public List<Map<String, Object>> getEventsByCategory(String category)
```
Fetches events filtered by category.

- **Parameters**:
  - `category`: Event category to filter by
- **Returns**: List of events matching the specified category
- **Implementation Details**:
  - Creates a filter map with the category value
  - Uses queryBuilder.selectWithFilters to get filtered results

###### getEventsByDate
```java
public List<Map<String, Object>> getEventsByDate(String date)
```
Fetches events filtered by a specific date.

- **Parameters**:
  - `date`: Date in YYYY-MM-DD format
- **Returns**: List of events on the specified date
- **Implementation Details**:
  - Creates a filter map with the date value
  - Uses SQLite-style date handling

###### getEventsByLocation
```java
public List<Map<String, Object>> getEventsByLocation(String location)
```
Fetches events filtered by location.

- **Parameters**:
  - `location`: Event location
- **Returns**: Currently returns an empty list since location column doesn't exist in the database

##### Dropdown Helpers

###### getEventCategories
```java
public List<String> getEventCategories()
```
Gets a list of all available categories for dropdowns or filters.

- **Returns**: List of unique category names
- **Implementation Details**:
  - Queries the Event table for all categories
  - Uses a HashSet to ensure uniqueness of categories
  - Converts the set back to a list for the return value

###### getEventLocations
```java
public List<String> getEventLocations()
```
Gets a list of all available event locations for dropdowns or filters.

- **Returns**: Currently returns an empty list since location column doesn't exist in the database

##### Event Details

###### getEventDetails
```java
public Map<String, Object> getEventDetails(int eventId)
```
Fetches detailed information for a specific event.

- **Parameters**:
  - `eventId`: Unique event identifier
- **Returns**: Map containing detailed event information or null if not found
- **Implementation Details**:
  - Creates a filter map with the event_id
  - Uses queryBuilder.selectWithFilters to get the event details
  - Returns the first result or null if no results found

##### Booking

###### bookEvent
```java
public void bookEvent(int customerId, int eventId, int numberOfTickets) throws Exception
```
Books an event for a customer.

- **Parameters**:
  - `customerId`: Customer's ID
  - `eventId`: Event's ID
  - `numberOfTickets`: Number of tickets to book
- **Throws**: Exception if booking fails
- **Implementation Details**:
  - Creates a booking map with customer ID, event ID, tickets count, and timestamp
  - Uses queryBuilder.insert to add the booking to the database

##### Event Management

###### editEvent
```java
public boolean editEvent(int eventId, String eventName, String eventDate, String teamA, String teamB, String eventDescription, String eventCategory, String eventType, String location)
```
Edits an existing event's details.

- **Parameters**:
  - `eventId`: The ID of the event to update
  - `eventName`: New event name
  - `eventDate`: New event date
  - `teamA`: New team A name
  - `teamB`: New team B name
  - `eventDescription`: New event description
  - `eventCategory`: New event category
  - `eventType`: New event type
  - `location`: New location (optional, can be null)
- **Returns**: True if the update was successful, false otherwise
- **Implementation Details**:
  - Validates that teamA and teamB are not the same
  - Creates a map with updated values
  - Checks if location column exists before including it
  - Uses queryBuilder.update to update the event in the database
  - Has error handling and retry logic for location column issues

###### columnExists (private)
```java
private boolean columnExists(String tableName, String columnName)
```
Checks if a column exists in a table.

- **Parameters**:
  - `tableName`: The name of the table to check
  - `columnName`: The name of the column to check
- **Returns**: True if the column exists, false otherwise
- **Implementation Details**:
  - Uses metadata query approach rather than direct selection
  - Queries sqlite_master for table info to extract column information

###### deleteEvent
```java
public boolean deleteEvent(int eventId)
```
Deletes an event from the database.

- **Parameters**:
  - `eventId`: The ID of the event to delete
- **Returns**: True if the deletion was successful, false otherwise
- **Implementation Details**:
  - Checks if the Booking table exists
  - If it exists, checks for and deletes associated bookings
  - Deletes the event from the Event table
  - Includes comprehensive error handling

###### tableExists (private)
```java
private boolean tableExists(String tableName)
```
Checks if a table exists in the database.

- **Parameters**:
  - `tableName`: The name of the table to check
- **Returns**: True if the table exists, false otherwise
- **Implementation Details**:
  - Queries the sqlite_master table to check for the table's existence

## Usage Example
```java
UpcomingEventServer eventServer = new UpcomingEventServer();

// Get all events
List<Map<String, Object>> allEvents = eventServer.getAllEvents();

// Get events by category
List<Map<String, Object>> sportsEvents = eventServer.getEventsByCategory("Sports");

// Get event details
Map<String, Object> eventDetails = eventServer.getEventDetails(101);

// Book an event for a customer
try {
    eventServer.bookEvent(1001, 101, 2);
    System.out.println("Event booked successfully!");
} catch (Exception e) {
    System.out.println("Failed to book event: " + e.getMessage());
}

// Edit an event
boolean updateSuccess = eventServer.editEvent(
    101,
    "Updated Championship Final",
    "2023-12-15 19:00:00",
    "Team Eagles",
    "Team Lions",
    "Annual championship final match - Updated description",
    "Sports",
    "Football",
    null  // No location
);

if (updateSuccess) {
    System.out.println("Event updated successfully!");
} else {
    System.out.println("Failed to update event.");
}

// Delete an event
boolean deleteSuccess = eventServer.deleteEvent(101);
if (deleteSuccess) {
    System.out.println("Event deleted successfully!");
} else {
    System.out.println("Failed to delete event.");
}
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `java.sql.Timestamp`: Used for timestamp generation
- `java.util.ArrayList`: Used for list operations
- `java.util.HashMap`: Used for storing key-value pairs
- `java.util.HashSet`: Used for ensuring uniqueness
- `java.util.List`: Used for handling lists of data
- `java.util.Map`: Used for handling key-value mappings
- `java.util.Set`: Used for storing unique values
