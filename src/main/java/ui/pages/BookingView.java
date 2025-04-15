package ui.pages;

import ui.components.Sidebar;
import ui.Router;

import javax.swing.*;

import services.BookingServiceSer;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TODO: Booking System Architecture
 * 1. Create the following structure:
 * services/
 * │ ├── booking/
 * │ │ ├── BookingService.java # Core booking functionality
 * │ │ ├── TicketManager.java # Ticket management
 * │ │ ├── PricingService.java # Dynamic pricing
 * │ │ └── InventoryManager.java # Seat/ticket inventory
 * │ └── payment/
 * │ ├── PaymentProcessor.java # Payment processing
 * │ └── RefundManager.java # Refund handling
 * └── payment/
 * ├── PaymentProcessor.java # Payment processing
 * └── RefundManager.java # Refund handling
 *
 * 2. Database Integration:
 * - Bookings table
 * - Tickets table
 * - Pricing table
 * - Payment transactions table
 *
 * 3. External Integration:
 * - Payment gateway
 * - Email notifications
 * - SMS confirmations
 */
public class BookingView extends JPanel {
    private JPanel mainPanel, contentPanel;
    private JComboBox<String> eventCombo;
    private JComboBox<String> priceCombo;
    private JTextField nameField;
    private BookingServiceSer bookingServiceSer;

    // Maps to store event ID references with event names
    private java.util.Map<String, Integer> eventIdMap = new java.util.HashMap<>();
    private int selectedEventId = -1;

    // Temporary hardcoded customer ID (this would come from login in real app)
    private final int CUSTOMER_ID = 1;

    public BookingView() {
        setLayout(new BorderLayout());

        // Initialize service
        bookingServiceSer = new BookingServiceSer();

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Create main panel
        createMainPanel();

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    // Custom rounded panel class (for the form panel only)
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        // Create header panel
        createHeader();

        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Create the booking system
        createBookingSystem();

        // Add panels to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(64, 143, 224));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Ticket Management");
        headerLabel.setForeground(new Color(240, 240, 255));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createBookingSystem() {
        // Booking form panel with rounded corners
        JPanel formPanel = new RoundedPanel(null, 15);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(800, 400));

        // Book a Ticket title
        JLabel bookTicketLabel = new JLabel("Book a Ticket");
        bookTicketLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bookTicketLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookTicketLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookTicketLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(bookTicketLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Name label and text field
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));

        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(800, 35));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setForeground(new Color(50, 50, 50));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));

        // Event label and combo
        JLabel eventLabel = new JLabel("Select Event:");
        eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(eventLabel);
        formPanel.add(Box.createVerticalStrut(5));

        // Initialize with default selection
        eventCombo = new JComboBox<>();
        eventCombo.addItem("Select Event");

        // Load events from database
        loadEventsFromDatabase();

        eventCombo.setMaximumSize(new Dimension(800, 35));
        eventCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eventCombo);
        formPanel.add(Box.createVerticalStrut(15));

        // Add event selection listener
        eventCombo.addActionListener(e -> {
            if (eventCombo.getSelectedIndex() > 0) {
                String selectedEvent = (String) eventCombo.getSelectedItem();
                selectedEventId = eventIdMap.get(selectedEvent);

                // Check if it's possible to create a ticket for this event
                // (Match events can only have one ticket)
                boolean canCreateTicket = bookingServiceSer.canCreateTicketForEvent(selectedEventId);
                if (!canCreateTicket) {
                    JOptionPane.showMessageDialog(this,
                            bookingServiceSer.getLastErrorMessage(),
                            "Ticket Creation Restricted",
                            JOptionPane.WARNING_MESSAGE);
                    // Reset selection
                    eventCombo.setSelectedIndex(0);
                    selectedEventId = -1;
                }
            } else {
                selectedEventId = -1;
            }
        });

        // Price label and combo
        JLabel priceLabel = new JLabel("Select Price Category:");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(priceLabel);
        formPanel.add(Box.createVerticalStrut(5));

        priceCombo = new JComboBox<>(bookingServiceSer.getPricingOptions());
        priceCombo.setMaximumSize(new Dimension(800, 35));
        priceCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(priceCombo);
        formPanel.add(Box.createVerticalStrut(15));

        // Button panel for centering the Book Now button
        JPanel buttonPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER), 15);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(800, 50));

        // Create a custom rounded button that matches the image
        JButton bookNowButton = new JButton("Book Now") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 204, 113));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        bookNowButton.setBackground(new Color(66, 133, 244));
        bookNowButton.setForeground(Color.WHITE);
        bookNowButton.setFocusPainted(false);
        bookNowButton.setBorderPainted(false);
        bookNowButton.setContentAreaFilled(false);
        bookNowButton.setPreferredSize(new Dimension(120, 35));
        bookNowButton.setFont(new Font("Arial", Font.PLAIN, 14));

        buttonPanel.add(bookNowButton);
        formPanel.add(buttonPanel);

        // Add components to content panel
        contentPanel.add(formPanel);

        // Add Book Now button action
        bookNowButton.addActionListener(e -> {
            // Validate form inputs
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a customer name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (eventCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select an event", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (priceCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select a price category", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected values
            String customerName = nameField.getText().trim();
            String selectedEvent = (String) eventCombo.getSelectedItem();
            String selectedPriceCategory = (String) priceCombo.getSelectedItem();

            // Determine ticket type based on price category
            String ticketType = bookingServiceSer.getTicketTypeFromPriceCategory(selectedPriceCategory);

            // Get event details to verify constraints
            Map<String, Object> eventDetails = bookingServiceSer.getEventDetails(selectedEventId);
            if (eventDetails != null) {
                // Check if ticket type matches the event category
                String eventCategory = (String) eventDetails.get("category");

                // Ensure ticket type is compatible with event category (enforce schema
                // constraint)
                // Schema constraint: ticket_type IN ('Regular', 'VIP')
                if ((eventCategory.equals("VIP") && !ticketType.equals("VIP")) ||
                        (eventCategory.equals("Regular") && !ticketType.equals("Regular"))) {
                    JOptionPane.showMessageDialog(this,
                            "Ticket type must match event category: " + eventCategory + "\n" +
                                    "VIP events require VIP tickets.\n" +
                                    "Regular events require Regular tickets.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);

                    // Suggest the correct price category
                    if (eventCategory.equals("VIP")) {
                        // Find the VIP price option index
                        for (int i = 0; i < priceCombo.getItemCount(); i++) {
                            if (priceCombo.getItemAt(i).toString().startsWith("VIP")) {
                                priceCombo.setSelectedIndex(i);
                                break;
                            }
                        }
                    } else if (eventCategory.equals("Regular")) {
                        // Find the first Regular price option index
                        for (int i = 0; i < priceCombo.getItemCount(); i++) {
                            if (priceCombo.getItemAt(i).toString().startsWith("Regular")) {
                                priceCombo.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                    return;
                }
            }

            try {
                // Call the booking service to create the booking
                boolean success = bookingServiceSer.createBooking(customerName, selectedEvent, selectedPriceCategory,
                        CUSTOMER_ID, selectedEventId, ticketType);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Booking created successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Reset form
                    nameField.setText("");
                    eventCombo.setSelectedIndex(0);
                    priceCombo.setSelectedIndex(0);

                    // Navigate to customer page
                    Router.showPage("CustomerPage");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create booking: " +
                            bookingServiceSer.getLastErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Load events from the database into the event combo box
     */
    private void loadEventsFromDatabase() {
        try {
            // Clear any previous entries except the first one
            eventIdMap.clear();
            eventCombo.removeAllItems();
            eventCombo.addItem("Select Event");

            // Fetch all events from the database using the service
            List<Map<String, Object>> events = bookingServiceSer.getAllEvents();

            SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            for (Map<String, Object> event : events) {
                int eventId = (Integer) event.get("event_id");
                String eventName = (String) event.get("event_name");
                String teamA = (String) event.get("team_a");
                String teamB = (String) event.get("team_b");
                String category = (String) event.get("category");
                String eventType = (String) event.get("event_type");

                // Format the date for display
                String eventDate = "Unknown date";
                if (event.get("event_date") != null) {
                    try {
                        eventDate = event.get("event_date").toString();
                        // If the date is a timestamp object, try to format it
                        if (event.get("event_date") instanceof Date) {
                            eventDate = displayDateFormat.format((Date) event.get("event_date"));
                        }
                    } catch (Exception e) {
                        System.out.println("Error formatting date: " + e.getMessage());
                    }
                }

                // Create a display string for the event
                String displayText = String.format("%s - %s vs %s (%s, %s) - %s",
                        eventName, teamA, teamB, category, eventType, eventDate);

                // Add to combo box and map
                eventCombo.addItem(displayText);
                eventIdMap.put(displayText, eventId);
            }
        } catch (Exception e) {
            System.out.println("Error loading events: " + e.getMessage());

            // Add some dummy data if database load fails
            eventCombo.addItem("Football Match - Team A vs Team B");
            eventCombo.addItem("Concert - Artist X");
            eventCombo.addItem("Basketball Game - Team C vs Team D");
        }
    }

    // TODO: Additional Features
    // 1. Booking Process:
    // - Multi-step booking wizard
    // - Seat selection
    // - Payment processing
    // - Confirmation emails
    //
    // 2. Inventory Management:
    // - Real-time availability
    // - Hold management
    // - Waitlist functionality
    // - Capacity tracking
    //
    // 3. Payment Features:
    // - Multiple payment methods
    // - Partial payments
    // - Refund processing
    // - Invoice generation
    //
    // 4. Customer Management:
    // - Customer profiles
    // - Booking history
    // - Preferences
    // - VIP handling
    //
    // 5. Reporting:
    // - Sales reports
    // - Booking analytics
    // - Revenue tracking
    // - Performance metrics
}