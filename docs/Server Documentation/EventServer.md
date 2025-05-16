# EventServer

## Overview
The `EventServer` class is responsible for managing events in the application. It provides functionality for adding events to the database and includes a nested class for customer information management.

## Class Structure

### Main Class: EventServer
This class handles event-related operations and interacts with the database using the QueryBuilder.

#### Constructor
- `EventServer()`: Initializes a new EventServer with a QueryBuilder instance for database operations.

#### Methods
- `addEvent(String eventName, String eventDate, String teamA, String teamB, String eventDescription, String eventCategory, String eventType)`: Adds a new event to the database.
  - **Parameters**:
    - `eventName`: Name of the event
    - `eventDate`: Date of the event in string format ("YYYY-MM-DD HH:MM:SS")
    - `teamA`: First team name
    - `teamB`: Second team name
    - `eventDescription`: Description of the event
    - `eventCategory`: Category of the event
    - `eventType`: Type of the event
  - **Throws**: Exception if the operation fails or if teamA is the same as teamB
  - **Implementation Details**: 
    - Validates that teamA and teamB are not the same
    - Creates a HashMap with all event details
    - Inserts the event into the database using the QueryBuilder
    - Includes timestamps for created_at and updated_at fields

### Nested Class: CustomInformationService
This inner class handles customer-related operations, storing and retrieving customer information.

#### Constructor
- `CustomInformationService()`: Initializes a new customer information service with a QueryBuilder instance.

#### Methods
- `addCustomer(String firstName, String lastName, String contactNumber, String email)`: Adds a new customer to the database.
  - **Parameters**:
    - `firstName`: Customer's first name
    - `lastName`: Customer's last name
    - `contactNumber`: Customer's contact number
    - `email`: Customer's email address
  - **Returns**: The ID of the newly added customer
  - **Throws**: Exception if the operation fails
  - **Implementation Details**: 
    - Creates a HashMap with all customer details
    - Inserts the customer into the database
    - Returns the ID of the inserted customer

- `getCustomerId(String firstName, String lastName, String contactNumber, String email)`: Gets the ID of a customer based on their information.
  - **Parameters**:
    - `firstName`: Customer's first name
    - `lastName`: Customer's last name
    - `contactNumber`: Customer's contact number
    - `email`: Customer's email address
  - **Returns**: The customer ID as an integer
  - **Throws**: Exception if the customer is not found
  - **Implementation Details**: 
    - Creates filters for database lookup
    - Queries the database for matching customers
    - Returns the ID of the first matching customer

- `getCustomerDetails(int customerId)`: Retrieves customer details based on their ID.
  - **Parameters**:
    - `customerId`: The ID of the customer to retrieve
  - **Returns**: A Map containing the customer details
  - **Throws**: Exception if the customer is not found
  - **Implementation Details**:
    - Creates a filter for database lookup by ID
    - Queries the database for the customer record
    - Returns the details of the matched customer

## Usage Example
```java
EventServer eventServer = new EventServer();
try {
    // Adding an event
    eventServer.addEvent("Football Match", "2023-10-15 18:00:00", "Team A", "Team B", 
                         "Championship finals", "Sports", "Football");
                        
    // Using the CustomInformationService
    EventServer.CustomInformationService customerService = new EventServer.CustomInformationService();
    int customerId = customerService.addCustomer("John", "Doe", "1234567890", "john.doe@example.com");
    
    // Getting customer details
    Map<String, Object> customerDetails = customerService.getCustomerDetails(customerId);
} catch (Exception e) {
    e.printStackTrace();
}
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `java.sql.Timestamp`: Used for timestamp generation
- `java.util.HashMap`: Used for storing key-value pairs
- `java.util.List`: Used for handling lists of data
- `java.util.Map`: Used for handling key-value mappings
