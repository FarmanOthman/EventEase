package ui.dialogs;

import services.EventCalendarService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Dialog for adding new events to the calendar.
 * Collects event data and uses the service to save it.
 */
public class EventAddDialog extends JDialog {
  private EventCalendarService eventService;
  private boolean confirmed = false;

  // Form fields
  private JTextField eventNameField;
  private JTextField eventDateField;
  private JComboBox<String> categoryCombo;
  private JComboBox<String> eventTypeCombo;
  private JTextField teamAField;
  private JTextField teamBField;
  private JTextArea descriptionArea;

  /**
   * Creates a new event add dialog
   * 
   * @param parent       The parent frame
   * @param selectedDate The pre-selected date (if any)
   */
  public EventAddDialog(Frame parent, LocalDate selectedDate) {
    super(parent, "Add New Event", true);

    eventService = new EventCalendarService();

    createUI(selectedDate);
    setMinimumSize(new Dimension(400, 380));
    pack();
    setLocationRelativeTo(parent);
  }

  /**
   * Creates the dialog UI
   */
  private void createUI(LocalDate selectedDate) {
    // Set layout
    setLayout(new BorderLayout());

    // Create form panel
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

    // Title
    JLabel titleLabel = new JLabel("Add New Event");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
    titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    formPanel.add(titleLabel);
    formPanel.add(Box.createVerticalStrut(12));

    // Form fields
    eventNameField = addFormField(formPanel, "Event Name:", "");

    // Date field with today's date as default
    String dateValue = selectedDate != null ? selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    eventDateField = addFormField(formPanel, "Event Date (YYYY-MM-DD):", dateValue);

    // Category dropdown
    String[] categories = { "Regular", "VIP" };
    categoryCombo = addComboField(formPanel, "Category:", categories);

    // Event type dropdown
    String[] eventTypes = { "Match", "Concert", "Exhibition", "Other" };
    eventTypeCombo = addComboField(formPanel, "Event Type:", eventTypes);

    // Team fields
    teamAField = addFormField(formPanel, "Team A / Performer:", "");
    teamBField = addFormField(formPanel, "Team B / Opponent:", "");

    // Description area
    JLabel descLabel = new JLabel("Description:");
    descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    formPanel.add(descLabel);
    formPanel.add(Box.createVerticalStrut(3));

    descriptionArea = new JTextArea();
    descriptionArea.setRows(3);
    descriptionArea.setLineWrap(true);
    descriptionArea.setWrapStyleWord(true);

    JScrollPane scrollPane = new JScrollPane(descriptionArea);
    scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
    scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
    formPanel.add(scrollPane);
    formPanel.add(Box.createVerticalStrut(12));

    // Button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dispose());

    JButton saveButton = new JButton("Save Event");
    saveButton.addActionListener(e -> saveEvent());

    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    // Add components to dialog
    add(formPanel, BorderLayout.CENTER);
    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Adds a labeled text field to the form
   */
  private JTextField addFormField(JPanel panel, String labelText, String defaultValue) {
    JLabel label = new JLabel(labelText);
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(label);
    panel.add(Box.createVerticalStrut(3));

    JTextField field = new JTextField(defaultValue);
    field.setAlignmentX(Component.LEFT_ALIGNMENT);
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    panel.add(field);
    panel.add(Box.createVerticalStrut(8));

    return field;
  }

  /**
   * Adds a labeled combo box to the form
   */
  private JComboBox<String> addComboField(JPanel panel, String labelText, String[] items) {
    JLabel label = new JLabel(labelText);
    label.setAlignmentX(Component.LEFT_ALIGNMENT);
    panel.add(label);
    panel.add(Box.createVerticalStrut(3));

    JComboBox<String> combo = new JComboBox<>(items);
    combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    panel.add(combo);
    panel.add(Box.createVerticalStrut(8));

    return combo;
  }

  /**
   * Validates and saves the event
   */
  private void saveEvent() {
    // Validate fields
    if (eventNameField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please enter an event name", "Validation Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (eventDateField.getText().trim().isEmpty()) {
      JOptionPane.showMessageDialog(this, "Please enter an event date", "Validation Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Try to save the event
    try {
      boolean success = eventService.addEvent(
          eventNameField.getText().trim(),
          eventDateField.getText().trim(),
          categoryCombo.getSelectedItem().toString(),
          eventTypeCombo.getSelectedItem().toString(),
          teamAField.getText().trim(),
          teamBField.getText().trim(),
          descriptionArea.getText().trim());

      if (success) {
        confirmed = true;
        JOptionPane.showMessageDialog(this, "Event added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
      } else {
        JOptionPane.showMessageDialog(this,
            "Failed to add event: " + eventService.getLastErrorMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Error adding event: " + e.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Returns whether the dialog was confirmed
   */
  public boolean isConfirmed() {
    return confirmed;
  }
}