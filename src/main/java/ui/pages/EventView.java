package ui.pages;

import ui.components.Sidebar;
import ui.Router;
import services.event.EventService;
import javax.swing.*;
import java.awt.*;

/**
 * TODO: Event Management System Architecture
 * 1. Create the following structure:
 * services/
 * ├── event/
 * │ ├── EventService.java # Core event functionality
 * │ ├── VenueManager.java # Venue management
 * │ ├── TicketingService.java # Ticket management
 * │ └── SchedulingService.java # Event scheduling
 * └── management/
 * ├── ResourceManager.java # Resource allocation
 * └── StaffingService.java # Staff management
 *
 * 2. Event Features:
 * - Event creation/editing
 * - Venue allocation
 * - Capacity management
 * - Resource planning
 *
 * 3. Integration Points:
 * - Booking system
 * - Calendar system
 * - Staff scheduling
 * - Resource management
 *
 * 4. Event Creation Features:
 * - Dynamic form validation
 * - Image upload for event
 * - Rich text description
 * - Custom field support
 *
 * 5. Venue Management:
 * - Venue availability check
 * - Seating layout designer
 * - Capacity calculator
 * - Facility requirements
 *
 * 6. Pricing Management:
 * - Multiple ticket tiers
 * - Dynamic pricing
 * - Discount codes
 * - Package deals
 *
 * 7. Event Operations:
 * - Staff assignment
 * - Equipment tracking
 * - Setup checklist
 * - Post-event reporting
 *
 * 8. Marketing Features:
 * - Social media integration
 * - Email campaign tools
 * - Promotional materials
 * - Analytics tracking
 *
 * 9. Attendee Management:
 * - Registration tracking
 * - Check-in system
 * - Attendee communications
 * - Feedback collection
 *
 * 10. Reporting Tools:
 * - Sales analytics
 * - Attendance tracking
 * - Revenue forecasting
 * - Performance metrics
 */
public class EventView extends JPanel {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private EventService eventService;
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;

    public EventView() {
        setLayout(new BorderLayout());

        // Initialize EventService
        eventService = new EventService();

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Create main panel with event form
        createMainPanel();

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create header panel
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(64, 143, 224));
        headerPanel.setPreferredSize(new Dimension(600, 50));
        JLabel headerLabel = new JLabel("Event Management");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        // Create content panel for the form
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Add "Add Event" title
        JLabel titleLabel = new JLabel("Add Event");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Create form components
        createFormComponents();

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private void createFormComponents() {
        // Event Name
        JPanel eventNamePanel = new JPanel(new BorderLayout());
        eventNamePanel.setBackground(Color.WHITE);
        eventNamePanel.setMaximumSize(new Dimension(600, 60));
        JLabel eventNameLabel = new JLabel("Event Name:");
        JTextField eventNameField = new JTextField();
        eventNamePanel.add(eventNameLabel, BorderLayout.NORTH);
        eventNamePanel.add(eventNameField, BorderLayout.CENTER);

        // Team panels
        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        teamsPanel.setBackground(Color.WHITE);
        teamsPanel.setMaximumSize(new Dimension(600, 60));

        JPanel team1Panel = new JPanel(new BorderLayout());
        team1Panel.setBackground(Color.WHITE);
        JLabel team1Label = new JLabel("Team1:");
        JTextField team1Field = new JTextField();
        team1Panel.add(team1Label, BorderLayout.NORTH);
        team1Panel.add(team1Field, BorderLayout.CENTER);

        JPanel team2Panel = new JPanel(new BorderLayout());
        team2Panel.setBackground(Color.WHITE);
        JLabel team2Label = new JLabel("Team2:");
        JTextField team2Field = new JTextField();
        team2Panel.add(team2Label, BorderLayout.NORTH);
        team2Panel.add(team2Field, BorderLayout.CENTER);

        teamsPanel.add(team1Panel);
        teamsPanel.add(team2Panel);

        // Date panel
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(Color.WHITE);
        datePanel.setMaximumSize(new Dimension(600, 60));
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField();
        datePanel.add(dateLabel, BorderLayout.NORTH);
        datePanel.add(dateField, BorderLayout.CENTER);

        // Type and Category panels
        JPanel typeCategoryPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        typeCategoryPanel.setBackground(Color.WHITE);
        typeCategoryPanel.setMaximumSize(new Dimension(600, 60));

        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBackground(Color.WHITE);
        JLabel typeLabel = new JLabel("Type:");
        typeCombo = new JComboBox<>(new String[] { "choose" });
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(typeCombo, BorderLayout.CENTER);

        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(Color.WHITE);
        JLabel categoryLabel = new JLabel("Category:");
        categoryCombo = new JComboBox<>(new String[] { "choose" });
        categoryPanel.add(categoryLabel, BorderLayout.NORTH);
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);

        typeCategoryPanel.add(typePanel);
        typeCategoryPanel.add(categoryPanel);

        // Event Details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setMaximumSize(new Dimension(600, 100));
        JLabel detailsLabel = new JLabel("Event Details:");
        JTextArea detailsArea = new JTextArea(4, 20);
        detailsArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        detailsPanel.add(detailsLabel, BorderLayout.NORTH);
        detailsPanel.add(detailsArea, BorderLayout.CENTER);

        // Custom rounded button panel with left alignment
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setMaximumSize(new Dimension(600, 40));

        // Create the rounded green Add button
        JButton addButton = new JButton("Add") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Paint the rounded background
                g2.setColor(new Color(28, 184, 96));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // Paint the text
                FontMetrics fm = g2.getFontMetrics();
                Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
                String text = "Add";

                int x = (textRect.width - fm.stringWidth(text)) / 2;
                int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();

                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(75, 35);
            }
        };

        // Remove default button styling
        addButton.setContentAreaFilled(false);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add ActionListener to the button to handle click event
        addButton.addActionListener(e -> {
            // TODO: Validate form fields
            String eventName = eventNameField.getText();
            String team1 = team1Field.getText();
            String team2 = team2Field.getText();
            String date = dateField.getText();
            String type = (String) typeCombo.getSelectedItem();
            String category = (String) categoryCombo.getSelectedItem();
            String details = detailsArea.getText();

            try {
                // Save event to database
                boolean success = eventService.createEvent(eventName, team1, team2, date, type, category, details);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event created successfully!", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Clear form fields after successful creation
                    eventNameField.setText("");
                    team1Field.setText("");
                    team2Field.setText("");
                    dateField.setText("");
                    typeCombo.setSelectedIndex(0);
                    categoryCombo.setSelectedIndex(0);
                    detailsArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create event", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(addButton);

        // Add components to content panel
        contentPanel.add(eventNamePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(teamsPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(datePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(typeCategoryPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(buttonPanel);

        // Populate category combo with options
        categoryCombo.addActionListener(e -> {
            String selectedCategory = (String) categoryCombo.getSelectedItem();
            // Fetch event types based on category
            typeCombo.removeAllItems();
            for (String type : eventService.getEventTypes(selectedCategory)) {
                typeCombo.addItem(type);
            }
        });

        // Initial population of category combo
        for (String category : eventService.getEventCategories()) {
            categoryCombo.addItem(category);
        }
    }
}
