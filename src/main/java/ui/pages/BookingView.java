package ui.pages;

import ui.components.RoundedButton;
import ui.components.Sidebar;
import ui.Router;
import ui.Refreshable;
import javax.swing.*;

import server.EventService;
import services.BookingServiceSer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BookingView class represents the ticket booking interface in the application.
 * This view allows users to create bookings for events by selecting events and 
 * providing customer information.
 * Implements Refreshable interface to update content when needed.
 */
public class BookingView extends JPanel implements Refreshable {
    private JPanel mainPanel, contentPanel;
    private JComboBox<String> eventCombo;
    private JComboBox<String> priceCombo;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField contactNumberField;
    private BookingServiceSer bookingServiceSer;

    // Maps to store event ID references with event names
    private java.util.Map<String, Integer> eventIdMap = new java.util.HashMap<>();
    private int selectedEventId = -1;

    // Customer information
    private int customerId = -1;
    private String customerName = "";

    /**
     * Constructor initializes the booking view, loads customer data if available,
     * and sets up the UI components.
     */
    public BookingView() {
        setName("BookingView"); // Set the name for the Router to identify this panel
        setLayout(new BorderLayout());

        // Initialize booking service
        bookingServiceSer = new BookingServiceSer();

        // Check if customer information was passed from CustomerPage
        loadCustomerFromRouter();

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Create main panel
        createMainPanel();

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);

        // If we have customer information from the router, prefill the fields
        populateCustomerFields();
    }

    /**
     * Refreshes the view by reloading events, updating price options,
     * and refreshing customer data. Used when returning to this view.
     */
    @Override
    public void refresh() {
        // Reload events from database
        eventIdMap.clear();
        eventCombo.removeAllItems();
        loadEventsFromDatabase();

        // Update price options based on selected event
        updatePriceOptions();

        // Refresh the customer data if needed
        loadCustomerFromRouter();
        populateCustomerFields();

        // Refresh sidebar
        Component sidebarComponent = null;
        for (Component component : getComponents()) {
            if (component instanceof Sidebar) {
                sidebarComponent = component;
                break;
            }
        }

        if (sidebarComponent != null) {
            // Remove old sidebar
            remove(sidebarComponent);

            // Add new sidebar
            Sidebar sidebar = new Sidebar();
            add(sidebar, BorderLayout.WEST);
        }

        // Force UI update
        revalidate();
        repaint();
    }

    /**
     * Loads customer data from the Router shared data if available.
     * This allows passing customer information between different views.
     */
    private void loadCustomerFromRouter() {
        // Check if customer information was passed from CustomerPage
        Object routerCustomerId = Router.getData("customerId");
        Object routerCustomerName = Router.getData("customerName");

        if (routerCustomerId != null && routerCustomerName != null) {
            customerId = (int) routerCustomerId;
            customerName = (String) routerCustomerName;

            // Clear the router data after retrieving it
            Router.clearData("customerId");
            Router.clearData("customerName");
        }
    }

    /**
     * Populates customer information fields if customer data is available.
     * Splits the customer name into first and last name and retrieves
     * additional details like email and contact number from the database.
     */
    private void populateCustomerFields() {
        if (!customerName.isEmpty()) {
            // Split customer name into first and last name components
            String[] nameParts = customerName.split(" ", 2);
            if (nameParts.length > 0) {
                firstNameField.setText(nameParts[0]);
                if (nameParts.length > 1) {
                    lastNameField.setText(nameParts[1]);
                }
            }

            // Retrieve customer details using the ID
            try {
                EventService.CustomInformationService customInfoService = new EventService.CustomInformationService();
                Map<String, Object> customerInfo = customInfoService.getCustomerDetails(customerId);
                if (customerInfo != null) {
                    // Populate contact and email fields
                    contactNumberField.setText((String) customerInfo.get("contact_number"));
                    emailField.setText((String) customerInfo.get("email"));
                }
            } catch (Exception e) {
                System.out.println("Error retrieving customer details: " + e.getMessage());
            }

            // Disable the fields since we already have customer information
            // to prevent modification of existing customer data
            firstNameField.setEditable(false);
            lastNameField.setEditable(false);
            contactNumberField.setEditable(false);
            emailField.setEditable(false);

            // Change background color to indicate non-editable fields
            firstNameField.setBackground(new Color(240, 240, 240));
            lastNameField.setBackground(new Color(240, 240, 240));
            contactNumberField.setBackground(new Color(240, 240, 240));
            emailField.setBackground(new Color(240, 240, 240));
        }
    }

    /**
     * Custom JPanel with rounded corners used for the booking form.
     * Provides a more modern UI appearance with rounded edges.
     */
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            cornerRadius = radius;
            setOpaque(false); // Required for custom painting
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Use anti-aliasing for smooth rounded corners
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    /**
     * Creates the main panel containing the header and content area.
     */
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

    /**
     * Creates the header section with title at the top of the view.
     */
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(64, 133, 219)); // Blue header background
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Ticket Management");
        headerLabel.setForeground(new Color(240, 240, 255)); // Light text color
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Creates the booking form with customer information fields,
     * event selection, price category selection, and booking button.
     */
    private void createBookingSystem() {
        // Booking form panel with rounded corners
        JPanel formPanel = new RoundedPanel(null, 15);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(800, 600)); // Increased height for additional fields

        // Book a Ticket title
        JLabel bookTicketLabel = new JLabel("Book a Ticket");
        bookTicketLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bookTicketLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookTicketLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookTicketLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(bookTicketLabel);
        formPanel.add(Box.createVerticalStrut(20)); // Spacing

        // ---- Customer Information Section ----
        JLabel customerSectionLabel = new JLabel("Customer Information");
        customerSectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customerSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        customerSectionLabel.setForeground(new Color(64, 133, 219));
        formPanel.add(customerSectionLabel);
        formPanel.add(Box.createVerticalStrut(10));

        // First Name field
        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstNameLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(firstNameLabel);
        formPanel.add(Box.createVerticalStrut(5));

        firstNameField = new JTextField();
        firstNameField.setMaximumSize(new Dimension(800, 35));
        firstNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        firstNameField.setForeground(new Color(50, 50, 50));
        formPanel.add(firstNameField);
        formPanel.add(Box.createVerticalStrut(10));

        // Last Name field
        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastNameLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(lastNameLabel);
        formPanel.add(Box.createVerticalStrut(5));

        lastNameField = new JTextField();
        lastNameField.setMaximumSize(new Dimension(800, 35));
        lastNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        lastNameField.setForeground(new Color(50, 50, 50));
        formPanel.add(lastNameField);
        formPanel.add(Box.createVerticalStrut(10));

        // Contact Number field
        JLabel contactNumberLabel = new JLabel("Contact Number:");
        contactNumberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactNumberLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(contactNumberLabel);
        formPanel.add(Box.createVerticalStrut(5));

        contactNumberField = new JTextField();
        contactNumberField.setMaximumSize(new Dimension(800, 35));
        contactNumberField.setAlignmentX(Component.LEFT_ALIGNMENT);
        contactNumberField.setForeground(new Color(50, 50, 50));
        formPanel.add(contactNumberField);
        formPanel.add(Box.createVerticalStrut(10));

        // Email field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(800, 35));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setForeground(new Color(50, 50, 50));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(20));

        // ---- Event Information Section ----
        JLabel eventSectionLabel = new JLabel("Event Information");
        eventSectionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        eventSectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventSectionLabel.setForeground(new Color(64, 133, 219));
        formPanel.add(eventSectionLabel);
        formPanel.add(Box.createVerticalStrut(10));

        // Event selection dropdown
        JLabel eventLabel = new JLabel("Select Event:");
        eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(eventLabel);
        formPanel.add(Box.createVerticalStrut(5));

        // Initialize event combo with default selection
        eventCombo = new JComboBox<>();
        eventCombo.addItem("Select Event");

        // Load events from database
        loadEventsFromDatabase();

        eventCombo.setMaximumSize(new Dimension(800, 35));
        eventCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eventCombo);
        formPanel.add(Box.createVerticalStrut(10));

        // Add event selection listener to handle event selection changes
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

            // Update price options based on selected event
            updatePriceOptions();
        });

        // Price category selection dropdown
        JLabel priceLabel = new JLabel("Select Price Category:");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(priceLabel);
        formPanel.add(Box.createVerticalStrut(5));

        priceCombo = new JComboBox<>(bookingServiceSer.getPricingOptions());
        priceCombo.setMaximumSize(new Dimension(800, 35));
        priceCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(priceCombo);
        formPanel.add(Box.createVerticalStrut(20));

        // Button panel for centering the Book Now button
        JPanel buttonPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER), 15);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(800, 50));

        // Create a custom rounded button with green background
        RoundedButton bookNowButton = new RoundedButton("Book Now", 25);
        bookNowButton.setBackground(new Color(28, 184, 96));
        bookNowButton.setFont(new Font("Arial", Font.BOLD, 14));
        bookNowButton.setForeground(Color.white);
        bookNowButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(bookNowButton);
        bookNowButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        formPanel.add(buttonPanel);

        // Add components to content panel
        contentPanel.add(formPanel);

        // Add Book Now button action listener for form submission
        bookNowButton.addActionListener(e -> {
            // Validate customer information fields
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String contactNumber = contactNumberField.getText().trim();
            String email = emailField.getText().trim();

            // Input validation - show error messages for missing required fields
            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a first name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a last name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (contactNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a contact number", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an email", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (eventCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please select an event", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (priceCombo.getSelectedIndex() == 0 || priceCombo.getSelectedItem().equals("Select Price Category")) {
                JOptionPane.showMessageDialog(this, "Please select a price category", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Get selected values for booking creation
            String customerFullName = firstName + " " + lastName;
            String selectedEvent = (String) eventCombo.getSelectedItem();
            String selectedPriceCategory = (String) priceCombo.getSelectedItem();

            // Determine ticket type based on price category
            String ticketType = bookingServiceSer.getTicketTypeFromPriceCategory(selectedPriceCategory);

            try {
                // Create an instance of CustomInformationService
                EventService.CustomInformationService customInfoService = new EventService.CustomInformationService();
                int customerIdToUse;

                // If we don't have a customer ID yet, add the customer to the database
                if (customerId <= 0) {
                    customerIdToUse = customInfoService.addCustomer(firstName, lastName, contactNumber, email);
                } else {
                    customerIdToUse = customerId;
                }

                // Call the booking service to create the booking
                boolean success = bookingServiceSer.createBooking(
                        customerFullName,
                        selectedEvent,
                        selectedPriceCategory,
                        customerIdToUse,
                        selectedEventId,
                        ticketType);

                if (success) {
                    // Show success message
                    JOptionPane.showMessageDialog(this,
                            "Booking created successfully for " + customerFullName + "!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Reset form fields
                    firstNameField.setText("");
                    lastNameField.setText("");
                    contactNumberField.setText("");
                    emailField.setText("");
                    eventCombo.setSelectedIndex(0);
                    priceCombo.setSelectedIndex(0);

                    // Navigate back to event view
                    Router.showPage("EventView");
                } else {
                    // Show error message from service
                    JOptionPane.showMessageDialog(this, "Failed to create booking: " +
                            bookingServiceSer.getLastErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                // Handle any exceptions during booking creation
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Loads available events from the database into the event combo box.
     * Creates a formatted display string for each event with key information.
     * Falls back to dummy data if database load fails.
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

                // Create a display string for the event with consistent format
                String displayText = String.format("%s - %s vs %s (%s, %s) - %s",
                        eventName, teamA, teamB, category, eventType, eventDate);

                // Add to combo box and map for later reference
                eventCombo.addItem(displayText);
                eventIdMap.put(displayText, eventId);
            }
        } catch (Exception e) {
            System.out.println("Error loading events: " + e.getMessage());

            // Add some dummy data if database load fails
            // This provides a fallback UI for testing when database connection fails
            eventCombo.addItem("Football Match - Team A vs Team B");
            eventCombo.addItem("Concert - Artist X");
            eventCombo.addItem("Basketball Game - Team C vs Team D");
        }
    }

    /**
     * Updates the price category dropdown options based on the selected event.
     * Different event types may have different pricing structures.
     */
    
    private void updatePriceOptions() {
        if (priceCombo != null) {
            priceCombo.removeAllItems();

            // Default options for all events
            if (selectedEventId != -1) {
                // Use the same format as in BookingServiceSer.getPricingOptions()
                priceCombo.addItem("Select Price Category");
                priceCombo.addItem("VIP - $25");
                priceCombo.addItem("Regular - Premium - $15");
                priceCombo.addItem("Regular - Standard - $10");
            } else {
                // Prompt to select an event first
                priceCombo.addItem("Select Event First");
            }
        }
    }
}