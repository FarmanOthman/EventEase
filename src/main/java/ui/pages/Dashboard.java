package ui.pages;

import javax.swing.*;
import java.awt.*;
import ui.Router;
import ui.components.Sidebar;
import ui.components.RoundedButton;

public class Dashboard extends JPanel {
  public Dashboard() {
    setLayout(new BorderLayout());

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Create main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    add(mainPanel, BorderLayout.CENTER);

    // Create top header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(800, 50));
    JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerPanel.add(titleLabel);
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Create main content panel with better spacing
    JPanel contentPanel = new JPanel(new GridLayout(3, 3, 30, 30)); // Increased spacing
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

    String[] cardItems = { "Manage Events", "Manage Ticket", "Sales Reports", "Calendar", "Notification",
        "Data Persistence Import/Export", "Upcoming Events", "User Management" };

    for (String item : cardItems) {
      if (!item.isEmpty()) {
        RoundedButton card = new RoundedButton(item, 25);
        card.setBackground(new Color(248, 249, 250)); // Light gray background
        card.setFont(new Font("Arial", Font.BOLD, 14));
        card.setForeground(new Color(60, 60, 60)); // Dark gray text
        card.setPreferredSize(new Dimension(180, 40));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
        // Add action listener for the User Management button
        else if (item.equals("User Management")) {
          card.addActionListener(e -> Router.showPage("UserManagementView"));
        }

        contentPanel.add(card);
      } else {
        contentPanel.add(new JLabel("")); // Empty space
      }
    }

    mainPanel.add(contentPanel, BorderLayout.CENTER);
  }
}