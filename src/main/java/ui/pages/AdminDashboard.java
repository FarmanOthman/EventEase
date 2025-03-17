package ui.pages;

import javax.swing.*;
import java.awt.*;
import ui.Router;

public class AdminDashboard extends JPanel {
  public AdminDashboard() {
    setLayout(new BorderLayout());

    // Create main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    add(mainPanel);

    // Create top header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(70, 130, 200));
    headerPanel.setPreferredSize(new Dimension(1000, 50));
    JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerPanel.add(titleLabel);
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Create sidebar panel (without a border)
    JPanel sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new GridLayout(5, 1, 15, 15)); // More spacing
    sidebarPanel.setBackground(new Color(70, 130, 200));
    sidebarPanel.setPreferredSize(new Dimension(200, 600));
    sidebarPanel.setBorder(BorderFactory.createEmptyBorder()); // ✅ No border

    String[] menuItems = { "Manage Ticket", "Sales Reports", "Calendar", "User Management", "Logout" };
    for (String item : menuItems) {
      JButton button = new JButton(item);
      button.setForeground(Color.WHITE);
      button.setBackground(new Color(70, 130, 200));
      button.setFont(new Font("Arial", Font.BOLD, 16));
      button.setFocusPainted(false);
      button.setBorderPainted(false);

      // Add action listener for Sales Reports button
      if (item.equals("Sales Reports")) {
        button.addActionListener(e -> Router.showPage("ReportsView"));
      }
      // Add action listener for Manage Ticket button
      else if (item.equals("Manage Ticket")) {
        button.addActionListener(e -> Router.showPage("BookingView"));
      }

      sidebarPanel.add(button);
    }
    mainPanel.add(sidebarPanel, BorderLayout.WEST);

    // Create main content panel with better spacing
    JPanel contentPanel = new JPanel(new GridLayout(3, 3, 30, 30)); // Increased spacing
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding

    String[] cardItems = { "Manage Events", "Manage Ticket", "Sales Reports", "Calendar", "Notification",
        "Data Persistence Import/Export", "" };
    for (String item : cardItems) {
      if (!item.isEmpty()) {
        JButton card = new JButton(item);
        card.setFont(new Font("Arial", Font.PLAIN, 14));
        card.setFocusPainted(false);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // ✅ Subtle border for cards

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

        contentPanel.add(card);
      } else {
        contentPanel.add(new JLabel("")); // Empty space
      }
    }
    mainPanel.add(contentPanel, BorderLayout.CENTER);
  }
}
