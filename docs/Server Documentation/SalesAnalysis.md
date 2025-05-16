# SalesAnalysis

## Overview
The `SalesAnalysis` class is responsible for retrieving and analyzing sales data in the application. It provides methods to fetch sales information from different database tables or generate sample data when required.

## Class Structure

### Main Class: SalesAnalysis
This class handles operations related to sales data analysis.

#### Methods

##### getSalesReportData
```java
public List<Map<String, Object>> getSalesReportData()
```
Gets sales data for analysis from the appropriate table based on availability.

- **Returns**: A list of maps containing sales report data
- **Implementation Details**:
  - First checks if the Booking and Event tables exist
  - If they exist, retrieves data from these tables
  - If not, checks if the SalesReport table exists and retrieves from there
  - If no tables are available, generates sample data
  - Uses error handling to return sample data if any exception occurs

##### isTableExists (private)
```java
private boolean isTableExists(String tableName) throws SQLException
```
Checks if a specific table exists in the database.

- **Parameters**:
  - `tableName`: The name of the table to check
- **Returns**: True if the table exists, false otherwise
- **Throws**: SQLException if database access fails
- **Implementation Details**:
  - Uses database metadata to check if the table exists
  - Properly manages connection resources with try-with-resources

##### getDataFromBookingTable (private)
```java
private List<Map<String, Object>> getDataFromBookingTable()
```
Retrieves sales data from the Booking and Event tables.

- **Returns**: A list of maps containing sales data
- **Implementation Details**:
  - Uses a complex SQL query to join the Booking and Event tables
  - Calculates aggregated metrics like total tickets sold and revenue
  - Segments tickets by category (VIP, Standard, Premium)
  - Groups results by event
  - Maps SQL result set to a list of maps for easy processing
  - Includes error handling with try-catch

##### getDataFromSalesReportTable (private)
```java
private List<Map<String, Object>> getDataFromSalesReportTable()
```
Retrieves sales data from the SalesReport table.

- **Returns**: A list of maps containing sales data
- **Implementation Details**:
  - Uses a simple SQL query to select all records from SalesReport table
  - Maps the SalesReport fields to the format expected by the UI
  - Approximates ticket category distribution since detailed data is not available
  - Includes error handling with try-catch

##### generateSampleData (private)
```java
private List<Map<String, Object>> generateSampleData()
```
Generates sample sales data for demonstration when actual data is unavailable.

- **Returns**: A list of maps containing sample sales data
- **Implementation Details**:
  - Creates 10 sample records with realistic but randomized data
  - Uses arrays of team names and opponents
  - Generates random dates within the last year
  - Calculates realistic ticket sales and revenue figures
  - Segments tickets into VIP, standard, and premium categories

## Usage Example
```java
SalesAnalysis salesAnalysis = new SalesAnalysis();

// Get sales report data
List<Map<String, Object>> salesData = salesAnalysis.getSalesReportData();

// Process and display the data
for (Map<String, Object> event : salesData) {
    System.out.println("Event: " + event.get("team_a") + " vs " + event.get("team_b"));
    System.out.println("Date: " + event.get("event_date"));
    System.out.println("Tickets Sold: " + event.get("total_ticket_sold"));
    System.out.println("Revenue: $" + event.get("total_revenue"));
    System.out.println("VIP Tickets: " + event.get("vip_tickets"));
    System.out.println("Standard Tickets: " + event.get("standard_tickets"));
    System.out.println("Premium Tickets: " + event.get("premium_tickets"));
    System.out.println("-------------------------------------------------");
}
```

## Dependencies
- `database.*`: For database access
- `java.sql.*`: For SQL operations
- `java.util.*`: For collections and date handling
- `java.util.Calendar`: For date calculations
- `java.util.Random`: For generating random values
