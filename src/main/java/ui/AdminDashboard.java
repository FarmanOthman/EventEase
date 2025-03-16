package ui;

import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboard {
    private JPanel mainPanel;
    private JFrame frame;

    public AdminDashboard(JFrame frame) {
        this.frame = frame;

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

        // Create sidebar panel (Fixed: Pass `frame` instead of `mainPanel`)
        mainPanel.add(new Sidebar(frame), BorderLayout.WEST);

        // Create main content panel
        JPanel contentPanel = new JPanel(new GridLayout(3, 3, 30, 30));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Card items for the dashboard
        String[] cardItems = { "Manage Events", "Manage Ticket", "Sales Reports", "Calendar", "Notification", "Data Persistence Import/Export", "" };

        // Add buttons to the content panel
        for (String item : cardItems) {
            if (!item.isEmpty()) {
                JButton card = new JButton(item);
                card.setFont(new Font("Arial", Font.PLAIN, 14));
                card.setFocusPainted(false);
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

                // Add ActionListener for "Manage Events"
                if (item.equals("Manage Events")) {
                    card.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // Open the Event Management view when clicked
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        new EventView(); // This will show the EventView GUI
                                    } catch (Exception ex) {
                                        ex.printStackTrace(); // Log any exception that occurs
                                    }
                                }
                            });
                        }
                    });
                }

                contentPanel.add(card);
            } else {
                contentPanel.add(new JLabel(""));
            }
        }

        // Add the content panel to the main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    // Method to get the main panel of the AdminDashboard
    public JPanel getPanel() {
        return mainPanel;
    }
}
