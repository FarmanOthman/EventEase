-- Create CUSTOMER table
CREATE TABLE IF NOT EXISTS CUSTOMER (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    contact_number TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT email_unique UNIQUE(email)  -- Ensures email is unique
);

-- Create TEAM table
CREATE TABLE IF NOT EXISTS TEAM (
    team_id INTEGER PRIMARY KEY AUTOINCREMENT,
    team_name TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT team_name_unique UNIQUE(team_name) -- Ensures unique team names
);

-- Create VENUE table
CREATE TABLE IF NOT EXISTS VENUE (
    venue_id INTEGER PRIMARY KEY AUTOINCREMENT,
    venue_name TEXT NOT NULL,
    location TEXT NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0),  -- Ensure valid capacity
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create EVENT table
CREATE TABLE IF NOT EXISTS EVENT (
    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    team_a INTEGER NOT NULL,
    team_b INTEGER NOT NULL,
    event_date TIMESTAMP NOT NULL,
    venue_id INTEGER NOT NULL,
    event_status TEXT DEFAULT 'Scheduled',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (team_a) REFERENCES TEAM(team_id) ON DELETE CASCADE,
    FOREIGN KEY (team_b) REFERENCES TEAM(team_id) ON DELETE CASCADE,
    FOREIGN KEY (venue_id) REFERENCES VENUE(venue_id) ON DELETE CASCADE,
    CONSTRAINT event_unique UNIQUE (team_a, team_b, event_date) -- Prevent duplicate events
);

-- Create TICKET_CATEGORY table
CREATE TABLE IF NOT EXISTS TICKET_CATEGORY (
    ticket_category_id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_name TEXT NOT NULL,
    category_price REAL NOT NULL CHECK (category_price > 0),  -- Ensure valid price
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create BOOKING table
CREATE TABLE IF NOT EXISTS BOOKING (
    booking_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    ticket_category INTEGER NOT NULL,
    price REAL NOT NULL CHECK (price > 0),  -- Ensure valid price
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TEXT DEFAULT 'Pending',
    payment_status TEXT DEFAULT 'Unpaid',
    receipt_number TEXT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES EVENT(event_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (ticket_category) REFERENCES TICKET_CATEGORY(ticket_category_id) ON DELETE CASCADE
);

-- Create ADMIN table
CREATE TABLE IF NOT EXISTS ADMIN (
    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT DEFAULT 'Manager',
    email TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create REPORT table
CREATE TABLE IF NOT EXISTS REPORT (
    report_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id INTEGER NOT NULL,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_revenue REAL NOT NULL CHECK (total_revenue >= 0),  -- Ensure valid revenue
    total_tickets_sold INTEGER NOT NULL CHECK (total_tickets_sold >= 0), -- Ensure valid ticket count
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES EVENT(event_id) ON DELETE CASCADE
);

-- Create NOTIFICATION table
CREATE TABLE IF NOT EXISTS NOTIFICATION (
    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
    booking_id INTEGER NOT NULL,
    admin_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    notification_type TEXT NOT NULL,
    is_read BOOLEAN DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES ADMIN(admin_id) ON DELETE CASCADE
);
