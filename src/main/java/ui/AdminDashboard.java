package ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard {
  public static void main(String[] args) {
    // Create main frame
    JFrame frame = new JFrame("Dashboard");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 600);
    frame.setLocationRelativeTo(null);

    // Create main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    frame.add(mainPanel);

    // Create top header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(70, 130, 200));
    headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 50));
    JLabel titleLabel = new JLabel("Dashboard", SwingConstants.CENTER);
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
    headerPanel.add(titleLabel);
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Create sidebar panel (without a border)
    JPanel sidebarPanel = new JPanel();
    sidebarPanel.setLayout(new GridLayout(5, 1, 15, 15)); // More spacing
    sidebarPanel.setBackground(new Color(70, 130, 200));
    sidebarPanel.setPreferredSize(new Dimension(200, frame.getHeight()));
    sidebarPanel.setBorder(BorderFactory.createEmptyBorder()); // ✅ Removed border around sidebar

    String[] menuItems = { "Manage Ticket", "Sales Reports", "Calendar", "User Management", "Logout" };
    for (String item : menuItems) {
      JButton button = new JButton(item);
      button.setForeground(Color.WHITE);
      button.setBackground(new Color(70, 130, 200));
      button.setFont(new Font("Arial", Font.BOLD, 16));
      button.setFocusPainted(false);
      button.setBorderPainted(false);
      sidebarPanel.add(button);
    }
    mainPanel.add(sidebarPanel, BorderLayout.WEST);

    // Create main content panel with better spacing
    JPanel contentPanel = new JPanel(new GridLayout(3, 3, 30, 30)); // Increased spacing
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

    String[] cardItems = { "Manage Events", "Manage Ticket", "Sales Reports", "Calendar", "Notification",
        "Data Persistence Import/Export", "" };
    for (String item : cardItems) {
      if (!item.isEmpty()) {
        JButton card = new JButton(item);
        card.setFont(new Font("Arial", Font.PLAIN, 14));
        card.setFocusPainted(false);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // ✅ Kept a subtle border for cards
        contentPanel.add(card);
      } else {
        contentPanel.add(new JLabel("")); // Empty space
      }
    }
    mainPanel.add(contentPanel, BorderLayout.CENTER);

  }
}
