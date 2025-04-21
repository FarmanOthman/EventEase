package ui.dialogs;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Dialog that displays detailed information about an event.
 * Used by the UpcomingEvent view to show event details.
 */
public class EventDetailsDialog extends JDialog {
  private Map<String, Object> eventDetails;

  /**
   * Constructor for the EventDetailsDialog
   * 
   * @param parent       The parent window
   * @param eventDetails A map containing event details from the database
   */
  public EventDetailsDialog(Window parent, Map<String, Object> eventDetails) {
    super(parent, "Event Details", ModalityType.APPLICATION_MODAL);
    this.eventDetails = eventDetails;

    // Set dialog properties
    setSize(600, 400);
    setLocationRelativeTo(parent);
    setResizable(true);

    // Create and set content
    JPanel contentPanel = createContentPanel();
    getContentPane().add(contentPanel);
  }

  /**
   * Creates the content panel with all event details
   */
  private JPanel createContentPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Create header
    JLabel headerLabel = new JLabel("Event Details");
    headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerLabel.setForeground(new Color(64, 133, 219));
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    headerPanel.add(headerLabel);

    // Create details panel with a grid layout
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 5, 5, 15);

    // Event Name
    addDetailRow(detailsPanel, gbc, "Event Name:",
        getString(eventDetails, "event_name"));

    // Event Type
    addDetailRow(detailsPanel, gbc, "Event Type:",
        getString(eventDetails, "event_type"));

    // Event Date
    String dateStr = getString(eventDetails, "event_date");
    if (eventDetails.get("event_date") instanceof Date) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      dateStr = dateFormat.format((Date) eventDetails.get("event_date"));
    }
    addDetailRow(detailsPanel, gbc, "Date:", dateStr);

    // Category
    addDetailRow(detailsPanel, gbc, "Category:",
        getString(eventDetails, "category"));

    // Teams
    String teamA = getString(eventDetails, "team_a");
    String teamB = getString(eventDetails, "team_b");
    addDetailRow(detailsPanel, gbc, "Teams:", teamA + " vs " + teamB);

    // Location - Only display if present in the event details
    if (eventDetails.containsKey("location") && eventDetails.get("location") != null) {
      addDetailRow(detailsPanel, gbc, "Location:",
          getString(eventDetails, "location"));
    }

    // Description
    String description = getString(eventDetails, "event_description");
    if (description != null && !description.isEmpty()) {
      gbc.gridx = 0;
      gbc.gridy++;
      gbc.gridwidth = 1;
      JLabel descLabel = new JLabel("Description:");
      descLabel.setFont(new Font("Arial", Font.BOLD, 12));
      detailsPanel.add(descLabel, gbc);

      gbc.gridx = 0;
      gbc.gridy++;
      gbc.gridwidth = 2;

      // Multi-line description in a scroll pane
      JTextArea descArea = new JTextArea(description);
      descArea.setLineWrap(true);
      descArea.setWrapStyleWord(true);
      descArea.setEditable(false);
      descArea.setBackground(panel.getBackground());
      descArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      JScrollPane scrollPane = new JScrollPane(descArea);
      scrollPane.setPreferredSize(new Dimension(500, 100));
      scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      detailsPanel.add(scrollPane, gbc);
    }

    // Create button panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(e -> dispose());
    buttonPanel.add(closeButton);

    // Add panels to the content panel
    panel.add(headerPanel, BorderLayout.NORTH);
    panel.add(new JScrollPane(detailsPanel), BorderLayout.CENTER);
    panel.add(buttonPanel, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Adds a row with a label and value to the details panel
   */
  private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
    // Add label
    JLabel lblField = new JLabel(label);
    lblField.setFont(new Font("Arial", Font.BOLD, 12));
    panel.add(lblField, gbc);

    // Add value
    gbc.gridx = 1;
    JLabel lblValue = new JLabel(value != null ? value : "N/A");
    lblValue.setFont(new Font("Arial", Font.PLAIN, 12));
    panel.add(lblValue, gbc);

    // Reset for next row
    gbc.gridx = 0;
    gbc.gridy++;
  }

  /**
   * Safely gets a string value from the event details map
   */
  private String getString(Map<String, Object> map, String key) {
    Object value = map.get(key);
    return value != null ? value.toString() : "N/A";
  }
}