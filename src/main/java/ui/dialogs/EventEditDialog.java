package ui.dialogs;

import services.EventServiceSer;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Dialog that allows editing of event details.
 * Used by the UpcomingEvent view to edit events.
 */
public class EventEditDialog extends JDialog {
  private Map<String, Object> eventDetails;
  private EventServiceSer eventServiceSer;
  private boolean eventUpdated = false;

  // Form components
  private JTextField eventNameField;
  private JTextField eventDateField;
  private JTextField teamAField;
  private JTextField teamBField;
  private JTextArea descriptionArea;
  private JComboBox<String> categoryCombo;
  private JComboBox<String> eventTypeCombo;
  private JTextField locationField;

  /**
   * Constructor for the EventEditDialog
   * 
   * @param parent          The parent window
   * @param eventDetails    A map containing event details from the database
   * @param eventServiceSer The event service for database operations
   */
  public EventEditDialog(Window parent, Map<String, Object> eventDetails, EventServiceSer eventServiceSer) {
    super(parent, "Edit Event", ModalityType.APPLICATION_MODAL);
    this.eventDetails = eventDetails;
    this.eventServiceSer = eventServiceSer;

    // Set dialog properties
    setSize(600, 500);
    setLocationRelativeTo(parent);
    setResizable(true);

    // Create and set content
    JPanel contentPanel = createContentPanel();
    getContentPane().add(contentPanel);
  }

  /**
   * Creates the content panel with all form fields
   */
  private JPanel createContentPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Create header
    JLabel headerLabel = new JLabel("Edit Event");
    headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerLabel.setForeground(new Color(64, 133, 219));
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    headerPanel.add(headerLabel);

    // Create form panel
    JPanel formPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 15);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Event Name
    eventNameField = addFormField(formPanel, gbc, "Event Name:",
        getString(eventDetails, "event_name"));

    // Event Date
    String dateStr = getString(eventDetails, "event_date");
    if (eventDetails.get("event_date") instanceof Date) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      dateStr = dateFormat.format((Date) eventDetails.get("event_date"));
    }
    eventDateField = addFormField(formPanel, gbc, "Date (YYYY-MM-DD HH:MM):", dateStr);

    // Team A
    teamAField = addFormField(formPanel, gbc, "Team A:",
        getString(eventDetails, "team_a"));

    // Team B
    teamBField = addFormField(formPanel, gbc, "Team B:",
        getString(eventDetails, "team_b"));

    // Location - only show if database supports it
    boolean hasLocationField = eventDetails.containsKey("location");
    if (hasLocationField) {
      locationField = addFormField(formPanel, gbc, "Location:",
          getString(eventDetails, "location"));
    } else {
      locationField = new JTextField(""); // Create empty field but don't add to UI
    }

    // Category
    JLabel categoryLabel = new JLabel("Category:");
    categoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
    gbc.gridx = 0;
    gbc.gridy++;
    formPanel.add(categoryLabel, gbc);

    gbc.gridx = 1;
    categoryCombo = new JComboBox<>(new String[] { "Regular", "VIP" });
    categoryCombo.setSelectedItem(getString(eventDetails, "category"));
    formPanel.add(categoryCombo, gbc);

    // Event Type
    JLabel typeLabel = new JLabel("Event Type:");
    typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
    gbc.gridx = 0;
    gbc.gridy++;
    formPanel.add(typeLabel, gbc);

    gbc.gridx = 1;
    eventTypeCombo = new JComboBox<>(new String[] { "Match", "Event" });
    eventTypeCombo.setSelectedItem(getString(eventDetails, "event_type"));
    formPanel.add(eventTypeCombo, gbc);

    // Description
    JLabel descLabel = new JLabel("Description:");
    descLabel.setFont(new Font("Arial", Font.BOLD, 12));
    gbc.gridx = 0;
    gbc.gridy++;
    formPanel.add(descLabel, gbc);

    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    descriptionArea = new JTextArea(getString(eventDetails, "event_description"));
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);
    descriptionArea.setPreferredSize(new Dimension(500, 100));
    descriptionArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

    JScrollPane scrollPane = new JScrollPane(descriptionArea);
    scrollPane.setPreferredSize(new Dimension(500, 100));
    formPanel.add(scrollPane, gbc);

    // Create button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());

    JButton saveButton = new JButton("Save Changes");
    saveButton.addActionListener(e -> saveChanges());

    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    // Add panels to the content panel
    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Adds a text field to the form with a label
   */
  private JTextField addFormField(JPanel panel, GridBagConstraints gbc, String label, String value) {
    JLabel lblField = new JLabel(label);
    lblField.setFont(new Font("Arial", Font.BOLD, 12));
    gbc.gridx = 0;
    gbc.gridy++;
    panel.add(lblField, gbc);

    gbc.gridx = 1;
    JTextField textField = new JTextField(value);
    textField.setPreferredSize(new Dimension(300, 25));
    panel.add(textField, gbc);

    return textField;
  }

  /**
   * Validates form inputs and saves changes
   */
  private void saveChanges() {
    // Get values from form
    String eventName = eventNameField.getText().trim();
    String eventDate = eventDateField.getText().trim();
    String teamA = teamAField.getText().trim();
    String teamB = teamBField.getText().trim();
    String location = locationField.getText().trim();
    String category = (String) categoryCombo.getSelectedItem();
    String eventType = (String) eventTypeCombo.getSelectedItem();
    String description = descriptionArea.getText().trim();

    // Validate inputs
    if (eventName.isEmpty()) {
      showError("Event name cannot be empty.");
      return;
    }

    if (eventDate.isEmpty()) {
      showError("Event date cannot be empty.");
      return;
    }

    if (teamA.isEmpty() || teamB.isEmpty()) {
      showError("Team names cannot be empty.");
      return;
    }

    if (teamA.equals(teamB)) {
      showError("Team A and Team B cannot be the same.");
      return;
    }

    // Get event ID
    Integer eventId = (Integer) eventDetails.get("event_id");
    if (eventId == null) {
      showError("Invalid event ID.");
      return;
    }

    // Call service to update the event, handling the location parameter
    boolean success = false;
    try {
      success = eventServiceSer.editEvent(eventId, eventName, eventDate,
          teamA, teamB, description, category, eventType, location);
    } catch (Exception e) {
      // Handle case when location column doesn't exist
      success = eventServiceSer.editEvent(eventId, eventName, eventDate,
          teamA, teamB, description, category, eventType, null);
    }

    if (success) {
      JOptionPane.showMessageDialog(this,
          "Event updated successfully.",
          "Success",
          JOptionPane.INFORMATION_MESSAGE);

      eventUpdated = true;
      dispose();
    } else {
      showError("Failed to update event: " + eventServiceSer.getLastErrorMessage());
    }
  }

  /**
   * Shows an error message dialog
   */
  private void showError(String message) {
    JOptionPane.showMessageDialog(this,
        message,
        "Validation Error",
        JOptionPane.ERROR_MESSAGE);
  }

  /**
   * Safely gets a string value from the event details map
   */
  private String getString(Map<String, Object> map, String key) {
    Object value = map.get(key);
    return value != null ? value.toString() : "";
  }

  /**
   * Returns whether the event was successfully updated
   */
  public boolean isEventUpdated() {
    return eventUpdated;
  }
}