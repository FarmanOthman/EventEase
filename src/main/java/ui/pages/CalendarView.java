package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: Calendar System Architecture
 * 1. Create the following structure:
 * services/
 * ├── calendar/
 * │ ├── CalendarService.java # Core calendar functionality
 * │ ├── EventManager.java # Event handling
 * │ ├── ReminderService.java # Reminders and alerts
 * │ └── SyncService.java # Calendar synchronization
 * └── scheduling/
 * ├── ScheduleManager.java # Schedule management
 * └── ConflictResolver.java # Event conflict handling
 *
 * 2. Calendar Features:
 * - Event creation and management
 * - Recurring events support
 * - Multi-calendar view
 * - Event categorization
 *
 * 3. Integration Points:
 * - External calendar sync
 * - Notification system
 * - Event booking system
 * - User preferences
 *
 * 4. Calendar View Features:
 * - Day/Week/Month view toggle
 * - Event drag and drop
 * - Event duration visualization
 * - Time zone support
 *
 * 5. Event Management:
 * - Click to create new event
 * - Event details popup
 * - Event editing and deletion
 * - Event color coding
 *
 * 6. Data Persistence:
 * - Local storage of events
 * - Cloud sync capability
 * - Offline mode support
 * - Data backup/restore
 *
 * 7. User Interface:
 * - Keyboard navigation
 * - Touch screen support
 * - Accessibility features
 * - Print calendar view
 *
 * 8. Event Features:
 * - Recurring event patterns
 * - Event reminders/alerts
 * - Event categories/tags
 * - Event sharing
 *
 * 9. Search and Filter:
 * - Event search
 * - Category filtering
 * - Date range filtering
 * - Advanced search options
 */
public class CalendarView extends JPanel {
  private JPanel mainPanel;
  private JPanel calendarPanel;
  private JLabel monthYearLabel;
  private YearMonth currentYearMonth;
  private Map<LocalDate, String> events;

  public CalendarView() {
    setLayout(new BorderLayout());

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Initialize events (this would typically come from a database)
    events = new HashMap<>();
    events.put(LocalDate.of(2025, 3, 25), "VIP-Event");

    // Set current month and year
    currentYearMonth = YearMonth.of(2025, 3); // March 2025

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
    JLabel headerLabel = new JLabel("Calendar");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);

    // Create calendar content
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    // Create calendar navigation panel
    JPanel navigationPanel = createNavigationPanel();
    contentPanel.add(navigationPanel);
    contentPanel.add(Box.createVerticalStrut(20));

    // Create calendar grid
    calendarPanel = new JPanel(new GridLayout(7, 7, 5, 5));
    calendarPanel.setBackground(Color.WHITE);
    updateCalendar();
    contentPanel.add(calendarPanel);

    // Create legend panel
    JPanel legendPanel = createLegendPanel();
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(legendPanel);

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  private JPanel createNavigationPanel() {
    JPanel navigationPanel = new JPanel(new BorderLayout());
    navigationPanel.setBackground(new Color(64, 133, 219));
    navigationPanel.setMaximumSize(new Dimension(800, 40));
    navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    // Previous month button
    JButton prevButton = new JButton("◀") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth("◀")) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString("◀", x, y);
        g2.dispose();
      }
    };
    prevButton.setContentAreaFilled(false);
    prevButton.setBorderPainted(false);
    prevButton.setFocusPainted(false);
    prevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    prevButton.setForeground(Color.WHITE);
    prevButton.addActionListener(e -> {
      currentYearMonth = currentYearMonth.minusMonths(1);
      updateCalendar();
    });

    // Month and year label
    monthYearLabel = new JLabel("", SwingConstants.CENTER);
    monthYearLabel.setFont(new Font("Arial", Font.BOLD, 18));
    monthYearLabel.setForeground(Color.WHITE);

    // Next month button
    JButton nextButton = new JButton("▶") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth("▶")) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString("▶", x, y);
        g2.dispose();
      }
    };
    nextButton.setContentAreaFilled(false);
    nextButton.setBorderPainted(false);
    nextButton.setFocusPainted(false);
    nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    nextButton.setForeground(Color.WHITE);
    nextButton.addActionListener(e -> {
      currentYearMonth = currentYearMonth.plusMonths(1);
      updateCalendar();
    });

    navigationPanel.add(prevButton, BorderLayout.WEST);
    navigationPanel.add(monthYearLabel, BorderLayout.CENTER);
    navigationPanel.add(nextButton, BorderLayout.EAST);

    return navigationPanel;
  }

  private JPanel createLegendPanel() {
    JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    legendPanel.setBackground(Color.WHITE);
    legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    JLabel legendLabel = new JLabel("Legend:");
    legendLabel.setFont(new Font("Arial", Font.BOLD, 12));

    JPanel vipEventIndicator = new JPanel();
    vipEventIndicator.setPreferredSize(new Dimension(10, 10));
    vipEventIndicator.setBackground(Color.RED);

    JLabel vipEventLabel = new JLabel("VIP-Event");
    vipEventLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    legendPanel.add(legendLabel);
    legendPanel.add(Box.createHorizontalStrut(10));
    legendPanel.add(vipEventIndicator);
    legendPanel.add(Box.createHorizontalStrut(5));
    legendPanel.add(vipEventLabel);

    return legendPanel;
  }

  private void updateCalendar() {
    calendarPanel.removeAll();

    // Update month/year label
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    monthYearLabel.setText(currentYearMonth.format(formatter));

    // Add day labels
    String[] days = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    for (String day : days) {
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
      calendarPanel.add(dayLabel);
    }

    // Get the first day of the month and the total days
    LocalDate firstOfMonth = currentYearMonth.atDay(1);
    int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
    int daysInMonth = currentYearMonth.lengthOfMonth();

    // Add empty labels for days before the first of the month
    for (int i = 0; i < dayOfWeek; i++) {
      calendarPanel.add(new JLabel(""));
    }

    // Add day buttons
    for (int i = 1; i <= daysInMonth; i++) {
      LocalDate date = currentYearMonth.atDay(i);
      JPanel dayPanel = new JPanel(new BorderLayout());
      dayPanel.setBackground(Color.WHITE);
      dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

      JLabel dayLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
      dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
      dayPanel.add(dayLabel, BorderLayout.CENTER);

      // Add event indicator if there's an event on this day
      if (events.containsKey(date)) {
        JPanel indicator = new JPanel();
        indicator.setPreferredSize(new Dimension(8, 8));
        indicator.setBackground(Color.RED);
        JPanel indicatorWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        indicatorWrapper.setBackground(Color.WHITE);
        indicatorWrapper.add(indicator);
        dayPanel.add(indicatorWrapper, BorderLayout.SOUTH);
      }

      calendarPanel.add(dayPanel);
    }

    // Add empty labels for remaining cells
    int remainingCells = 42 - (dayOfWeek + daysInMonth); // 42 = 6 rows * 7 days
    for (int i = 0; i < remainingCells; i++) {
      calendarPanel.add(new JLabel(""));
    }

    calendarPanel.revalidate();
    calendarPanel.repaint();
  }
}