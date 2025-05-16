# SalesDataService

## Overview
The `SalesDataService` class handles all operations related to sales data in the application. It provides methods for retrieving, analyzing, and exporting sales data. The service also initializes sample sales data when needed for demonstration purposes.

## Class Structure

### Main Class: SalesDataService
This class handles sales data operations and provides data for reporting and analysis.

#### Fields
- `queryBuilder`: An instance of QueryBuilder used for database operations
- `lastErrorMessage`: Stores the last error message for error reporting
- `excelExportService`: An instance of ExcelExportService used for exporting data to Excel
- `pdfExportService`: An instance of PDFExportServer used for exporting data to PDF

#### Constructor
- `SalesDataService()`: Initializes a new SalesDataService with the required dependencies and initializes sales data if needed.

#### Methods

##### initializeSalesData (private)
```java
private void initializeSalesData()
```
Initializes sales data if the table is empty.

- **Implementation Details**:
  - Checks if the Sales table already has data
  - If empty, calls generateAndInsertSampleData() to populate with sample data
  - Uses error handling to generate sample data if table doesn't exist

##### generateAndInsertSampleData (private)
```java
private void generateAndInsertSampleData()
```
Generates and inserts sample data into the Sales table.

- **Implementation Details**:
  - Creates a list of sample sales data for the last 30 days
  - Generates data for different ticket categories (Regular, VIP, Premium)
  - Uses realistic randomization for ticket counts and revenue
  - Attempts to insert the generated data into the database
  - Includes error handling

##### getAllSalesData
```java
public List<Map<String, Object>> getAllSalesData()
```
Fetches all sales data from the database.

- **Returns**: List of Maps containing sales data
- **Implementation Details**:
  - Uses QueryBuilder to select data from the Sales table
  - Returns sample data if the database table is empty
  - Uses error handling to generate sample data if query fails

##### generateSampleData (private)
```java
private List<Map<String, Object>> generateSampleData()
```
Generates sample sales data for display when actual data is unavailable.

- **Returns**: List of Maps containing sample sales data
- **Implementation Details**:
  - Creates realistic but randomized sales data
  - Includes data for different time periods and ticket categories
  - Sets appropriate data types for UI compatibility

##### getSalesDataByCategory
```java
public List<Map<String, Object>> getSalesDataByCategory(String category)
```
Gets sales data filtered by category.

- **Parameters**:
  - `category`: The category to filter by (e.g., "VIP", "Regular", "Premium")
- **Returns**: Filtered list of sales data
- **Implementation Details**:
  - Uses QueryBuilder to select data with the specified category filter
  - Returns empty list if no data matches or an error occurs

##### getSalesDataByDateRange
```java
public List<Map<String, Object>> getSalesDataByDateRange(String startDate, String endDate)
```
Gets sales data within a specific date range.

- **Parameters**:
  - `startDate`: Start date in YYYY-MM-DD format
  - `endDate`: End date in YYYY-MM-DD format
- **Returns**: List of sales data within the date range
- **Implementation Details**:
  - Constructs a SQL query to filter by date range
  - Uses PreparedStatement for secure parameter handling
  - Returns empty list if no data matches or an error occurs

##### exportSalesDataToExcel
```java
public boolean exportSalesDataToExcel(List<Map<String, Object>> data, String filePath)
```
Exports sales data to Excel format.

- **Parameters**:
  - `data`: The sales data to export
  - `filePath`: Path where to save the Excel file
- **Returns**: True if export succeeds, false otherwise
- **Implementation Details**:
  - Sets up column names for the Excel report
  - Delegates to excelExportService.exportToExcel()
  - Uses error handling to capture and log failures

##### exportSalesDataToPDF
```java
public boolean exportSalesDataToPDF(List<Map<String, Object>> data, String filePath)
```
Exports sales data to PDF format.

- **Parameters**:
  - `data`: The sales data to export
  - `filePath`: Path where to save the PDF file
- **Returns**: True if export succeeds, false otherwise
- **Implementation Details**:
  - Sets up column names for the PDF report
  - Delegates to pdfExportService.exportToPDF()
  - Uses error handling to capture and log failures

##### getRevenueTotal
```java
public double getRevenueTotal(List<Map<String, Object>> data)
```
Calculates the total revenue from a dataset.

- **Parameters**:
  - `data`: The sales data to analyze
- **Returns**: The total revenue as a double
- **Implementation Details**:
  - Iterates through the dataset and sums the revenue values
  - Handles different data types and formats

##### getTicketsTotal
```java
public int getTicketsTotal(List<Map<String, Object>> data)
```
Calculates the total number of tickets sold from a dataset.

- **Parameters**:
  - `data`: The sales data to analyze
- **Returns**: The total number of tickets sold as an integer
- **Implementation Details**:
  - Iterates through the dataset and sums the tickets_sold values
  - Handles different data types and formats

##### getLastErrorMessage
```java
public String getLastErrorMessage()
```
Gets the last error message.

- **Returns**: The last error message string

## Usage Example
```java
SalesDataService salesDataService = new SalesDataService();

// Get all sales data
List<Map<String, Object>> allSalesData = salesDataService.getAllSalesData();

// Filter sales data by category
List<Map<String, Object>> vipSalesData = salesDataService.getSalesDataByCategory("VIP");

// Get sales data for a specific date range
List<Map<String, Object>> monthlySalesData = salesDataService.getSalesDataByDateRange(
    "2023-04-01", "2023-04-30");

// Calculate totals
double totalRevenue = salesDataService.getRevenueTotal(monthlySalesData);
int totalTickets = salesDataService.getTicketsTotal(monthlySalesData);

System.out.println("Total Revenue: $" + totalRevenue);
System.out.println("Total Tickets: " + totalTickets);

// Export to Excel
boolean excelSuccess = salesDataService.exportSalesDataToExcel(
    monthlySalesData, "C:/Reports/monthly_sales.xlsx");
if (excelSuccess) {
    System.out.println("Excel export successful!");
} else {
    System.out.println("Excel export failed: " + salesDataService.getLastErrorMessage());
}

// Export to PDF
boolean pdfSuccess = salesDataService.exportSalesDataToPDF(
    monthlySalesData, "C:/Reports/monthly_sales.pdf");
if (pdfSuccess) {
    System.out.println("PDF export successful!");
} else {
    System.out.println("PDF export failed: " + salesDataService.getLastErrorMessage());
}
```

## Dependencies
- `database.QueryBuilder`: Used for database operations
- `server.ExcelExportService`: For Excel export functionality
- `server.PDFExportServer`: For PDF export functionality
- `java.util.*`: For collections and data structures
- `java.sql.*`: For direct database access when needed
