package ui.pages;

import ui.components.Sidebar;
import ui.components.RoundedButton;
import services.NotificationService;
import services.NotificationService.UINotification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class NotificationView extends JPanel {
  private JPanel mainPanel;
  private JPanel notificationsPanel;
  private JLabel notificationCountLabel;
  private RoundedButton markAllReadButton;
  private JComboBox<String> filterComboBox;
  private NotificationService notificationService;
  private List<UINotification> notifications;

  public NotificationView() {
    setLayout(new BorderLayout());
    add(new Sidebar(), BorderLayout.WEST);

    // Initialize the notification service
    notificationService = NotificationService.getInstance();
    notificationService.registerView(this);

    // Load notifications from service
    loadNotifications();

    createMainPanel();
    add(mainPanel, BorderLayout.CENTER);
  }

  private void loadNotifications() {
    // Get notifications from service (null means no type filter, false means
    // include read notifications)
    notifications = notificationService.getNotifications(null, false);
  }

  /**
   * Refresh notifications from the service
   * Called by the NotificationService when new notifications arrive
   */
  public void refreshNotifications() {
    loadNotifications();
    renderNotifications();
  }

  private void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create header panel with notification controls
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(600, 60));

    // Title section
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
    titlePanel.setBackground(new Color(64, 133, 219));

    JLabel headerLabel = new JLabel("Notifications");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 20));

    // Badge for unread count
    notificationCountLabel = new JLabel(notificationService.getUnreadCount() + "");
    notificationCountLabel.setOpaque(true);
    notificationCountLabel.setBackground(Color.RED);
    notificationCountLabel.setForeground(Color.WHITE);
    notificationCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
    notificationCountLabel.setHorizontalAlignment(JLabel.CENTER);
    notificationCountLabel.setPreferredSize(new Dimension(20, 20));
    notificationCountLabel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

    titlePanel.add(headerLabel);
    titlePanel.add(notificationCountLabel);

    // Control panel for notification actions
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    controlPanel.setBackground(new Color(64, 133, 219));

    // Filter dropdown
    String[] filterOptions = { "All Notifications", "Unread", "Bookings", "Events" };
    filterComboBox = new JComboBox<>(filterOptions);
    filterComboBox.setPreferredSize(new Dimension(150, 30));
    filterComboBox.addActionListener(e -> applyFilter((String) filterComboBox.getSelectedItem()));

    // Mark all as read button
    markAllReadButton = new RoundedButton("Mark All as Read", 25);
    markAllReadButton.setBackground(new Color(64, 133, 219));
    markAllReadButton.setFont(new Font("Arial", Font.BOLD, 14));
    markAllReadButton.setForeground(Color.white);
    markAllReadButton.setPreferredSize(new Dimension(160, 40));
    markAllReadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    markAllReadButton.addActionListener(e -> markAllAsRead());

    controlPanel.add(filterComboBox);
    controlPanel.add(markAllReadButton);

    // Add a refresh button to the control panel
    RoundedButton refreshButton = new RoundedButton("Refresh", 25);
    refreshButton.setBackground(new Color(245, 245, 245));
    refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
    refreshButton.setForeground(new Color(64, 133, 219));
    refreshButton.setPreferredSize(new Dimension(120, 40));
    refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    refreshButton.addActionListener(e -> refreshNotifications());
    controlPanel.add(refreshButton);

    headerPanel.add(titlePanel, BorderLayout.WEST);
    headerPanel.add(controlPanel, BorderLayout.EAST);

    // Create content panel with notification list
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    // Create section for categorizing notifications
    JPanel todayPanel = new JPanel();
    todayPanel.setLayout(new BoxLayout(todayPanel, BoxLayout.Y_AXIS));
    todayPanel.setBackground(Color.WHITE);
    todayPanel.setAlignmentX(LEFT_ALIGNMENT);

    JLabel todayLabel = new JLabel("Today");
    todayLabel.setFont(new Font("Arial", Font.BOLD, 16));
    todayLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    todayLabel.setAlignmentX(LEFT_ALIGNMENT);

    // Create notifications panel
    notificationsPanel = new JPanel();
    notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
    notificationsPanel.setBackground(Color.WHITE);
    notificationsPanel.setAlignmentX(LEFT_ALIGNMENT);

    // Add notifications
    renderNotifications();

    // Add components to content panel
    todayPanel.add(todayLabel);
    todayPanel.add(notificationsPanel);
    contentPanel.add(todayPanel);

    // Add empty panel at the bottom for spacing
    contentPanel.add(Box.createVerticalGlue());

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  private void renderNotifications() {
    notificationsPanel.removeAll();

    // Add notifications
    for (UINotification notification : notifications) {
      JPanel notificationPanel = createNotificationPanel(notification);
      notificationsPanel.add(notificationPanel);
      notificationsPanel.add(Box.createVerticalStrut(10));
    }

    if (notifications.isEmpty()) {
      JLabel emptyLabel = new JLabel("No notifications");
      emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
      emptyLabel.setForeground(Color.GRAY);
      emptyLabel.setAlignmentX(CENTER_ALIGNMENT);
      notificationsPanel.add(emptyLabel);
    }

    notificationsPanel.revalidate();
    notificationsPanel.repaint();
    updateNotificationCount();
  }

  private JPanel createNotificationPanel(UINotification notification) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(notification.isRead() ? notification.getType().getBackgroundColorRead()
        : notification.getType().getBackgroundColor());
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 3, 0, 0, notification.getType().getBorderColor()),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    panel.setMaximumSize(new Dimension(10000, 80));

    // Main content panel
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setOpaque(false);

    // Create message label
    JLabel messageLabel = new JLabel(notification.getMessage());
    messageLabel.setFont(new Font("Arial", notification.isRead() ? Font.PLAIN : Font.BOLD, 14));
    messageLabel.setForeground(new Color(33, 37, 41));

    // Create timestamp label
    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - MMM dd, yyyy");
    JLabel timeLabel = new JLabel(dateFormat.format(notification.getTimestamp()));
    timeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
    timeLabel.setForeground(Color.GRAY);

    contentPanel.add(messageLabel, BorderLayout.NORTH);
    contentPanel.add(timeLabel, BorderLayout.SOUTH);

    // Action buttons panel
    JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    actionsPanel.setOpaque(false);

    // Mark as read/unread button
    RoundedButton readButton = new RoundedButton(notification.isRead() ? "Mark as Unread" : "Mark as Read", 25);
    readButton.setBackground(new Color(64, 133, 219));
    readButton.setFont(new Font("Arial", Font.BOLD, 14));
    readButton.setForeground(Color.white);
    readButton.setPreferredSize(new Dimension(130, 30));
    readButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    readButton.addActionListener(e -> {
      if (notification.isRead()) {
        notificationService.markAsUnread(notification.getId());
      } else {
        notificationService.markAsRead(notification.getId());
      }
      refreshNotifications();
    });

    // Dismiss button
    RoundedButton dismissButton = new RoundedButton("Dismiss", 25);
    dismissButton.setBackground(new Color(231, 76, 60)); // Red color
    dismissButton.setFont(new Font("Arial", Font.BOLD, 14));
    dismissButton.setForeground(Color.white);
    dismissButton.setPreferredSize(new Dimension(120, 30));
    dismissButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    dismissButton.addActionListener(e -> {
      notificationService.deleteNotification(notification.getId());
      refreshNotifications();
    });

    actionsPanel.add(readButton);
    actionsPanel.add(dismissButton);

    panel.add(contentPanel, BorderLayout.CENTER);
    panel.add(actionsPanel, BorderLayout.EAST);

    // Add hover effect and pointer cursor
    panel.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        panel.setBackground(notification.getType().getBackgroundColorHover());
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        panel.setBackground(notification.isRead() ? notification.getType().getBackgroundColorRead()
            : notification.getType().getBackgroundColor());
        panel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
      }
    });

    return panel;
  }

  private void applyFilter(String filter) {
    server.notification.NotificationType serverType = null;
    boolean onlyUnread = false;

    switch (filter) {
      case "Unread":
        onlyUnread = true;
        break;
      case "Bookings":
        serverType = server.notification.NotificationType.BOOKING_CONFIRMED;
        break;
      case "Events":
        serverType = server.notification.NotificationType.EVENT_UPCOMING;
        break;
    }

    notifications = notificationService.getNotifications(serverType, onlyUnread);
    renderNotifications();
  }

  private void markAllAsRead() {
    notificationService.markAllAsRead();
    refreshNotifications();
  }

  private void updateNotificationCount() {
    int unreadCount = notificationService.getUnreadCount();
    notificationCountLabel.setText(String.valueOf(unreadCount));
    notificationCountLabel.setVisible(unreadCount > 0);
  }
}
