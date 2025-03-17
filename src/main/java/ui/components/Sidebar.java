package ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import ui.Router;

public class Sidebar extends JPanel {

    public Sidebar() {
        // Set layout to vertical BoxLayout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Set blue background color
        this.setBackground(new Color(64, 133, 219));

        // Set preferred size for the sidebar
        this.setPreferredSize(new Dimension(180, 500));

        // Add padding around the sidebar
        this.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Menu items with their corresponding route names
        String[][] menuItems = new String[][] {
                { "Dashboard", "AdminDashboard" },
                { "Manage Ticket", "BookingView" },
                { "Sales Reports", "ReportsView" },
                { "Calendar", "CalendarView" },
                { "Notification", "NotificationView" },
                { "Data Persistence", "DataPersistenceView" },
                { "User Management", "UserManagementView" },
                { "Logout", "LoginView" }
        };

        // Add menu items to the sidebar
        for (String[] item : menuItems) {
            String menuText = item[0];
            String routeName = item[1];

            // Create label for menu item
            JLabel menuLabel = new JLabel(menuText);
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            menuLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Add hover effect
            menuLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                    menuLabel.setOpaque(true);
                    menuLabel.setBackground(new Color(64, 155, 219));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    menuLabel.setOpaque(false);
                    menuLabel.setBackground(null);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    Router.showPage(routeName);
                }
            });

            // Add extra space before Logout
            if (menuText.equals("Logout")) {
                this.add(Box.createVerticalStrut(40));
            }

            // Add the menu item
            this.add(menuLabel);

            // Add small spacing between menu items (except after Logout)
            if (!menuText.equals("Logout")) {
                this.add(Box.createVerticalStrut(5));
            }
        }
    }
}
