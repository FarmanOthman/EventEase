package ui.pages;

import ui.components.Sidebar;
import ui.dialogs.EventDetailsDialog;
import ui.dialogs.EventEditDialog;

import javax.swing.*;
import javax.swing.table.*;

import services.EventServiceSer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Upcoming Events page that displays events from the database.
 * This class only handles the UI aspects, with all business logic
 * and data access delegated to the service layer (EventServiceSer).
 */
public class UpcomingEvent extends JPanel {
  private JPanel mainPanel;
  private JPanel contentPanel;
  private JTable eventsTable;
  private JComboBox<String> dateFilter;
  private JComboBox<String> locationFilter;
  private JComboBox<String> categoryFilter;
  private EventServiceSer eventServiceSer;

  public UpcomingEvent() {
    setLayout(new BorderLayout());

    // Initialize the event service
    eventServiceSer = new EventServiceSer();

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Create main panel
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);
  }

  private void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout(0, 20));
    mainPanel.setBackground(Color.WHITE);

    // Create header panel
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(600, 60));

    JLabel headerLabel = new JLabel("  Upcoming Events");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
    headerPanel.add(headerLabel, BorderLayout.WEST);

    // Add a refresh button
    JButton refreshButton = new JButton("Refresh");
    refreshButton.setBackground(new Color(245, 245, 245));
    refreshButton.setForeground(new Color(64, 133, 219));
    refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
    refreshButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
        BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    refreshButton.setFocusPainted(false);
    refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    refreshButton.addActionListener(e -> loadAllEvents());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBackground(new Color(64, 133, 219));
    buttonPanel.add(refreshButton);
    headerPanel.add(buttonPanel, BorderLayout.EAST);

    // Create content panel
    contentPanel = new JPanel();
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    // Create filter section
    createFilterSection();

    // Create events list section
    createEventsListSection();

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Wrap contentPanel in a JScrollPane
    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
  }

  private void createFilterSection() {
    // Filter Events Panel
    JPanel filterPanel = new JPanel();
    filterPanel.setBackground(Color.WHITE);
    filterPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Filter Events"),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    filterPanel.setLayout(new GridLayout(1, 3, 15, 0));
    filterPanel.setMaximumSize(new Dimension(800, 100));
    filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Date Filter
    JPanel datePanel = new JPanel(new BorderLayout(5, 5));
    datePanel.setBackground(Color.WHITE);
    JLabel dateLabel = new JLabel("Date:");
    dateLabel.setFont(new Font("Arial", Font.BOLD, 12));
    dateFilter = new JComboBox<>(new String[] { "[Select Date]" });
    dateFilter.setBackground(Color.WHITE);
    dateFilter.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    datePanel.add(dateLabel, BorderLayout.NORTH);
    datePanel.add(dateFilter, BorderLayout.CENTER);

    // Category Filter
    JPanel categoryPanel = new JPanel(new BorderLayout(5, 5));
    categoryPanel.setBackground(Color.WHITE);
    JLabel categoryLabel = new JLabel("Category:");
    categoryLabel.setFont(new Font("Arial", Font.BOLD, 12));
    categoryFilter = new JComboBox<>(new String[] { "[Select Category]" });
    categoryFilter.setBackground(Color.WHITE);
    categoryFilter.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // Add category options from database
    for (String category : eventServiceSer.getEventCategories()) {
      categoryFilter.addItem(category);
    }

    categoryPanel.add(categoryLabel, BorderLayout.NORTH);
    categoryPanel.add(categoryFilter, BorderLayout.CENTER);

    // Location Filter - if implemented later
    JPanel locationPanel = new JPanel(new BorderLayout(5, 5));
    locationPanel.setBackground(Color.WHITE);
    JLabel locationLabel = new JLabel("Location:");
    locationLabel.setFont(new Font("Arial", Font.BOLD, 12));
    locationFilter = new JComboBox<>(new String[] { "[Select Location]" });
    locationFilter.setBackground(Color.WHITE);
    locationFilter.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(200, 200, 200)),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    locationPanel.add(locationLabel, BorderLayout.NORTH);
    locationPanel.add(locationFilter, BorderLayout.CENTER);

    // Add filter action to category filter
    categoryFilter.addActionListener(e -> {
      if (categoryFilter.getSelectedIndex() > 0) {
        String category = (String) categoryFilter.getSelectedItem();
        updateEventTable(category);
      } else {
        loadAllEvents();
      }
    });

    // Add a reset button to clear all filters
    JPanel resetPanel = new JPanel(new BorderLayout(5, 5));
    resetPanel.setBackground(Color.WHITE);
    JButton resetButton = new JButton("Reset Filters");
    resetButton.setBackground(new Color(64, 133, 219));
    resetButton.setForeground(Color.WHITE);
    resetButton.setFont(new Font("Arial", Font.BOLD, 12));
    resetButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    resetButton.addActionListener(e -> {
      categoryFilter.setSelectedIndex(0);
      dateFilter.setSelectedIndex(0);
      locationFilter.setSelectedIndex(0);
      loadAllEvents();
    });

    JLabel blankLabel = new JLabel(" ");
    resetPanel.add(blankLabel, BorderLayout.NORTH);
    resetPanel.add(resetButton, BorderLayout.CENTER);

    // Add all panels to the filter panel
    filterPanel.add(categoryPanel);
    filterPanel.add(datePanel);
    filterPanel.add(resetPanel);

    contentPanel.add(filterPanel);
    contentPanel.add(Box.createVerticalStrut(20));
  }

  private void createEventsListSection() {
    // Events List Panel
    JPanel eventsListPanel = new JPanel();
    eventsListPanel.setBackground(Color.WHITE);
    eventsListPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Upcoming Events List"),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    eventsListPanel.setLayout(new BorderLayout());
    eventsListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Create empty table model with column headers
    String[] columnNames = { "Event Name", "Date", "Category", "Teams", "Actions" };
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

    // Create table
    eventsTable = new JTable(tableModel);
    eventsTable.setRowHeight(45);
    eventsTable.setShowGrid(true);
    eventsTable.setGridColor(new Color(230, 230, 230));
    eventsTable.getTableHeader().setBackground(new Color(64, 133, 219));
    eventsTable.getTableHeader().setForeground(Color.WHITE);
    eventsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
    eventsTable.setSelectionBackground(new Color(240, 245, 255));
    eventsTable.setSelectionForeground(Color.BLACK);
    eventsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
      @Override
      public Component getTableCellRendererComponent(JTable table, Object value,
          boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);

        // Skip "Actions" column
        if (column != 4) {
          if (!isSelected) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
          }
          // Add padding
          if (c instanceof JLabel) {
            ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
          }
        }
        return c;
      }
    });

    // Adjust column widths
    eventsTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Event Name
    eventsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Date
    eventsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
    eventsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Teams
    eventsTable.getColumnModel().getColumn(4).setPreferredWidth(250); // Actions

    // Load events from the database
    loadAllEvents();

    // Add mouse listener for button clicks
    eventsTable.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        int row = eventsTable.rowAtPoint(e.getPoint());
        int col = eventsTable.columnAtPoint(e.getPoint());

        if (row >= 0 && col == 4) { // Actions column
          handleActionButton(row, e.getX() - eventsTable.getCellRect(row, col, false).x);
        }
      }
    });

    eventsListPanel.add(new JScrollPane(eventsTable), BorderLayout.CENTER);
    contentPanel.add(eventsListPanel);
  }

  /**
   * Load all events from the database and populate the table
   */
  private void loadAllEvents() {
    try {
      // Clear the table
      DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
      model.setRowCount(0);

      // Get all events from the service
      List<Map<String, Object>> events = eventServiceSer.getAllEvents();

      // Date formatter for consistent display
      SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      // Add events to the table
      for (Map<String, Object> event : events) {
        String eventName = (String) event.get("event_name");

        // Format date properly
        String eventDate = "Unknown date";
        if (event.get("event_date") != null) {
          try {
            eventDate = event.get("event_date").toString();
            // If the event_date is a Date object, format it nicely
            if (event.get("event_date") instanceof Date) {
              eventDate = displayDateFormat.format((Date) event.get("event_date"));
            }
          } catch (Exception ex) {
            System.out.println("Error formatting date: " + ex.getMessage());
          }
        }

        String category = (String) event.get("category");
        String eventType = (String) event.get("event_type");
        String teamA = (String) event.get("team_a");
        String teamB = (String) event.get("team_b");
        String teams = teamA + " vs " + teamB;

        // Get the event ID
        Integer eventId = (Integer) event.get("event_id");

        // Store object data in a row
        Object[] rowData = new Object[5];
        rowData[0] = eventName + " (" + eventType + ")";
        rowData[1] = eventDate;
        rowData[2] = category;
        rowData[3] = teams;

        // Store event data in the Actions cell
        ActionData actionData = new ActionData();
        actionData.eventId = eventId;
        actionData.eventName = eventName;
        actionData.eventType = eventType;
        rowData[4] = actionData;

        model.addRow(rowData);
      }

      // If no events were found, add a message
      if (events.isEmpty()) {
        model.addRow(new Object[] { "No events found", "", "", "", null });
      }
    } catch (Exception e) {
      System.out.println("Error loading events: " + e.getMessage());

      // Add some default data if database load fails
      DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
      model.setRowCount(0);

      // Create dummy action data
      ActionData actionData = new ActionData();
      actionData.eventId = 1;
      actionData.eventName = "Event 1";
      actionData.eventType = "Match";

      model.addRow(new Object[] {
          "Event 1", "May 21, 2025", "VIP", "Team A vs Team B",
          actionData
      });

      actionData = new ActionData();
      actionData.eventId = 2;
      actionData.eventName = "Event 2";
      actionData.eventType = "Event";

      model.addRow(new Object[] {
          "Event 2", "June 14, 2025", "Regular", "Team C vs Team D",
          actionData
      });
    }

    // Add custom renderer for the actions column
    eventsTable.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
  }

  /**
   * Update the event table based on selected category
   */
  private void updateEventTable(String category) {
    try {
      // Clear the table
      DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
      model.setRowCount(0);

      // Get filtered events from the service
      List<Map<String, Object>> events = eventServiceSer.getEventsByCategory(category);

      // Date formatter for consistent display
      SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

      // Add events to the table
      for (Map<String, Object> event : events) {
        String eventName = (String) event.get("event_name");

        // Format date properly
        String eventDate = "Unknown date";
        if (event.get("event_date") != null) {
          try {
            eventDate = event.get("event_date").toString();
            // If the event_date is a Date object, format it nicely
            if (event.get("event_date") instanceof Date) {
              eventDate = displayDateFormat.format((Date) event.get("event_date"));
            }
          } catch (Exception ex) {
            System.out.println("Error formatting date: " + ex.getMessage());
          }
        }

        String eventCategory = (String) event.get("category");
        String eventType = (String) event.get("event_type");
        String teamA = (String) event.get("team_a");
        String teamB = (String) event.get("team_b");
        String teams = teamA + " vs " + teamB;

        // Get the event ID
        Integer eventId = (Integer) event.get("event_id");

        // Store object data in a row
        Object[] rowData = new Object[5];
        rowData[0] = eventName + " (" + eventType + ")";
        rowData[1] = eventDate;
        rowData[2] = eventCategory;
        rowData[3] = teams;

        // Store event data in the Actions cell
        ActionData actionData = new ActionData();
        actionData.eventId = eventId;
        actionData.eventName = eventName;
        actionData.eventType = eventType;
        rowData[4] = actionData;

        model.addRow(rowData);
      }

      // If no events were found, add a message
      if (events.isEmpty()) {
        model.addRow(new Object[] { "No events found for " + category, "", "", "", null });
      }
    } catch (Exception e) {
      System.out.println("Error loading events by category: " + e.getMessage());
    }

    // Add custom renderer for the actions column
    eventsTable.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
  }

  // Class to store action data for events
  private class ActionData {
    public Integer eventId;
    public String eventName;
    public String eventType;
  }

  // Custom renderer for the actions column
  private class ActionRenderer extends JPanel implements TableCellRenderer {
    private JButton detailsBtn;
    private JButton editBtn;
    private JButton deleteBtn;

    public ActionRenderer() {
      setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
      setBorder(BorderFactory.createEmptyBorder());
      setOpaque(true);

      // Create buttons
      detailsBtn = createButton("Details", new Color(0, 123, 255));
      editBtn = createButton("Edit", new Color(255, 153, 0));
      deleteBtn = createButton("Delete", new Color(220, 53, 69));

      add(detailsBtn);
      add(editBtn);
      add(deleteBtn);
    }

    private JButton createButton(String text, Color color) {
      JButton btn = new JButton(text);
      btn.setBackground(color);
      btn.setForeground(Color.WHITE);
      btn.setFont(new Font("Arial", Font.BOLD, 11));
      btn.setFocusPainted(false);
      btn.setBorderPainted(false);
      btn.setOpaque(true);
      btn.setPreferredSize(new Dimension(68, 25));
      // Add rounded corners effect
      btn.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(color, 1),
          BorderFactory.createEmptyBorder(4, 8, 4, 8)));
      return btn;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      // Adjust background color based on selection
      Color bg;
      if (isSelected) {
        bg = new Color(240, 245, 255);
      } else {
        bg = row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250);
      }
      setBackground(bg);

      return this;
    }
  }

  /**
   * Handle clicks on action buttons based on their position
   */
  private void handleActionButton(int row, int xOffset) {
    // Get action data from the table
    DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
    ActionData actionData = (ActionData) model.getValueAt(row, 4);

    if (actionData == null)
      return;

    // Determine which button was clicked based on x position
    // Each button is approximately 68px wide with 8px spacing in the renderer
    if (xOffset < 76) {
      // Details button
      showEventDetails(actionData);
    } else if (xOffset < 152) {
      // Edit button
      editEvent(actionData);
    } else {
      // Delete button
      deleteEvent(actionData);
    }
  }

  /**
   * Show event details dialog
   */
  private void showEventDetails(ActionData actionData) {
    if (actionData.eventId != null) {
      try {
        // Get event details from the service
        Map<String, Object> eventDetails = eventServiceSer.getEventDetails(actionData.eventId);

        if (eventDetails != null) {
          // Create and show the details dialog
          EventDetailsDialog dialog = new EventDetailsDialog(
              SwingUtilities.getWindowAncestor(UpcomingEvent.this),
              eventDetails);
          dialog.setVisible(true);
        } else {
          JOptionPane.showMessageDialog(UpcomingEvent.this,
              "Could not find event details.",
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(UpcomingEvent.this,
            "Error retrieving event details: " + ex.getMessage() + " (Type: " + actionData.eventType + ")",
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Edit event dialog
   */
  private void editEvent(ActionData actionData) {
    if (actionData.eventId != null) {
      try {
        // Get event details from the service
        Map<String, Object> eventDetails = eventServiceSer.getEventDetails(actionData.eventId);

        if (eventDetails != null) {
          // Create and show the edit dialog
          EventEditDialog dialog = new EventEditDialog(
              SwingUtilities.getWindowAncestor(UpcomingEvent.this),
              eventDetails,
              eventServiceSer);

          dialog.setVisible(true);

          // Refresh the table if the edit was successful
          if (dialog.isEventUpdated()) {
            loadAllEvents();
          }
        } else {
          JOptionPane.showMessageDialog(UpcomingEvent.this,
              "Could not find event details for editing.",
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(UpcomingEvent.this,
            "Error editing event: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Delete event with confirmation
   */
  private void deleteEvent(ActionData actionData) {
    if (actionData.eventId != null) {
      // Show confirmation dialog
      int option = JOptionPane.showConfirmDialog(
          UpcomingEvent.this,
          "Are you sure you want to delete event: " + actionData.eventName + "?",
          "Confirm Deletion",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);

      if (option == JOptionPane.YES_OPTION) {
        try {
          // Call service to delete the event
          boolean success = eventServiceSer.deleteEvent(actionData.eventId);

          if (success) {
            JOptionPane.showMessageDialog(
                UpcomingEvent.this,
                "Event deleted successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh the table
            loadAllEvents();
          } else {
            JOptionPane.showMessageDialog(
                UpcomingEvent.this,
                "Failed to delete event: " + eventServiceSer.getLastErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(
              UpcomingEvent.this,
              "Error deleting event: " + ex.getMessage(),
              "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
}