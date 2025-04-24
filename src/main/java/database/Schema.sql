-- Event Table (Stores event details with fixed categories and event types)
CREATE TABLE IF NOT EXISTS Event (
    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_name TEXT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    event_description TEXT,
    category TEXT CHECK (category IN ('Regular', 'VIP')) NOT NULL, -- Fixed event category (Regular, VIP)
    event_type TEXT CHECK (event_type IN ('Event', 'Match')) NOT NULL, -- Event type can be Event or Match
    team_a TEXT NOT NULL,
    team_b TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (team_a <> team_b) -- Ensure teams are different
);

-- Customer Table (Stores customer details)
CREATE TABLE IF NOT EXISTS Customer (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    contact_number TEXT NOT NULL CHECK (length(contact_number) >= 10 AND length(contact_number) <= 15),  -- Ensuring a valid contact number length (adjust based on region)
    email TEXT NOT NULL UNIQUE CHECK (email LIKE '%@%.%'), -- Basic email format validation (use more complex validation at the application layer)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Ticket Table (Stores individual tickets linked to an event with fixed ticket types)
CREATE TABLE IF NOT EXISTS Ticket (
    ticket_id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_id INTEGER NOT NULL,  
    customer_id INTEGER UNIQUE,  -- Making this UNIQUE ensures one-to-one relationship
    ticket_type TEXT CHECK (ticket_type IN ('Regular', 'VIP')) NOT NULL,
    ticket_date TIMESTAMP NOT NULL,
    ticket_status TEXT CHECK (ticket_status IN ('Available', 'Sold', 'Canceled')) DEFAULT 'Available',  -- Validating ticket status
    price REAL NOT NULL CHECK (price > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES Event(event_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE SET NULL,  -- If customer is deleted, set ticket's customer_id to NULL
    CONSTRAINT ticket_unique UNIQUE (event_id, ticket_type)  -- Only one ticket for Regular events
);

-- Admin Table (Stores admin login details with hashed password)
CREATE TABLE IF NOT EXISTS Admin (
    admin_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,  -- To store hashed password
    email TEXT UNIQUE CHECK (email LIKE '%@%.%'),  -- Basic email format validation (use more complex validation at the application layer)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Manager Table (Stores manager login details with hashed password)
CREATE TABLE IF NOT EXISTS Manager (
    manager_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,  -- To store hashed password
    email TEXT UNIQUE CHECK (email LIKE '%@%.%'),  -- Basic email format validation (use more complex validation at the application layer)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger to enforce the constraint that only one ticket can be created for an event if the event type is 'Match'
CREATE TRIGGER IF NOT EXISTS enforce_single_ticket_for_match
BEFORE INSERT ON Ticket
FOR EACH ROW
WHEN EXISTS (SELECT 1 FROM Event WHERE event_id = NEW.event_id AND event_type = 'Match')
BEGIN
    SELECT RAISE(ABORT, 'Only one ticket can be created for an event of type Match')
    WHERE (SELECT COUNT(*) FROM Ticket WHERE event_id = NEW.event_id) >= 1;
END;

-- Indexes for Performance Optimization
CREATE INDEX IF NOT EXISTS idx_event_name ON Event(event_name);
CREATE INDEX IF NOT EXISTS idx_event_date ON Event(event_date);
CREATE INDEX IF NOT EXISTS idx_ticket_event_id ON Ticket(event_id);
CREATE INDEX IF NOT EXISTS idx_event_name_date ON Event(event_name, event_date);  -- Composite index for faster event name and date search

-- Sales Table (Stores aggregated sales data)
CREATE TABLE IF NOT EXISTS Sales (
    sale_id INTEGER PRIMARY KEY AUTOINCREMENT,
    sale_date TIMESTAMP NOT NULL,
    tickets_sold INTEGER NOT NULL CHECK (tickets_sold >= 0),
    revenue REAL NOT NULL CHECK (revenue >= 0),
    category TEXT CHECK (category IN ('Regular', 'VIP', 'Premium')) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster date-based queries
CREATE INDEX IF NOT EXISTS idx_sales_date ON Sales(sale_date);