package ui.pages;

import ui.components.Sidebar;
import services.EventCalendarService;
import ui.dialogs.EventAddDialog;
import ui.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * Calendar view that displays events and allows interaction with dates.
 * Shows VIP events with special indicators and displays event details on click.
 * This class focuses only on presentation logic, delegating data operations to
 * services.
 */
public class CalendarView extends JPanel {
  // UI Components
  private JPanel mainPanel;
  private JPanel calendarPanel;
  private JLabel monthYearLabel;
  private JLabel statusLabel;

  // Data
  private YearMonth currentYearMonth;
  private Map<LocalDate, List<Map<String, Object>>> eventsByDate;

  // Services
  private EventCalendarService eventService;

  // UI colors
  private Color primaryColor = new Color(64, 133, 219);
  private Color accentColor = new Color(46, 204, 113);
  private Color vipColor = new Color(255, 69, 0); // Orange-red for VIP events
  private Color lightGrayColor = new Color(245, 245, 245);

  public CalendarView() {
    setLayout(new BorderLayout());

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Initialize services
    eventService = new EventCalendarService();

    // Initialize data structures
    eventsByDate = new HashMap<>();
    currentYearMonth = YearMonth.now();

    // Create and add UI components
    createMainPanel();
    add(mainPanel, BorderLayout.CENTER);

    // Load initial data
    loadEventsForCurrentMonth();
  }

  /**
   * Creates the main panel with all calendar components
   */
  private void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create header panel
    JPanel headerPanel = createHeaderPanel();

    // Create content panel
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
    contentPanel.add(calendarPanel);

    // Create legend panel
    JPanel legendPanel = createLegendPanel();
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(legendPanel);

    // Create status bar
    JPanel statusPanel = createStatusPanel();

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    mainPanel.add(statusPanel, BorderLayout.SOUTH);

    // Populate calendar initially
    updateCalendar();
  }

  /**
   * Creates the header panel with title
   */
  private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(primaryColor);
    headerPanel.setPreferredSize(new Dimension(0, 40));
    JLabel headerLabel = new JLabel("Event Calendar");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
    headerPanel.add(headerLabel);
    return headerPanel;
  }

  /**
   * Creates the status panel at the bottom
   */
  private JPanel createStatusPanel() {
    JPanel statusPanel = new JPanel(new BorderLayout());
    statusPanel.setBackground(lightGrayColor);
    statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    statusLabel = new JLabel("Calendar ready");
    statusLabel.setForeground(Color.DARK_GRAY);
    statusPanel.add(statusLabel, BorderLayout.WEST);
    return statusPanel;
  }

  /**
   * Creates the navigation panel with month controls
   */
  private JPanel createNavigationPanel() {
    JPanel navigationPanel = new JPanel(new BorderLayout());
    navigationPanel.setBackground(primaryColor);
    navigationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    navigationPanel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

    // Previous month button
    JButton prevButton = createStyledButton("<", Color.WHITE);
    prevButton.addActionListener(e -> {
      currentYearMonth = currentYearMonth.minusMonths(1);
      loadEventsForCurrentMonth();
      updateCalendar();
    });

    // Month and year label
    monthYearLabel = new JLabel("", SwingConstants.CENTER);
    monthYearLabel.setFont(new Font("Arial", Font.BOLD, 18));
    monthYearLabel.setForeground(Color.WHITE);

    // Next month button
    JButton nextButton = createStyledButton(">", Color.WHITE);
    nextButton.addActionListener(e -> {
      currentYearMonth = currentYearMonth.plusMonths(1);
      loadEventsForCurrentMonth();
      updateCalendar();
    });

    // Today button
    JButton todayButton = createStyledButton("Today", Color.WHITE);
    todayButton.addActionListener(e -> {
      currentYearMonth = YearMonth.now();
      loadEventsForCurrentMonth();
      updateCalendar();
    });

    // Add event button
    RoundedButton addEventButton = new RoundedButton("+ Add Event", 25);
    addEventButton.setBackground(new Color(28, 184, 96)); // Green color
    addEventButton.setFont(new Font("Arial", Font.BOLD, 14));
    addEventButton.setForeground(Color.white);
    addEventButton.setPreferredSize(new Dimension(120, 40));
    addEventButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    addEventButton.addActionListener(e -> showAddEventDialog(null));

    JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leftPanel.setBackground(primaryColor);
    leftPanel.add(prevButton);
    leftPanel.add(Box.createHorizontalStrut(10));
    leftPanel.add(todayButton);

    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightPanel.setBackground(primaryColor);
    rightPanel.add(addEventButton);
    rightPanel.add(Box.createHorizontalStrut(10));
    rightPanel.add(nextButton);

    navigationPanel.add(leftPanel, BorderLayout.WEST);
    navigationPanel.add(monthYearLabel, BorderLayout.CENTER);
    navigationPanel.add(rightPanel, BorderLayout.EAST);

    return navigationPanel;
  }

  /**
   * Creates the legend panel explaining calendar symbols
   */
  private JPanel createLegendPanel() {
    JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    legendPanel.setBackground(Color.WHITE);
    legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

    JLabel legendLabel = new JLabel("Legend:");
    legendLabel.setFont(new Font("Arial", Font.BOLD, 12));

    JPanel vipEventIndicator = new JPanel();
    vipEventIndicator.setPreferredSize(new Dimension(15, 15));
    vipEventIndicator.setBackground(vipColor);

    JLabel vipEventLabel = new JLabel("VIP Event");
    vipEventLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    JPanel regularEventIndicator = new JPanel();
    regularEventIndicator.setPreferredSize(new Dimension(15, 15));
    regularEventIndicator.setBackground(accentColor);

    JLabel regularEventLabel = new JLabel("Regular Event");
    regularEventLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    JLabel clickHintLabel = new JLabel("• Click on a day to view event details");
    clickHintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    clickHintLabel.setForeground(Color.GRAY);

    legendPanel.add(legendLabel);
    legendPanel.add(Box.createHorizontalStrut(10));
    legendPanel.add(vipEventIndicator);
    legendPanel.add(Box.createHorizontalStrut(5));
    legendPanel.add(vipEventLabel);
    legendPanel.add(Box.createHorizontalStrut(15));
    legendPanel.add(regularEventIndicator);
    legendPanel.add(Box.createHorizontalStrut(5));
    legendPanel.add(regularEventLabel);
    legendPanel.add(Box.createHorizontalStrut(15));
    legendPanel.add(clickHintLabel);

    return legendPanel;
  }

  /**
   * Loads event data for the current month using the service
   */
  private void loadEventsForCurrentMonth() {
    try {
      // Clear existing events
      eventsByDate.clear();

      // Get all events for current month via service
      List<Map<String, Object>> monthEvents = eventService.getEventsForMonth(
          currentYearMonth.getYear(),
          currentYearMonth.getMonthValue());

      // Organize events by date for easier access in the UI
      for (Map<String, Object> event : monthEvents) {
        String eventDateStr = (String) event.get("event_date");
        LocalDate eventDate = LocalDate.parse(eventDateStr.substring(0, 10)); // Extract YYYY-MM-DD part

        if (!eventsByDate.containsKey(eventDate)) {
          eventsByDate.put(eventDate, new ArrayList<>());
        }

        eventsByDate.get(eventDate).add(event);
      }

      int totalEvents = monthEvents.size();
      statusLabel.setText(String.format("Loaded %d events for %s", totalEvents,
          currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))));

    } catch (Exception e) {
      statusLabel.setText("Error loading events: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Updates the calendar grid with days and event indicators
   */
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
      final LocalDate date = currentYearMonth.atDay(i);
      JPanel dayPanel = createDayPanel(date, i);
      calendarPanel.add(dayPanel);
    }

    // Add empty labels for remaining cells to complete the grid
    int remainingCells = 42 - (dayOfWeek + daysInMonth); // 42 = 6 rows * 7 days
    for (int i = 0; i < remainingCells; i++) {
      calendarPanel.add(new JLabel(""));
    }

    calendarPanel.revalidate();
    calendarPanel.repaint();
  }

  /**
   * Creates a panel for a single day with appropriate event indicators
   */
  private JPanel createDayPanel(LocalDate date, int dayNumber) {
    JPanel dayPanel = new JPanel(new BorderLayout());
    dayPanel.setBackground(Color.WHITE);
    dayPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

    // Highlight today
    if (date.equals(LocalDate.now())) {
      dayPanel.setBackground(new Color(230, 230, 250)); // Light purple for today
      dayPanel.setBorder(BorderFactory.createLineBorder(primaryColor, 1));
    }

    // Add day number label
    JLabel dayLabel = new JLabel(String.valueOf(dayNumber), SwingConstants.CENTER);
    dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    dayPanel.add(dayLabel, BorderLayout.NORTH);

    // Check if there are events for this date and add indicators
    boolean hasEvents = eventsByDate.containsKey(date) && !eventsByDate.get(date).isEmpty();

    if (hasEvents) {
      // Create indicators panel
      JPanel indicatorsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
      indicatorsPanel.setBackground(dayPanel.getBackground());

      // Get events for this day
      List<Map<String, Object>> dayEvents = eventsByDate.get(date);
      int eventCount = 0;

      for (Map<String, Object> event : dayEvents) {
        String category = (String) event.get("category");

        // Show max 3 indicators
        if (eventCount < 3) {
          JPanel indicator = new JPanel();
          indicator.setPreferredSize(new Dimension(8, 8));
          indicator.setBackground("VIP".equalsIgnoreCase(category) ? vipColor : accentColor);
          indicatorsPanel.add(indicator);
          eventCount++;
        }
      }

      // Show count if more than 3 events
      if (dayEvents.size() > 3) {
        JLabel countLabel = new JLabel("+" + (dayEvents.size() - 3), SwingConstants.CENTER);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        countLabel.setForeground(Color.GRAY);
        indicatorsPanel.add(countLabel);
      }

      dayPanel.add(indicatorsPanel, BorderLayout.CENTER);
    }

    // Make the day panel clickable to show event details
    dayPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    dayPanel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        showEventsForDate(date);
      }

      @Override
      public void mouseEntered(MouseEvent e) {
        if (!date.equals(LocalDate.now())) {
          dayPanel.setBackground(new Color(240, 240, 240));
          for (Component comp : dayPanel.getComponents()) {
            if (comp instanceof JPanel) {
              comp.setBackground(new Color(240, 240, 240));
            }
          }
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (!date.equals(LocalDate.now())) {
          dayPanel.setBackground(Color.WHITE);
          for (Component comp : dayPanel.getComponents()) {
            if (comp instanceof JPanel) {
              comp.setBackground(Color.WHITE);
            }
          }
        } else {
          for (Component comp : dayPanel.getComponents()) {
            if (comp instanceof JPanel) {
              comp.setBackground(new Color(230, 230, 250));
            }
          }
        }
      }
    });

    return dayPanel;
  }

  /**
   * Shows a dialog with events for the selected date
   */
  private void showEventsForDate(LocalDate date) {
    // Get events for the selected date from our cached data
    List<Map<String, Object>> events = eventsByDate.getOrDefault(date, new ArrayList<>());

    if (events.isEmpty()) {
        int choice = JOptionPane.showOptionDialog(
            this,
            String.format("No events scheduled for %s.\n\nWould you like to add an event for this date?", 
                date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))),
            "No Events",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            new String[] { "Add Event", "Cancel" },
            "Cancel");

        if (choice == 0) { // Add Event was selected
            showAddEventDialog(date);
        }
        return;
    }

    StringBuilder eventDetails = new StringBuilder("Events for " + date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + ":\n\n");
    for (Map<String, Object> event : events) {
        eventDetails.append("- ").append(event.get("event_name"));
        if (event.containsKey("event_time")) {
            eventDetails.append(" at ").append(event.get("event_time"));
        }
        eventDetails.append("\n");
    }

    JOptionPane.showMessageDialog(
        this,
        eventDetails.toString(),
        "Scheduled Events",
        JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Shows dialog to add a new event
   */
  private void showAddEventDialog(LocalDate date) {
    EventAddDialog dialog = new EventAddDialog(
        (Frame) SwingUtilities.getWindowAncestor(this),
        date);
    dialog.setVisible(true);

    // If an event was added, refresh the calendar
    if (dialog.isConfirmed()) {
      loadEventsForCurrentMonth();
      updateCalendar();
      statusLabel.setText("Event added successfully. Calendar updated.");
    }
  }

  /**
   * Creates a formatted panel to display a single event
   */
  private JPanel createEventPanel(Map<String, Object> event) {
    JPanel eventPanel = new JPanel();
    eventPanel.setLayout(new BorderLayout());
    eventPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder("VIP".equalsIgnoreCase((String) event.get("category")) ? vipColor : accentColor,
            1),
        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    eventPanel.setBackground(Color.WHITE);
    eventPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
    eventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Event title
    String eventTitle;
    if (event.containsKey("team_a") && event.containsKey("team_b")) {
      eventTitle = event.get("team_a") + " vs " + event.get("team_b");
    } else {
      eventTitle = (String) event.get("event_name");
    }

    JLabel titleLabel = new JLabel(eventTitle);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

    // Event time (extract from date if available)
    String eventDate = (String) event.get("event_date");
    String eventTime = "All day";

    if (eventDate.length() > 10) { // Has time component
      eventTime = eventDate.substring(11); // Get time part
    }

    JLabel timeLabel = new JLabel(eventTime);
    timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

    // Event category badge
    JLabel categoryLabel = new JLabel((String) event.get("category"));
    categoryLabel.setOpaque(true);
    categoryLabel.setBackground("VIP".equalsIgnoreCase((String) event.get("category")) ? vipColor : accentColor);
    categoryLabel.setForeground(Color.WHITE);
    categoryLabel.setFont(new Font("Arial", Font.BOLD, 11));
    categoryLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

    // Event description
    String description = (String) event.get("event_description");
    if (description == null || description.isEmpty()) {
      description = "No description available";
    }

    JTextArea descriptionArea = new JTextArea(description);
    descriptionArea.setWrapStyleWord(true);
    descriptionArea.setLineWrap(true);
    descriptionArea.setEditable(false);
    descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
    descriptionArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    descriptionArea.setBackground(Color.WHITE);

    // Assemble panel
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(Color.WHITE);

    JPanel titleTimePanel = new JPanel();
    titleTimePanel.setLayout(new BoxLayout(titleTimePanel, BoxLayout.Y_AXIS));
    titleTimePanel.setBackground(Color.WHITE);
    titleTimePanel.add(titleLabel);
    titleTimePanel.add(Box.createVerticalStrut(5));
    titleTimePanel.add(timeLabel);

    headerPanel.add(titleTimePanel, BorderLayout.WEST);
    headerPanel.add(categoryLabel, BorderLayout.EAST);

    eventPanel.add(headerPanel, BorderLayout.NORTH);
    eventPanel.add(descriptionArea, BorderLayout.CENTER);

    return eventPanel;
  }

  /**
   * Creates a styled button with hover effects
   */
  private JButton createStyledButton(String text, Color foregroundColor) {
    RoundedButton button = new RoundedButton(text, 25);
    button.setBackground(primaryColor);
    button.setFont(new Font("Arial", Font.BOLD, 14));
    button.setForeground(foregroundColor);
    button.setPreferredSize(new Dimension(85, 30));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }
}