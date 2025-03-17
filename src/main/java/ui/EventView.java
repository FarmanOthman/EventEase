package ui;

import ui.components.Sidebar;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import database.QueryBuilder; // Import QueryBuilder

public class EventView extends JFrame {
    private JPanel mainPanel, contentPanel;
    private JFrame frame;

    // Create QueryBuilder instance to interact with the database
    private QueryBuilder queryBuilder;

    public EventView() {
        setTitle("Event Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout());

        // Use the separate Sidebar component
        mainPanel.add(new Sidebar(this), BorderLayout.WEST);

        // Create main panel with event form
        createMainPanel();

        // Initialize QueryBuilder (connect to database)
        queryBuilder = new QueryBuilder();

        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void createMainPanel() {
        mainPanel.setBackground(Color.WHITE);

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(64, 133, 219)); // Same blue as sidebar
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
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"choose"});
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(typeCombo, BorderLayout.CENTER);

        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(Color.WHITE);
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"choose"});
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
                g2.setColor(new Color(28, 184, 96)); // Bright green color matching the image
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
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get data from the form fields
                String eventName = eventNameField.getText();
                String team1 = team1Field.getText();
                String team2 = team2Field.getText();
                String eventDate = dateField.getText();
                String eventType = (String) typeCombo.getSelectedItem();
                String eventCategory = (String) categoryCombo.getSelectedItem();
                String eventDetails = detailsArea.getText();

                // Create a map for the values to insert into the database
                Map<String, Object> values = new HashMap<>();
                values.put("event_name", eventName);
                values.put("team1", team1);
                values.put("team2", team2);
                values.put("event_date", eventDate);
                values.put("event_type", eventType);
                values.put("event_category", eventCategory);
                values.put("event_details", eventDetails);

                // Insert data into database using QueryBuilder
                queryBuilder.insert("events", values); // "events" is the name of the table

                // Show confirmation dialog
                JOptionPane.showMessageDialog(EventView.this,
                        "Event Added Successfully!\n" +
                                "Event Name: " + eventName + "\n" +
                                "Team 1: " + team1 + "\n" +
                                "Team 2: " + team2 + "\n" +
                                "Date: " + eventDate + "\n" +
                                "Type: " + eventType + "\n" +
                                "Category: " + eventCategory + "\n" +
                                "Details: " + eventDetails,
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
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
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new EventView());
    }
}
