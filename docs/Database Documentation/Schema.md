---

# **EventEase Database Schema Documentation**

## **Overview**

This database schema supports the EventEase platform, which allows managing events, customers, tickets, and user roles (admins and managers). The schema includes tables for storing events, tickets, customer details, and login information for admin and manager roles.

---

## **Tables**

### 1. **Event Table**
Stores details about each event, including name, date, description, and teams involved. Event types are categorized as either "Event" or "Match".

#### Columns:
- **event_id**: Unique ID for the event (Primary Key).
- **event_name**: Name of the event.
- **event_date**: Date and time of the event.
- **event_description**: Description of the event.
- **category**: Category of the event (either 'Regular' or 'VIP').
- **event_type**: Type of event (either 'Event' or 'Match').
- **team_a**: Name of the first team (must be different from team_b).
- **team_b**: Name of the second team (must be different from team_a).
- **created_at**: Timestamp of when the event was created.
- **updated_at**: Timestamp of when the event was last updated.

#### Rules:
- Teams (`team_a` and `team_b`) cannot be the same.
- Event category is fixed to either 'Regular' or 'VIP'.
- Event type is fixed to either 'Event' or 'Match'.

---

### 2. **Customer Table**
Stores details about customers who purchase tickets for events.

#### Columns:
- **customer_id**: Unique ID for the customer (Primary Key).
- **first_name**: Customer's first name.
- **last_name**: Customer's last name.
- **contact_number**: Customer's contact number (valid length: 10 to 15 digits).
- **email**: Customer's email address (must follow basic email format).
- **created_at**: Timestamp of when the customer was created.
- **updated_at**: Timestamp of when the customer was last updated.

---

### 3. **Ticket Table**
Stores information about tickets available for each event. Each event has a fixed category and ticket types (Regular or VIP).

#### Columns:
- **ticket_id**: Unique ID for the ticket (Primary Key).
- **event_id**: The event ID the ticket belongs to (Foreign Key).
- **ticket_type**: Type of ticket (either 'Regular' or 'VIP').
- **ticket_date**: Date and time of the ticket sale.
- **ticket_status**: Status of the ticket (Available, Sold, Canceled).
- **price**: Price of the ticket.
- **created_at**: Timestamp of when the ticket was created.
- **updated_at**: Timestamp of when the ticket was last updated.

#### Rules:
- Only one ticket can be sold for "Match" type events.
- The ticket status can be 'Available', 'Sold', or 'Canceled'.
- A ticket must be linked to a valid event via `event_id`.

---

### 4. **Admin Table**
Stores login credentials for system administrators.

#### Columns:
- **admin_id**: Unique ID for the admin (Primary Key).
- **username**: Admin's username (unique).
- **password**: Hashed password for the admin.
- **email**: Admin's email (must follow basic email format).
- **created_at**: Timestamp of when the admin was created.
- **updated_at**: Timestamp of when the admin was last updated.

---

### 5. **Manager Table**
Stores login credentials for system managers.

#### Columns:
- **manager_id**: Unique ID for the manager (Primary Key).
- **username**: Manager's username (unique).
- **password**: Hashed password for the manager.
- **email**: Manager's email (must follow basic email format).
- **created_at**: Timestamp of when the manager was created.
- **updated_at**: Timestamp of when the manager was last updated.

---

## **Triggers**

### `enforce_single_ticket_for_match`
This trigger ensures that only one ticket can be created for an event if the event type is "Match". If an attempt is made to insert more than one ticket for such an event, the operation will be aborted.

---

## **Indexes**

To optimize queries, the following indexes are created:

- **idx_event_name**: Index on `event_name` in the `Event` table.
- **idx_event_date**: Index on `event_date` in the `Event` table.
- **idx_ticket_event_id**: Index on `event_id` in the `Ticket` table for faster lookups.
- **idx_event_name_date**: Composite index on `event_name` and `event_date` in the `Event` table for efficient searches.

---

## **Key Rules**

1. **Event Types**: Events can be of type "Event" or "Match". For "Match" events, only one ticket is allowed.
2. **Ticket Categories**: Tickets are categorized into either "Regular" or "VIP".
3. **Team Validation**: Teams involved in a match (team_a and team_b) must be different.
4. **Password Hashing**: Admin and Manager passwords are stored in a hashed format for security.
5. **Email and Contact Validation**: Basic validations are enforced for email format and contact number length.

---

## **Conclusion**

This simplified database schema is designed to efficiently manage events, tickets, customer data, and user roles (admins and managers) while enforcing data integrity and security. The schema includes necessary constraints, triggers, and indexes to ensure consistent data and optimize query performance.

---
