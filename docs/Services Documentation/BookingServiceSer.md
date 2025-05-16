# BookingServiceSer

## Overview
The `BookingServiceSer` class serves as a mediator between the UI and the server-side code, handling all booking operations in the application. It communicates with the `BookingServer` class in the server layer to perform booking-related operations and provides additional validation and helper methods.

## Class Structure

### Main Class: BookingServiceSer
This class handles booking operations and acts as a bridge between the UI and server layer.

#### Fields
- `bookingService`: An instance of BookingServer used for performing booking operations
- `eventServiceSer`: An instance of EventServiceSer used for retrieving event information
- `queryBuilder`: An instance of QueryBuilder used for direct database operations
- `lastErrorMessage`: Stores the last error message for error reporting

#### Constructor
- `BookingServiceSer()`: Initializes a new BookingServiceSer with the required dependencies.

#### Methods

##### getAllEvents
```java
public List<Map<String, Object>> getAllEvents()
```
Loads all events for the booking dropdown.

- **Returns**: List of all available events
- **Implementation Details**:
  - Delegates to eventServiceSer.getAllEvents()

##### createBooking
```java
public boolean createBooking(String customerName, String selectedEvent, String selectedPriceCategory, int customerId, int eventId, String ticketType)
```
Creates a new booking in the system.

- **Parameters**:
  - `customerName`: Name of the customer
  - `selectedEvent`: Name of the selected event
  - `selectedPriceCategory`: Price category of the ticket (e.g., VIP, Premium, Standard)
  - `customerId`: ID of the customer making the booking
  - `eventId`: ID of the event being booked
  - `ticketType`: Type of ticket (e.g., Regular or VIP)
- **Returns**: True if booking succeeds, false otherwise
- **Implementation Details**:
  - Delegates to bookingService.addBooking() with error handling
  - Updates lastErrorMessage if booking fails

##### getPricingOptions
```java
public String[] getPricingOptions()
```
Gets pricing options available for booking.

- **Returns**: An array of available price categories
- **Implementation Details**:
  - Returns predefined price category strings including:
    - "Select Price Category"
    - "VIP - $25"
    - "Regular - Premium - $15"
    - "Regular - Standard - $10"

##### getTicketTypeFromPriceCategory
```java
public String getTicketTypeFromPriceCategory(String priceCategory)
```
Determines the ticket type based on the selected price category.

- **Parameters**:
  - `priceCategory`: The selected price category
- **Returns**: The ticket type ("VIP" or "Regular")
- **Implementation Details**:
  - Maps price categories to ticket types defined in the database schema
  - Returns "VIP" for VIP categories
  - Returns "Regular" for Regular, Premium, or Standard categories

##### getLastErrorMessage
```java
public String getLastErrorMessage()
```
Gets the last error message.

- **Returns**: The last error message string

##### getCustomerById
```java
public Map<String, Object> getCustomerById(int customerId)
```
Gets customer information by ID.

- **Parameters**:
  - `customerId`: The ID of the customer to retrieve
- **Returns**: Map containing customer details or null if not found
- **Implementation Details**:
  - Creates a filter map with the customer ID
  - Uses queryBuilder to select customer information
  - Returns null with error message if customer not found

##### getEventById
```java
public Map<String, Object> getEventById(int eventId)
```
Gets event information by ID.

- **Parameters**:
  - `eventId`: The ID of the event to retrieve
- **Returns**: Map containing event details or null if not found
- **Implementation Details**:
  - Creates a filter map with the event ID
  - Uses queryBuilder to select event information
  - Returns null with error message if event not found

##### getEventDetails
```java
public Map<String, Object> getEventDetails(int eventId)
```
Gets detailed event information by event ID.

- **Parameters**:
  - `eventId`: The ID of the event to retrieve
- **Returns**: Map containing detailed event information or null if not found
- **Implementation Details**:
  - Creates a filter map with the event ID
  - Uses queryBuilder to select event details
  - Returns null with error message if event not found

##### canCreateTicketForEvent
```java
public boolean canCreateTicketForEvent(int eventId)
```
Validates if a ticket can be created for the specified event.

- **Parameters**:
  - `eventId`: The ID of the event to validate
- **Returns**: True if a ticket can be created, false otherwise
- **Implementation Details**:
  - Gets event details
  - Checks if the event is a Match type
  - For Match events, verifies that no tickets exist already (enforcing one ticket per match constraint)
  - Updates lastErrorMessage if validation fails

## Usage Example
```java
BookingServiceSer bookingService = new BookingServiceSer();

// Get all events for a dropdown
List<Map<String, Object>> allEvents = bookingService.getAllEvents();

// Get pricing options
String[] priceOptions = bookingService.getPricingOptions();

// Create a booking
boolean success = bookingService.createBooking(
    "John Doe",                // Customer name
    "Football Championship",   // Event name
    "VIP - $25",               // Price category
    101,                       // Customer ID
    201,                       // Event ID
    "VIP"                      // Ticket type
);

if (!success) {
    String error = bookingService.getLastErrorMessage();
    System.out.println("Booking failed: " + error);
}

// Get customer details
Map<String, Object> customerDetails = bookingService.getCustomerById(101);

// Check if ticket can be created for an event
if (bookingService.canCreateTicketForEvent(201)) {
    // Proceed with ticket creation
}
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `server.BookingServer`: Server-side component for booking operations
- `services.EventServiceSer`: Service for event-related operations
- `java.util.HashMap`: Used for storing key-value pairs
- `java.util.List`: Used for handling lists of data
- `java.util.Map`: Used for handling key-value mappings
