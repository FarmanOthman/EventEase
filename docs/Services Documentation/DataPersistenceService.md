# DataPersistenceService

## Overview
The `DataPersistenceService` class is responsible for managing data persistence operations in the application, including data import, export, and backup. It provides functionality for importing data from Excel files, exporting data to Excel and PDF formats, and creating backups of the database.

## Class Structure

### Main Class: DataPersistenceService
This class handles all data persistence operations including imports, exports, and backups.

#### Constants
- `BACKUP_DIRECTORY`: The directory path where backups are stored ("backups/")

#### Fields
- `excelExportService`: An instance of ExcelExportService used for exporting data to Excel
- `pdfExportService`: An instance of PDFExportServer used for exporting data to PDF
- `queryBuilder`: An instance of QueryBuilder used for database operations

#### Constructor
- `DataPersistenceService()`: Initializes a new DataPersistenceService with the required dependencies and ensures the backup directory exists.

#### Methods

##### initializeBackupDirectory (private)
```java
private void initializeBackupDirectory()
```
Creates the backup directory if it doesn't exist.

- **Implementation Details**:
  - Checks if the backup directory exists
  - Creates the directory if it doesn't exist

##### importFromExcel
```java
public ImportResult importFromExcel(String filePath, String entityType)
```
Imports data from an Excel file for the specified entity type.

- **Parameters**:
  - `filePath`: The path to the Excel file
  - `entityType`: The type of entity to import (e.g., "event", "ticket")
- **Returns**: ImportResult containing success flag and message
- **Implementation Details**:
  - Validates the file exists
  - Reads the Excel file and extracts headers and data
  - Delegates to specific import methods based on entity type
  - Handles IOExceptions with appropriate error messages

##### importEvents (private)
```java
private ImportResult importEvents(Sheet sheet, Map<Integer, String> columnMap)
```
Imports event data from an Excel sheet.

- **Parameters**:
  - `sheet`: The Excel sheet containing event data
  - `columnMap`: Mapping of column indices to column names
- **Returns**: ImportResult indicating success or failure
- **Implementation Details**:
  - Reads each row from the sheet
  - Converts Excel data to database-compatible event data
  - Inserts the events into the database
  - Provides error handling and reporting

##### importTickets (private)
```java
private ImportResult importTickets(Sheet sheet, Map<Integer, String> columnMap)
```
Imports ticket data from an Excel sheet.

- **Parameters**:
  - `sheet`: The Excel sheet containing ticket data
  - `columnMap`: Mapping of column indices to column names
- **Returns**: ImportResult indicating success or failure
- **Implementation Details**:
  - Reads each row from the sheet
  - Converts Excel data to database-compatible ticket data
  - Inserts the tickets into the database
  - Provides error handling and reporting

##### exportToExcel
```java
public boolean exportToExcel(List<Map<String, Object>> data, String filePath, String sheetName, String[] columnNames)
```
Exports data to an Excel file.

- **Parameters**:
  - `data`: The data to export
  - `filePath`: The path where the Excel file will be saved
  - `sheetName`: The name of the sheet in the Excel file
  - `columnNames`: The names of the columns for the data
- **Returns**: True if export succeeds, false otherwise
- **Implementation Details**:
  - Delegates to excelExportService.exportToExcel()
  - Provides error handling

##### exportToPDF
```java
public boolean exportToPDF(List<Map<String, Object>> data, String filePath, String title, String[] columnNames)
```
Exports data to a PDF file.

- **Parameters**:
  - `data`: The data to export
  - `filePath`: The path where the PDF file will be saved
  - `title`: The title of the PDF document
  - `sheetName`: The names of the columns for the data
- **Returns**: True if export succeeds, false otherwise
- **Implementation Details**:
  - Delegates to pdfExportService.exportToPDF()
  - Provides error handling

##### createBackup
```java
public BackupResult createBackup(String backupName)
```
Creates a backup of the database.

- **Parameters**:
  - `backupName`: Name for the backup
- **Returns**: BackupResult containing result information
- **Implementation Details**:
  - Uses BackupManager to create database backup
  - Generates a timestamp for the backup
  - Stores the backup in the backup directory
  - Returns result with backup details

##### restoreBackup
```java
public boolean restoreBackup(String backupPath)
```
Restores the database from a backup.

- **Parameters**:
  - `backupPath`: Path to the backup file
- **Returns**: True if restore succeeds, false otherwise
- **Implementation Details**:
  - Uses BackupManager to restore database from backup
  - Provides error handling

##### listBackups
```java
public List<BackupInfo> listBackups()
```
Gets a list of available backups.

- **Returns**: List of BackupInfo objects with backup details
- **Implementation Details**:
  - Scans the backup directory for backup files
  - Extracts information about each backup
  - Returns list of backup details

### Inner Classes

#### ImportResult
Represents the result of an import operation.

##### Fields
- `success`: Boolean indicating if import was successful
- `message`: Result message with details

#### BackupInfo
Represents information about a database backup.

##### Fields
- `name`: Name of the backup
- `path`: File path to the backup
- `date`: Date when the backup was created
- `size`: Size of the backup in bytes

#### BackupResult
Represents the result of a backup operation.

##### Fields
- `success`: Boolean indicating if backup was successful
- `backupInfo`: Information about the created backup
- `message`: Result message with details

## Usage Example
```java
DataPersistenceService persistenceService = new DataPersistenceService();

// Import events from Excel
ImportResult importResult = persistenceService.importFromExcel(
    "C:/Data/events.xlsx", 
    "event"
);
System.out.println("Import result: " + importResult.message);

// Export data to Excel
List<Map<String, Object>> salesData = getSalesData(); // Get from somewhere
boolean excelSuccess = persistenceService.exportToExcel(
    salesData,
    "C:/Reports/sales_report.xlsx",
    "Sales Report",
    new String[] {"Date", "Event", "Tickets Sold", "Revenue"}
);

// Export data to PDF
boolean pdfSuccess = persistenceService.exportToPDF(
    salesData,
    "C:/Reports/sales_report.pdf", 
    "Monthly Sales Report",
    new String[] {"Date", "Event", "Tickets Sold", "Revenue"}
);

// Create a backup
BackupResult backupResult = persistenceService.createBackup("Monthly_Backup");
if (backupResult.success) {
    System.out.println("Backup created at: " + backupResult.backupInfo.path);
}

// List all backups
List<BackupInfo> backups = persistenceService.listBackups();
for (BackupInfo backup : backups) {
    System.out.println("Backup: " + backup.name + " - " + backup.date);
}

// Restore a backup
boolean restoreSuccess = persistenceService.restoreBackup(
    "backups/Monthly_Backup_2023-04-15.db"
);
if (restoreSuccess) {
    System.out.println("Database restored successfully!");
}
```

## Dependencies
- `java.util.*`: Various utility classes for collections and data structures
- `java.io.*`: For file operations
- `java.sql.*`: For database operations
- `java.text.SimpleDateFormat`: For date formatting
- `org.apache.poi.ss.usermodel.*`: For Excel file manipulation
- `database.BackupManager`: For database backup and restore operations
- `database.Database`: For database connectivity
- `database.QueryBuilder`: For database queries
- `server.ExcelExportService`: For Excel export functionality
- `server.PDFExportServer`: For PDF export functionality
