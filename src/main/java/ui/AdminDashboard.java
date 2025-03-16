package ui;
import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;

public class AdminDashboard {
    private JPanel mainPanel;

    public AdminDashboard() {
        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());

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
        mainPanel.add(new Sidebar(), BorderLayout.WEST);

        // Create main content panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 3, 30, 30));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] cardItems = { "Manage Events", "Manage Ticket", "Sales Reports", "Calendar", "Notification", "Data Persistence Import/Export", "" };
        for (String item : cardItems) {
            if (!item.isEmpty()) {
                JButton card = new JButton(item);
                card.setFont(new Font("Arial", Font.PLAIN, 14));
                card.setFocusPainted(false);
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                contentPanel.add(card);
            } else {
                contentPanel.add(new JLabel(""));
            }
        }
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // Method to get the panel
    public JPanel getPanel() {
        
        return mainPanel;
    }
}
