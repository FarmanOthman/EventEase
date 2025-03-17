package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * TODO: Upcoming Events System Architecture
 * 1. Create the following structure:
 * services/
 * ├── events/
 * │ ├── EventListingService.java # Event listing management
 * │ ├── EventFilterService.java # Search and filtering
 * │ ├── EventDetailsService.java # Event information
 * │ └── EventPromotionService.java # Featured events
 * └── display/
 * ├── ListingManager.java # Display management
 * └── SortingService.java # Custom sorting
 *
 * 2. Event Features:
 * - Advanced filtering
 * - Smart sorting
 * - Event previews
 * - Quick booking
 *
 * 3. Integration Points:
 * - Event database
 * - Booking system
 * - Calendar system
 * - Notification system
 *
 * 4. Listing Features:
 * - Infinite scroll
 * - Dynamic loading
 * - List/Grid view toggle
 * - Custom view options
 *
 * 5. Search Capabilities:
 * - Full-text search
 * - Advanced filters
 * - Saved searches
 * - Search history
 *
 * 6. Event Details:
 * - Rich event preview
 * - Quick actions
 * - Share functionality
 * - Save/Bookmark
 *
 * 7. Notification Features:
 * - Event reminders
 * - Price alerts
 * - Availability alerts
 * - Custom notifications
 *
 * 8. User Preferences:
 * - Favorite categories
 * - Location settings
 * - Price range filters
 * - Display preferences
 *
 * 9. Performance Features:
 * - Data caching
 * - Lazy loading
 * - Image optimization
 * - Response compression
 *
 * 10. Analytics Integration:
 * - View tracking
 * - Click analysis
 * - Search patterns
 * - User behavior
 */
public class UpcomingEvent extends JPanel {
  private JPanel mainPanel;
  private JPanel contentPanel;
  private JTable eventsTable;
  private JComboBox<String> dateFilter;
  private JComboBox<String> locationFilter;
  private JComboBox<String> categoryFilter;

  public UpcomingEvent() {
    setLayout(new BorderLayout());

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
    categoryPanel.add(new JLabel("◆"));
    categoryPanel.add(categoryLabel);
    categoryPanel.add(categoryFilter);

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

    // Create table model
    DefaultTableModel tableModel = new DefaultTableModel(
        new Object[][] {
            { "Event 1", "May 21, 2025", "VIP", createDetailsButton() },
            { "Event 2", "June 14, 2025", "Normal", createDetailsButton() },
            { "Event 3", "July 10, 2025", "Normal", createDetailsButton() }
        },
        new String[] { "Event Name", "Date", "Category", "Action" }) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 3; // Only allow editing of the Action column
      }

      @Override
      public Class<?> getColumnClass(int column) {
        return column == 3 ? JButton.class : String.class;
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
    eventsTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
    eventsTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor());

    eventsListPanel.add(new JScrollPane(eventsTable), BorderLayout.CENTER);
    contentPanel.add(eventsListPanel);
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
      button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      return (JButton) value;
    }

    @Override
    public Object getCellEditorValue() {
      return button;
    }
  }
}