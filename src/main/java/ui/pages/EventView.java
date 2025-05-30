package ui.pages;
import ui.components.RoundedButton;
import ui.components.Sidebar;
import ui.Refreshable;
import javax.swing.*;
import ui.Router; // Ensure Router is imported from the correct package
// If the Router class is in a different package, update the import statement accordingly
import services.EventServiceSer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

/**
 * Event Management System - Stadium Management
 */
public class EventView extends JPanel implements Refreshable {
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private EventServiceSer eventServiceSer;
    private JComboBox<String> typeCombo;
    private JComboBox<String> categoryCombo;
    // Adding dateSpinner as a class field so we can access it in refresh method
    private JSpinner dateSpinner;

    public EventView() {
        setName("EventView"); // Set the name for the Router to identify this panel
        setLayout(new BorderLayout());

        // Initialize EventServiceSer
        eventServiceSer = new EventServiceSer();

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Create main panel with event form
        createMainPanel();

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        // Check if a date was passed from CalendarView
        Object selectedDateObj = Router.getData("selectedDate");
        if (selectedDateObj != null && selectedDateObj instanceof java.time.LocalDate) {
            java.time.LocalDate selectedDate = (java.time.LocalDate) selectedDateObj;
            
            // Convert LocalDate to Date for the spinner
            java.util.Date date = java.sql.Date.valueOf(selectedDate);
            
            // Set the date in the dateSpinner
            dateSpinner.setValue(date);
            
            Router.clearData("selectedDate"); // Ensure Router is correctly imported or fully qualified
        }
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Create header panel
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(64, 133, 219));
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
       
        contentPanel.add(titleLabel);

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
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD HH):");

        // Create date and time fields in the correct format for the database
        JPanel dateTimePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        dateTimePanel.setBackground(Color.WHITE);

        // Date picker using spinner
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // Set current date as default

        // Time picker using spinner
        JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, " h a" );
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date()); // Set current time as default

        dateTimePanel.add(dateSpinner);
        dateTimePanel.add(timeSpinner);

        datePanel.add(dateLabel, BorderLayout.NORTH);
        datePanel.add(dateTimePanel, BorderLayout.CENTER);

        // Type and Category panels
        JPanel typeCategoryPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        typeCategoryPanel.setBackground(Color.WHITE);
        typeCategoryPanel.setMaximumSize(new Dimension(600, 60));

        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBackground(Color.WHITE);
        JLabel typeLabel = new JLabel("Type:");
        typeCombo = new JComboBox<>(new String[] { "Match", "Event" });
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(typeCombo, BorderLayout.CENTER);

        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBackground(Color.WHITE);
        JLabel categoryLabel = new JLabel("Category:");
        categoryCombo = new JComboBox<>(new String[] { "Regular", "VIP" });
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
        buttonPanel.setMaximumSize(new Dimension(600, 100));

        // Create the rounded green Add button
        RoundedButton addButton = new RoundedButton("Add", 25);
        
        addButton.setBackground(new Color(28, 184, 96));
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setForeground(Color.white);
        addButton.setPreferredSize(new Dimension(120, 40));
        buttonPanel.add(addButton);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


 {
           

        

        // Update addButton.addActionListener to correctly format date and time
        addButton.addActionListener(e -> {
            // Example of adding an event
            String eventName = eventNameField.getText();
            String team1 = team1Field.getText();
            String team2 = team2Field.getText();

            // Format the date and time correctly for database
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H a");
            Date dateValue = (Date) dateSpinner.getValue();
            Date timeValue = (Date) timeSpinner.getValue();

            // Combine date and time values
            Calendar dateCal = Calendar.getInstance();
            dateCal.setTime(dateValue);

            Calendar timeCal = Calendar.getInstance();
            timeCal.setTime(timeValue);

            dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
            

            String date = dateFormat.format(dateCal.getTime());

            String type = (String) typeCombo.getSelectedItem();
            String category = (String) categoryCombo.getSelectedItem();
            String details = detailsArea.getText();

            // Validate inputs
            if (eventName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Event name cannot be empty", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (team1.trim().isEmpty() || team2.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Team names cannot be empty", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (team1.equals(team2)) {
                JOptionPane.showMessageDialog(this, "Team A and Team B cannot be the same", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate that the event date is not in the past
            Date currentDate = new Date();
            if (dateCal.getTime().before(currentDate)) {
                JOptionPane.showMessageDialog(this, "Event date cannot be in the past", "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (category == null || !(category.equals("Regular") || category.equals("VIP"))) {
                JOptionPane.showMessageDialog(this, "Please select a valid category (Regular or VIP)",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println("Selected category: " + category);
            System.out.println("Formatted date: " + date);

            try {
                // Use the service layer instead of directly accessing the server layer
                boolean success = eventServiceSer.addEvent(eventName, date, team1, team2, details, category, type);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Event Added!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Clear the form fields
                    eventNameField.setText("");
                    team1Field.setText("");
                    team2Field.setText("");
                    dateSpinner.setValue(new Date());
                    timeSpinner.setValue(new Date());
                    typeCombo.setSelectedIndex(0);
                    categoryCombo.setSelectedIndex(0);
                    detailsArea.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add event: " + eventServiceSer.getLastErrorMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception error) {
                JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
}
}