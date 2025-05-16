# ReportServer

## Overview
The `ReportServer` class is responsible for generating and storing sales reports in the application. It provides functionality for adding sales report data to the database for later analysis and export.

## Class Structure

### Main Class: ReportServer
This class handles sales report operations and interacts with the database using the QueryBuilder.

#### Fields
- `queryBuilder`: An instance of QueryBuilder used for database operations

#### Constructor
- `ReportServer()`: Initializes a new ReportServer with a QueryBuilder instance.

#### Methods

##### addSalesReport
```java
public void addSalesReport(String date, int ticketsSold, double revenue, String category)
```
Method to insert a sales report into the database.

- **Parameters**:
  - `date`: The date of the report in the format "YYYY-MM-DD"
  - `ticketsSold`: The number of tickets sold
  - `revenue`: The total revenue generated
  - `category`: The category of the tickets/event (e.g., "VIP")
- **Implementation Details**:
  - Creates a HashMap with all report details
  - Adds timestamps for created_at and updated_at fields
  - Inserts the report data into the SalesReport table
  - Uses try-catch for error handling and logging

##### close
```java
public void close()
```
Closes the database connection when done.

- **Implementation Details**:
  - Calls queryBuilder.closeConnection() to properly close resources

## Usage Example
```java
ReportServer reportServer = new ReportServer();

// Add a sales report
reportServer.addSalesReport(
    "2025-04-15",  // Date
    450,           // Tickets sold
    22500.0,       // Revenue
    "VIP"          // Category
);

// Add another sales report for a different category
reportServer.addSalesReport(
    "2025-04-15",  // Same date
    800,           // Tickets sold
    16000.0,       // Revenue
    "Standard"     // Category
);

// Close the connection when done
reportServer.close();
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `java.sql.Timestamp`: Used for timestamp generation
- `java.util.HashMap`: Used for storing key-value pairs
- `java.util.Map`: Used for handling key-value mappings
