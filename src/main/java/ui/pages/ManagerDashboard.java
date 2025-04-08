package ui.pages;

import javax.swing.*;
import java.awt.*;
import ui.Router;
import ui.components.Sidebar;

/**
 * TODO: Dashboard System Design
 * 1. Define the following structure:
 * services/
 * ├── dashboard/
 * │ ├── DashboardService.java # Core dashboard logic
 * │ ├── AnalyticsService.java # Provides real-time analytics
 * │ ├── MetricsService.java # Tracks key performance indicators (KPIs)
 * │ └── NotificationService.java # Manages alert notifications
 * └── monitoring/
 * ├── SystemMonitor.java # Monitors system health
 * └── PerformanceTracker.java # Tracks system performance metrics
 *
 * 2. Dashboard Features:
 * - Real-time display of analytics
 * - System health monitoring
 * - User activity tracking
 * - Performance metric tracking
 *
 * 3. Integration Points:
 * - Analytics engine for data analysis
 * - Monitoring system for tracking system status
 * - Alert system for notifications
 * - Reporting system for generating reports
 */

public class ManagerDashboard extends JPanel {
  public ManagerDashboard() {
    setLayout(new BorderLayout());

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Create main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);

    // Create top header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(70, 130, 200));
    headerPanel.setPreferredSize(new Dimension(800, 50));
    JLabel titleLabel = new JLabel("Manager Dashboard", SwingConstants.CENTER);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerPanel.add(titleLabel);
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Create main content panel with better spacing
    JPanel contentPanel = new JPanel(new GridLayout(3, 3, 30, 30)); // Increased spacing
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

    String[] cardItems = { "Manage Events", "Manage Ticket", "Sales Reports", "Calendar", "Notification",
        "Data Persistence Import/Export", "Upcoming Events", "User Manager","" };
    for (String item : cardItems) {
      if (!item.isEmpty()) {
        JButton card = new JButton(item);
        card.setFont(new Font("Arial", Font.PLAIN, 14));
        card.setFocusPainted(false);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // Subtle border for cards

        // Add action listener for the Manage Events button
        if (item.equals("Manage Events")) {
          card.addActionListener(e -> Router.showPage("EventView"));
        }
        // Add action listener for the Sales Reports button
        else if (item.equals("Sales Reports")) {
          card.addActionListener(e -> Router.showPage("ReportsView"));
        }
        // Add action listener for the Manage Ticket button
        else if (item.equals("Manage Ticket")) {
          card.addActionListener(e -> Router.showPage("BookingView"));
        }
        // Add action listener for the Calendar button
        else if (item.equals("Calendar")) {
          card.addActionListener(e -> Router.showPage("CalendarView"));
        }
        // Add action listener for the Data Persistence button
        else if (item.equals("Data Persistence Import/Export")) {
          card.addActionListener(e -> Router.showPage("DataPersistenceView"));
        }
        // Add action listener for the Notification button
        else if (item.equals("Notification")) {
          card.addActionListener(e -> Router.showPage("NotificationView"));
        }
        // Add action listener for the Upcoming Events button
        else if (item.equals("Upcoming Events")) {
          card.addActionListener(e -> Router.showPage("UpcomingEvent"));
        }
        else if (item.equals("User Manager")) {
          card.addActionListener(e -> Router.showPage("AdminManagementView"));
      }
      

        contentPanel.add(card);
      } else {
        contentPanel.add(new JLabel("")); // Empty space
      }
    }
    mainPanel.add(contentPanel, BorderLayout.CENTER);
  }
}
