# PDFExportServer

## Overview
The `PDFExportServer` class is responsible for exporting data to PDF format in the application. It provides comprehensive functionality for generating professionally formatted PDF documents with tables, headers, and summary sections.

## Class Structure

### Main Class: PDFExportServer
This class handles all operations related to PDF generation and export.

#### Constants
- `TITLE_FONT`: Font used for document titles (Helvetica Bold, 18pt, dark gray)
- `HEADER_FONT`: Font used for table headers (Helvetica Bold, 12pt, white)
- `NORMAL_FONT`: Font used for regular text (Helvetica, 10pt, black)
- `FOOTER_FONT`: Font used for footer text (Helvetica Oblique, 8pt, gray)

#### Methods

##### exportToPDF
```java
public boolean exportToPDF(List<Map<String, Object>> data, String filePath, String title, String[] columnNames)
```
Exports data to a PDF file.

- **Parameters**:
  - `data`: Data to export as a list of maps
  - `filePath`: Path where to save the PDF
  - `title`: Title of the document
  - `columnNames`: Column headers for the table
- **Returns**: True if export was successful, false otherwise
- **Implementation Details**:
  - Creates a new PDF document
  - Adds a title with custom formatting
  - Adds a generated date with right alignment
  - Generates a data table with the provided column names and data
  - Adds a summary section if data is present
  - Adds a footer with application information
  - Uses try-catch for error handling with DocumentException and IOException

##### createDataTable (private)
```java
private PdfPTable createDataTable(List<Map<String, Object>> data, String[] columnNames)
```
Creates a table with the provided data.

- **Parameters**:
  - `data`: The data to put in the table
  - `columnNames`: The column headers
- **Returns**: A PdfPTable object containing the formatted table
- **Implementation Details**:
  - Sets up a table with columns equal to the number of column names
  - Creates a header row with blue background and white text
  - Adds data rows with proper formatting based on value type
  - Aligns numeric values to the right
  - Maps display column names to database column names

##### mapColumnName (private)
```java
private String mapColumnName(String displayColumnName)
```
Maps display column names to database column names.

- **Parameters**:
  - `displayColumnName`: The display name of the column
- **Returns**: The corresponding database column name
- **Implementation Details**:
  - Uses a switch statement to map common display names
  - Handles cases like "Date" → "event_date", "Team A" → "team_a", etc.
  - For unmapped names, converts to lowercase and replaces spaces with underscores

##### createSummarySection (private)
```java
private PdfPTable createSummarySection(List<Map<String, Object>> data)
```
Creates a summary section with totals and averages.

- **Parameters**:
  - `data`: The data to summarize
- **Returns**: A PdfPTable object containing the summary information
- **Implementation Details**:
  - Creates a two-column table with a header
  - Calculates totals for revenue and tickets
  - Adds total records count
  - Adds total revenue with currency formatting
  - Adds total tickets sold
  - Calculates and adds average revenue per ticket
  - Handles various data types and formats when extracting values

##### addSummaryRow (private)
```java
private void addSummaryRow(PdfPTable table, String label, String value)
```
Adds a row to the summary table.

- **Parameters**:
  - `table`: The table to add the row to
  - `label`: The label for the row
  - `value`: The value for the row
- **Implementation Details**:
  - Creates two cells with light gray background
  - Sets the label cell with left alignment
  - Sets the value cell with right alignment
  - Handles null values with a dash placeholder

##### formatValue (private)
```java
private String formatValue(Object value, String columnName)
```
Formats a value based on its type and column name.

- **Parameters**:
  - `value`: The value to format
  - `columnName`: The name of the column (used to determine formatting)
- **Returns**: A formatted string representation of the value
- **Implementation Details**:
  - Handles null values with a dash placeholder
  - Formats dates with a consistent YYYY-MM-DD format
  - Formats monetary values with a dollar sign and two decimal places
  - Formats numeric values with two decimal places
  - Truncates long descriptions with ellipsis
  - Returns plain string representation for other values

## Usage Example
```java
PDFExportServer pdfExporter = new PDFExportServer();

// Prepare sample data
List<Map<String, Object>> salesData = getSalesData();  // Replace with actual data source

// Define column names for the PDF
String[] columns = {
    "Date", "Event", "Team A", "Team B", "Tickets Sold", "Revenue", "VIP Tickets"
};

// Export to PDF
boolean success = pdfExporter.exportToPDF(
    salesData,
    "C:/Reports/SalesReport.pdf",
    "Monthly Sales Report - April 2023",
    columns
);

if (success) {
    System.out.println("PDF export successful!");
} else {
    System.out.println("Failed to export PDF.");
}
```

## Dependencies
- `java.io.FileOutputStream`: For writing the PDF to a file
- `java.io.IOException`: For handling IO exceptions
- `java.text.SimpleDateFormat`: For date formatting
- `java.util.Date`: For current date/time
- `java.util.List`: For handling lists of data
- `java.util.Map`: For handling key-value mappings
- `com.itextpdf.text.*`: Core iText classes for PDF generation
- `com.itextpdf.text.pdf.*`: PDF-specific iText classes
