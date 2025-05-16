# Server Documentation Index

This directory contains documentation for all server-side components in the EventEase application. The server package is responsible for providing business logic, database interaction, and processing functionalities.

## Core Server Components

| Component | Description |
|-----------|-------------|
| [AuthenticationServer](AuthenticationServer.md) | Handles user authentication and authorization in the application, including login, registration, and role management. |
| [BookingServer](BookingServer.md) | Manages ticket booking operations, sales tracking, and notification of new bookings. |
| [CalendarEventServer](CalendarEventServer.md) | Provides calendar-specific event operations including date-based filtering and VIP event identification. |
| [EventServer](EventServer.md) | Core server for event management operations and customer information handling. |
| [UpcomingEventServer](UpcomingEventServer.md) | Handles all upcoming event-related operations including filtering, management, and booking. |

## Utility and Helper Components

| Component | Description |
|-----------|-------------|
| [InputValidator](InputValidator.md) | Provides validation methods for various types of user inputs. |
| [NotificationManager](NotificationManager.md) | Manages system notifications and implements the Observer pattern for real-time notification delivery. |

## Reporting and Export Components

| Component | Description |
|-----------|-------------|
| [ExcelExportService](ExcelExportService.md) | Facilitates data export to Excel format with custom styling and analysis sections. |
| [PDFExportServer](PDFExportServer.md) | Handles PDF document generation with professional formatting and summary sections. |
| [ReportServer](ReportServer.md) | Manages reporting functionality, particularly for sales reports. |
| [SalesAnalysis](SalesAnalysis.md) | Provides sales data analysis and reporting capabilities. |

## Architecture Overview

The server package implements a layered architecture:

1. **Presentation Layer**: UI components that interact with these server classes
2. **Business Logic Layer**: The server classes documented here
3. **Data Access Layer**: Database interactions via QueryBuilder and other database classes

Most server components follow these design patterns:

- **Singleton Pattern**: Used in NotificationManager for centralized notification handling
- **Observer Pattern**: Implemented in NotificationManager for real-time updates
- **Repository Pattern**: Used for data access abstraction
- **Facade Pattern**: Server classes provide simplified interfaces to complex subsystems

## Common Interactions

Server components typically interact with:

- **Database Package**: For data persistence and retrieval
- **UI Components**: To provide data and functionality to the interface
- **Utility Classes**: For common operations like validation and formatting

All server components focus on maintaining separation of concerns, with each class having a specific responsibility in the application's functionality.
