# BookingServer

## Overview
The `BookingServer` class is responsible for handling all aspects of ticket booking and sales tracking within the application. It manages the creation of tickets, updates sales records, and ensures data integrity through input validation.

## Class Structure

### Main Class: BookingServer
This class handles ticket booking operations and interacts with the database using the QueryBuilder.

#### Constructor
- `BookingServer()`: Initializes a new BookingServer with a QueryBuilder instance for database operations.

#### Methods

##### addBooking
```java
public boolean addBooking(String customerName, String selectedEvent, String selectedPriceCategory, int customerId, int eventId, String ticketType)
```
Adds a customer booking for an event to the database.

- **Parameters**:
  - `customerName`: Name of the customer
  - `selectedEvent`: Name of the selected event
  - `selectedPriceCategory`: Price category of the ticket (e.g., VIP, Premium, Standard)
  - `customerId`: ID of the customer making the booking
  - `eventId`: ID of the event being booked
  - `ticketType`: Type of ticket (e.g., Regular or VIP)
- **Returns**: `true` if booking succeeds, `false` otherwise
- **Implementation Details**:
  - Validates all input parameters
  - Determines the ticket price based on the selected price category
  - Checks if a ticket for this event and ticket type already exists
  - Creates a new ticket record if none exists
  - Updates the Sales table to record the sale
  - Sends notifications about the new ticket to relevant users
  - Uses error handling to catch and log exceptions

##### updateSalesTable (private)
```java
private void updateSalesTable(String ticketType, double price)
```
Updates the Sales table when a ticket is sold, aggregating sales by date and category.

- **Parameters**:
  - `ticketType`: Type of ticket sold (e.g., Regular or VIP)
  - `price`: Price of the ticket
- **Implementation Details**:
  - Gets today's date for the sales record
  - Determines the sales category based on ticket type and price
  - Checks if a sales record for today and this category already exists
  - Updates existing records by incrementing ticket count and revenue
  - Creates new records if none exist for today and this category

##### getCategoryFromTicketType (private)
```java
private String getCategoryFromTicketType(String ticketType, double price)
```
Maps ticket type and price to a sales category.

- **Parameters**:
  - `ticketType`: Type of ticket (e.g., Regular or VIP)
  - `price`: Price of the ticket
- **Returns**: A category string (VIP, Premium, or Regular)
- **Implementation Details**:
  - Maps VIP tickets to the "VIP" category
  - Maps Regular tickets with a price of 15.0 to the "Premium" category
  - Maps all other tickets to the "Regular" category

##### validateBookingInputs (private)
```java
private boolean validateBookingInputs(String customerName, String selectedEvent, String selectedPriceCategory, int customerId, int eventId, String ticketType)
```
Validates all booking input parameters.

- **Parameters**: The same as `addBooking` method
- **Returns**: `true` if all inputs are valid, `false` otherwise
- **Implementation Details**:
  - Validates strings using InputValidator
  - Validates price category
  - Validates customer and event IDs are positive integers
  - Validates ticket type is either "Regular" or "VIP"

##### getPriceFromCategory (private)
```java
private double getPriceFromCategory(String priceCategory)
```
Determines the price based on the price category.

- **Parameters**:
  - `priceCategory`: The selected price category (e.g., VIP, Premium, Standard)
- **Returns**: The price as a double
- **Implementation Details**:
  - Returns 25.0 for VIP tickets
  - Returns 15.0 for Premium tickets
  - Returns 10.0 for Standard tickets
  - Attempts to parse price from the category string if standard recognition fails
  - Defaults to 10.0 if parsing fails

##### bookTicket
```java
public boolean bookTicket(String customerName, String eventName, String priceCategory)
```
This method is currently unimplemented and throws an UnsupportedOperationException.

## Usage Example
```java
BookingServer bookingServer = new BookingServer();

// Add a booking for a customer
boolean bookingSuccess = bookingServer.addBooking(
    "John Doe",      // Customer name
    "Football Match", // Event name
    "VIP",           // Price category
    1001,            // Customer ID
    2001,            // Event ID
    "VIP"            // Ticket type
);

if (bookingSuccess) {
    System.out.println("Booking was successful!");
} else {
    System.out.println("Booking failed!");
}
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `java.sql.Timestamp`: Used for timestamp generation
- `java.time.LocalDate`: Used for date operations
- `java.sql.Date`: Used for SQL date operations
- `java.util.HashMap`: Used for storing key-value pairs
- `java.util.List`: Used for handling lists of data
- `java.util.Map`: Used for handling key-value mappings
- `server.notification.NotificationType`: Used for categorizing notifications
- `server.InputValidator`: Used for validating input parameters
- `server.NotificationManager`: Used for sending notifications
