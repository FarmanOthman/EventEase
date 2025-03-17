package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Notification System Architecture
 * 1. Create the following structure:
 * services/
 * ├── notification/
 * │ ├── NotificationService.java # Core notification handling
 * │ ├── NotificationManager.java # Notification lifecycle management
 * │ ├── NotificationRepository.java # Database operations
 * │ └── listeners/ # Event listeners
 * └── websocket/
 * └── WebSocketService.java # Real-time updates
 *
 * 2. Implement Observer pattern for real-time updates:
 * - Create NotificationObserver interface
 * - Register views as observers
 * - Broadcast notifications to all observers
 *
 * 3. Add notification persistence:
 * - Store notifications in database
 * - Implement read/unread status
 * - Add notification history
 */
public class NotificationView extends JPanel {
  private JPanel mainPanel;
  private JPanel notificationsPanel;
  private List<NotificationItem> notifications;

  private static class NotificationItem {
    String message;
    NotificationType type;

    NotificationItem(String message, NotificationType type) {
      this.message = message;
      this.type = type;
    }
  }

  private enum NotificationType {
    // TODO: Notification Types Implementation
    // 1. Add more notification types:
    // - SYSTEM_UPDATE
    // - ERROR
    // - WARNING
    // - INFO
    // - SUCCESS
    //
    // 2. For each type, define:
    // - Priority level
    // - Auto-dismiss timeout
    // - Sound alert
    // - Icon
    BOOKING_CONFIRMED(new Color(220, 255, 220), new Color(40, 167, 69)),
    TICKET_SOLD_OUT(new Color(255, 220, 220), new Color(220, 53, 69)),
    EVENT_UPCOMING(new Color(255, 243, 205), new Color(255, 193, 7));

    final Color backgroundColor;
    final Color borderColor;

    NotificationType(Color backgroundColor, Color borderColor) {
      this.backgroundColor = backgroundColor;
      this.borderColor = borderColor;
    }
  }

  public NotificationView() {
    setLayout(new BorderLayout());

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // TODO: Notification Initialization
    // 1. Load notifications from database
    // 2. Set up WebSocket connection for real-time updates
    // 3. Initialize notification sound system
    // 4. Set up notification preferences
    initializeNotifications();

    // Create main panel
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);
  }

  private void initializeNotifications() {
    // TODO: Notification Data Management
    // 1. Create NotificationRepository class:
    // - Add CRUD operations
    // - Implement pagination
    // - Add sorting options
    // - Support filtering
    //
    // 2. Add notification features:
    // - Mark as read/unread
    // - Batch operations
    // - Search functionality
    // - Categories/tags
    notifications = new ArrayList<>();
    notifications
        .add(new NotificationItem("Your booking for Event A has been confirmed", NotificationType.BOOKING_CONFIRMED));
    notifications.add(new NotificationItem("VIP tickets for Event B are sold out", NotificationType.TICKET_SOLD_OUT));
    notifications.add(new NotificationItem("Event C is starting in 2 days", NotificationType.EVENT_UPCOMING));
  }

  private void createMainPanel() {
    // TODO: UI Enhancement
    // 1. Add notification grouping
    // 2. Implement infinite scroll
    // 3. Add filter options
    // 4. Create notification preferences panel
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create header panel with notification controls
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(600, 50));

    // TODO: Header Controls
    // 1. Add notification count badge
    // 2. Implement mark all as read
    // 3. Add filter dropdown
    // 4. Create settings button
    JLabel headerLabel = new JLabel("Notifications");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(new Color(64, 133, 219));
    titlePanel.add(headerLabel);
    headerPanel.add(titlePanel, BorderLayout.CENTER);

    // Create content panel with notification list
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    // Create notifications panel
    notificationsPanel = new JPanel();
    notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));
    notificationsPanel.setBackground(Color.WHITE);

    // Add notifications
    for (NotificationItem notification : notifications) {
      JPanel notificationPanel = createNotificationPanel(notification);
      notificationsPanel.add(notificationPanel);
      notificationsPanel.add(Box.createVerticalStrut(10));
    }

    // Add components to content panel
    contentPanel.add(notificationsPanel);

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  private JPanel createNotificationPanel(NotificationItem notification) {
    // TODO: Notification Panel Features
    // 1. Add notification actions:
    // - Mark as read/unread
    // - Snooze notification
    // - Add to favorites
    // - Show details
    //
    // 2. Enhance UI:
    // - Add icons
    // - Show timestamp
    // - Display priority level
    // - Add action buttons
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(notification.type.backgroundColor);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(notification.type.borderColor, 1),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    panel.setMaximumSize(new Dimension(800, 50));

    // Create message label
    JLabel messageLabel = new JLabel(notification.message);
    messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    messageLabel.setForeground(new Color(33, 37, 41));
    panel.add(messageLabel, BorderLayout.CENTER);

    // Create dismiss button
    JButton dismissButton = new JButton("×") {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(108, 117, 125));
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth("×")) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString("×", x, y);
        g2.dispose();
      }
    };
    dismissButton.setPreferredSize(new Dimension(24, 24));
    dismissButton.setBorderPainted(false);
    dismissButton.setContentAreaFilled(false);
    dismissButton.setFocusPainted(false);
    dismissButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

    // TODO: Notification Actions
    // 1. Implement proper notification dismissal:
    // - Update database
    // - Animate dismissal
    // - Support undo
    // - Batch dismissal
    dismissButton.addActionListener(e -> {
      notifications.remove(notification);
      notificationsPanel.remove(panel);
      notificationsPanel.remove(notificationsPanel.getComponent(notificationsPanel.getComponentCount() - 1));
      notificationsPanel.revalidate();
      notificationsPanel.repaint();
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    buttonPanel.setOpaque(false);
    buttonPanel.add(dismissButton);
    panel.add(buttonPanel, BorderLayout.EAST);

    return panel;
  }
}
