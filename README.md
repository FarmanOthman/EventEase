# EventEase - Event Management System

## Overview

EventEase is a comprehensive Java-based event management system designed to handle stadium events, matches, and ticketing operations. The system provides a robust platform for managing events, bookings, and user roles with a secure authentication system.

## Features

### Event Management
- Create and manage events (matches and general events)
- Categorize events as Regular or VIP
- Track event details including teams, dates, and descriptions
- Comprehensive event calendar view
- Upcoming events dashboard

### Booking System
- Flexible ticket booking system
- Multiple pricing tiers:
  - VIP: $100
  - Regular Premium: $75
  - Regular Standard: $50
- Special handling for match events (limited to one ticket)
- Real-time ticket availability tracking

### User Management
- Role-based access control (Admin and Manager roles)
- Secure authentication with BCrypt password hashing
- User profile management
- Email-based user verification

### Customer Management
- Customer database with contact information
- Booking history tracking
- Email notifications
- Contact information validation

### Reporting and Analytics
- Sales analysis and reporting
- Event performance metrics
- Excel and PDF export capabilities
- Custom reporting options

## Technical Stack

- **Language**: Java
- **UI Framework**: Swing
- **Database**: SQLite
- **Build Tool**: Maven
- **Security**: BCrypt password hashing
- **Export Formats**: Excel, PDF

## Database Schema

The system uses a relational database with the following key tables:
- Event (event management)
- Customer (customer information)
- Ticket (booking and ticketing)
- Admin (administrator accounts)
- Manager (manager accounts)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── database/    # Database operations
│   │   ├── server/      # Business logic
│   │   ├── services/    # Service layer
│   │   ├── ui/         # User interface
│   │   └── utils/      # Utility classes
│   └── resources/
│       ├── config.properties
│       └── EventEase.db
└── test/
    └── java/           # Test cases
```

## Key Features Implementation

### Event Management
- Supports both match and general event types
- Enforces team validation for matches
- Maintains event status tracking

### Booking System
- Implements ticket category management
- Handles special rules for match events
- Provides real-time availability updates

### Security
- Password hashing using BCrypt
- Input validation and sanitization
- Role-based access control

## Getting Started

1. Clone the repository
2. Ensure Java and Maven are installed
3. Run `mvn install` to install dependencies
4. Configure database connection in `config.properties`
5. Run the application using `mvn exec:java`

## Testing

The project includes comprehensive test coverage:
- Unit tests
- Integration tests
- Controller tests
- Database tests

## Documentation

Detailed documentation is available in the `docs/` directory:
- Database Documentation
- API Documentation
- User Guides
- System Design Diagrams

## Contributing

1. Fork the repository
2. Create a feature branch
3. Submit a pull request

## License

This project is proprietary and all rights are reserved.

## Support

For support and queries, please contact the development team.