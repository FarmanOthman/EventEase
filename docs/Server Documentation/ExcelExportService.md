# ExcelExportService

## Overview
The `ExcelExportService` class is responsible for exporting data to Excel format in the application. It provides comprehensive functionality for generating professionally formatted Excel documents with custom formatting, data analysis, and summary sections.

## Class Structure

### Main Class: ExcelExportService
This class handles all operations related to Excel generation and export.

#### Methods

##### exportToExcel
```java
public boolean exportToExcel(List<Map<String, Object>> data, String filePath, String sheetName, String[] columnNames)
```
Exports data to an Excel file with the provided filename.

- **Parameters**:
  - `data`: The data to export as a list of maps
  - `filePath`: The full path where to save the Excel file
  - `sheetName`: The name of the Excel sheet
  - `columnNames`: The names of the columns
- **Returns**: True if export was successful, false otherwise
- **Implementation Details**:
  - Creates a new XSSFWorkbook (XLSX format)
  - Creates a sheet with the provided name
  - Creates custom styles for headers, data cells, dates, and currency values
  - Adds a header row with styling
  - Adds data rows with appropriate formatting based on value type and column name
  - Adds an analysis section for sales reports
  - Auto-sizes columns for better readability
  - Uses try-with-resources for proper resource management
  - Includes error handling with try-catch

##### createHeaderStyle (private)
```java
private CellStyle createHeaderStyle(Workbook workbook)
```
Creates a style for header cells.

- **Parameters**:
  - `workbook`: The workbook to create the style in
- **Returns**: A CellStyle for header cells
- **Implementation Details**:
  - Sets a royal blue background
  - Uses white, bold text
  - Creates a professional look for table headers

##### createDataCellStyle (private)
```java
private CellStyle createDataCellStyle(Workbook workbook)
```
Creates a style for data cells.

- **Parameters**:
  - `workbook`: The workbook to create the style in
- **Returns**: A CellStyle for data cells
- **Implementation Details**:
  - Adds thin borders on all sides
  - Creates a clean, professional look for data cells

##### createDateStyle (private)
```java
private CellStyle createDateStyle(Workbook workbook)
```
Creates a style for date cells.

- **Parameters**:
  - `workbook`: The workbook to create the style in
- **Returns**: A CellStyle for date cells
- **Implementation Details**:
  - Clones the data cell style
  - Adds date formatting (YYYY-MM-DD)

##### createCurrencyStyle (private)
```java
private CellStyle createCurrencyStyle(Workbook workbook)
```
Creates a style for currency cells.

- **Parameters**:
  - `workbook`: The workbook to create the style in
- **Returns**: A CellStyle for currency cells
- **Implementation Details**:
  - Clones the data cell style
  - Adds currency formatting ($#,##0.00)

##### formatCell (private)
```java
private void formatCell(Cell cell, Object value, String columnName, CellStyle dateStyle, CellStyle currencyStyle, CellStyle defaultStyle)
```
Formats a cell based on the value type and column name.

- **Parameters**:
  - `cell`: The cell to format
  - `value`: The value to put in the cell
  - `columnName`: The name of the column (used to determine formatting)
  - `dateStyle`: The style to use for date cells
  - `currencyStyle`: The style to use for currency cells
  - `defaultStyle`: The default style to use
- **Implementation Details**:
  - Handles null values with empty strings
  - Formats dates with date style
  - Formats currency values (price, revenue, amount) with currency style
  - Formats long descriptions with word wrapping
  - Uses default style for other values

##### addAnalysisSection (private)
```java
private void addAnalysisSection(Workbook workbook, Sheet sheet, List<Map<String, Object>> data, String[] columnNames, int startRow)
```
Adds an analysis section to the Excel sheet with summary data.

- **Parameters**:
  - `workbook`: The workbook to add the section to
  - `sheet`: The sheet to add the section to
  - `data`: The data to analyze
  - `columnNames`: The column names
  - `startRow`: The row to start adding the section at
- **Implementation Details**:
  - Creates a header with "Sales Analysis Summary"
  - Calculates total revenue and tickets sold
  - Adds rows for total revenue, total tickets sold, and average revenue per ticket
  - Adds a timestamp for when the report was generated

## Usage Example
```java
ExcelExportService excelExporter = new ExcelExportService();

// Prepare sample data
List<Map<String, Object>> salesData = getSalesData();  // Replace with actual data source

// Define column names for the Excel file
String[] columns = {
    "Date", "Event", "Team A", "Team B", "Tickets Sold", "Revenue", "VIP Tickets"
};

// Export to Excel
boolean success = excelExporter.exportToExcel(
    salesData,
    "C:/Reports/SalesReport.xlsx",
    "Sales Report",
    columns
);

if (success) {
    System.out.println("Excel export successful!");
} else {
    System.out.println("Failed to export Excel file.");
}
```

## Dependencies
- `org.apache.poi.ss.usermodel.*`: Core POI classes for Excel handling
- `org.apache.poi.xssf.usermodel.XSSFWorkbook`: For XLSX format support
- `java.io.FileOutputStream`: For writing the Excel file
- `java.io.IOException`: For handling IO exceptions
- `java.text.SimpleDateFormat`: For date formatting
- `java.util.List`: For handling lists of data
- `java.util.Map`: For handling key-value mappings
- `java.util.Date`: For date handling
