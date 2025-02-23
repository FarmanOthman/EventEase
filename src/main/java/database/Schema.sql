-- Create Event table (represents an event, e.g., a football match or championship)
CREATE TABLE IF NOT EXISTS Event (
    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_name TEXT NOT NULL,
    event_start_date TIMESTAMP NOT NULL,
    event_end_date TIMESTAMP NOT NULL,
    event_description TEXT,
    event_location TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Customer table
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    contact_number TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Ticket_category table (represents different ticket types for games)
CREATE TABLE IF NOT EXISTS Ticket_category (
    ticket_category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL,
    category_price REAL NOT NULL CHECK (category_price > 0),  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Team table (needed for foreign key references in Ticket table)
CREATE TABLE IF NOT EXISTS Team (
    team_id INTEGER PRIMARY KEY AUTOINCREMENT,
    team_name TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Ticket table (represents individual games within the event)
CREATE TABLE IF NOT EXISTS Ticket (
    ticket_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id INTEGER NOT NULL,  
    team_a INTEGER NOT NULL,
    team_b INTEGER NOT NULL,
    ticket_date TIMESTAMP NOT NULL,
    ticket_status TEXT DEFAULT 'Scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE,  
    FOREIGN KEY (team_a) REFERENCES Team(team_id) ON DELETE CASCADE,
    FOREIGN KEY (team_b) REFERENCES Team(team_id) ON DELETE CASCADE,
    CONSTRAINT ticket_unique UNIQUE (event_id, team_a, team_b, ticket_date) 
);

-- Create Booking_event table (Booking the event itself)
CREATE TABLE IF NOT EXISTS Booking_event (
    booking_event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,  -- Fixed: Added event_id column
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TEXT DEFAULT 'Pending',
    payment_status TEXT DEFAULT 'Unpaid',
    receipt_number TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE 
);

-- Create Booking_ticket table (Booking tickets for individual games within the event)
CREATE TABLE IF NOT EXISTS Booking_ticket (
    booking_ticket_id INTEGER PRIMARY KEY AUTOINCREMENT,
    ticket_id INTEGER NOT NULL,  
    ticket_category INTEGER NOT NULL,
    price REAL NOT NULL CHECK (price > 0),  
    quantity INTEGER NOT NULL CHECK (quantity > 0),  
    total_price REAL NOT NULL CHECK (total_price > 0),  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES Ticket(ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (ticket_category) REFERENCES Ticket_category(ticket_category_id) ON DELETE CASCADE
);

-- Create Role table to manage roles dynamically
CREATE TABLE IF NOT EXISTS Role (
    role_id INTEGER PRIMARY KEY AUTOINCREMENT,
    role_name TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Admin table with hashed password for security
CREATE TABLE IF NOT EXISTS Admin (
    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,  
    role_id INTEGER NOT NULL,
    email TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES Role(role_id),
    CONSTRAINT username_unique UNIQUE(username) 
);

-- Create Manager table to store manager information
CREATE TABLE IF NOT EXISTS Manager (
    manager_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,  
    role_id INTEGER NOT NULL,
    email TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES Role(role_id),
    CONSTRAINT username_unique UNIQUE(username) 
);

-- Create Report table
CREATE TABLE IF NOT EXISTS Report (
    report_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id INTEGER NOT NULL,  
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_revenue REAL NOT NULL CHECK (total_revenue >= 0),  
    total_tickets_sold INTEGER NOT NULL CHECK (total_tickets_sold >= 0), 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE
);

-- Create Notification table
CREATE TABLE IF NOT EXISTS Notification (
    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id INTEGER NOT NULL,
    admin_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    notification_type TEXT NOT NULL,
    is_read BOOLEAN DEFAULT 0, 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES Booking_ticket(booking_ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES Admin(admin_id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_event_name ON Event(event_name);
CREATE INDEX IF NOT EXISTS idx_event_start_date ON Event(event_start_date);
CREATE INDEX IF NOT EXISTS idx_ticket_event_id ON Ticket(event_id);
CREATE INDEX IF NOT EXISTS idx_booking_event_id ON Booking_event(event_id);
CREATE INDEX IF NOT EXISTS idx_booking_ticket_ticket_id ON Booking_ticket(ticket_id);
CREATE INDEX IF NOT EXISTS idx_notification_is_read ON Notification(is_read);
