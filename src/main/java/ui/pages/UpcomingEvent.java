package ui.pages;

import ui.components.Sidebar;
import services.event.EventServiceSer;
import ui.Router;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
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
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(600, 50));
    JLabel headerLabel = new JLabel("Upcoming Events");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);

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
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  private void createFilterSection() {
    // Filter Events Panel
    JPanel filterPanel = new JPanel();
    filterPanel.setBackground(Color.WHITE);
    filterPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Filter Events"),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
    filterPanel.setMaximumSize(new Dimension(800, 80));
    filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Date Filter
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    datePanel.setBackground(Color.WHITE);
    JLabel dateLabel = new JLabel("Date:");
    dateFilter = new JComboBox<>(new String[] { "[Select Date]" });
    datePanel.add(new JLabel("◆"));
    datePanel.add(dateLabel);
    datePanel.add(dateFilter);

    // Location Filter
    JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    locationPanel.setBackground(Color.WHITE);
    JLabel locationLabel = new JLabel("Location:");
    locationFilter = new JComboBox<>(new String[] { "[Select Location]" });
    locationPanel.add(new JLabel("◆"));
    locationPanel.add(locationLabel);
    locationPanel.add(locationFilter);

    // Category Filter
    JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    categoryPanel.setBackground(Color.WHITE);
    JLabel categoryLabel = new JLabel("Category:");
    categoryFilter = new JComboBox<>(new String[] { "[Select Category]" });

    // Add category options from database
    for (String category : eventServiceSer.getEventCategories()) {
      categoryFilter.addItem(category);
    }

    categoryPanel.add(new JLabel("◆"));
    categoryPanel.add(categoryLabel);
    categoryPanel.add(categoryFilter);

    // Add filter action to category filter
    categoryFilter.addActionListener(e -> {
      if (categoryFilter.getSelectedIndex() > 0) {
        String category = (String) categoryFilter.getSelectedItem();
        updateEventTable(category);
      } else {
        loadAllEvents();
      }
    });

    filterPanel.add(datePanel);
    filterPanel.add(locationPanel);
    filterPanel.add(categoryPanel);

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
    String[] columnNames = { "Event Name", "Date", "Category", "Teams", "Action" };
    DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 4; // Only allow editing of the Action column
      }

      @Override
      public Class<?> getColumnClass(int column) {
        return column == 4 ? JButton.class : String.class;
      }
    };

    // Create table
    eventsTable = new JTable(tableModel);
    eventsTable.setRowHeight(40);
    eventsTable.setShowGrid(true);
    eventsTable.setGridColor(new Color(230, 230, 230));
    eventsTable.getTableHeader().setBackground(new Color(64, 133, 219));
    eventsTable.getTableHeader().setForeground(Color.WHITE);
    eventsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

    // Set custom renderer for the Action column
    eventsTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
    eventsTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor());

    // Load events from the database
    loadAllEvents();

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

        // Create the Details button with event ID as client property
        JButton detailsButton = createDetailsButton();
        detailsButton.putClientProperty("event_id", event.get("event_id"));
        detailsButton.putClientProperty("event_type", eventType);

        // Add the tooltip to show the event type
        detailsButton.setToolTipText(eventType + " - Click for details and booking");

        model.addRow(new Object[] {
            eventName + " (" + eventType + ")",
            eventDate,
            category,
            teams,
            detailsButton
        });
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
      model.addRow(new Object[] { "Event 1", "May 21, 2025", "VIP", "Team A vs Team B", createDetailsButton() });
      model.addRow(new Object[] { "Event 2", "June 14, 2025", "Regular", "Team C vs Team D", createDetailsButton() });
    }
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

        // Create the Details button with event ID stored as a property
        JButton detailsButton = createDetailsButton();
        detailsButton.putClientProperty("event_id", event.get("event_id"));
        detailsButton.putClientProperty("event_type", eventType);

        // Add the tooltip to show the event type
        detailsButton.setToolTipText(eventType + " - Click for details and booking");

        model.addRow(new Object[] {
            eventName + " (" + eventType + ")",
            eventDate,
            eventCategory,
            teams,
            detailsButton
        });
      }

      // If no events were found, add a message
      if (events.isEmpty()) {
        model.addRow(new Object[] { "No events found for " + category, "", "", "", null });
      }
    } catch (Exception e) {
      System.out.println("Error loading events by category: " + e.getMessage());
    }
  }

  private JButton createDetailsButton() {
    JButton button = new JButton("Details") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(39, 174, 96));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth("Details")) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString("Details", x, y);
        g2.dispose();
      }
    };
    button.setPreferredSize(new Dimension(80, 30));
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  // Custom renderer for the Details button
  private class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
      setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      return (JButton) value;
    }
  }

  // Custom editor for the Details button
  private class ButtonEditor extends DefaultCellEditor {
    private JButton button;

    public ButtonEditor() {
      super(new JTextField());
      button = new JButton();
      button.setOpaque(true);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      JButton btn = (JButton) value;

      // Navigate to booking page when Details button is clicked
      btn.addActionListener(e -> {
        // Check if this is a Match event and if so, validate it can have tickets
        String eventType = (String) btn.getClientProperty("event_type");
        Integer eventId = (Integer) btn.getClientProperty("event_id");

        if (eventId != null) {
          // Log the event type and ID being viewed
          System.out.println("Viewing details for " + eventType + " event (ID: " + eventId + ")");

          // Store the selected event ID in a static variable or pass it to the
          // BookingView
          // For now, just navigate to the BookingView
          Router.showPage("BookingView");
        }
      });

      return btn;
    }

    @Override
    public Object getCellEditorValue() {
      return button;
    }
  }
}