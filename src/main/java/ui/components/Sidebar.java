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
<<<<<<< HEAD
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import ui.BookingView;
import ui.LoginView;

public class Sidebar extends JPanel {

    // private JFrame parentFrame;

    public Sidebar(JFrame parentFrame) {
        // this.parentFrame = parentFrame;

=======
import ui.Router;

public class Sidebar extends JPanel {

    public Sidebar() {
>>>>>>> GUI-implementation
        // Set layout to vertical BoxLayout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Set blue background color
        this.setBackground(new Color(64, 133, 219));

        // Set preferred size for the sidebar
        this.setPreferredSize(new Dimension(180, 500));

        // Add padding around the sidebar
        this.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

<<<<<<< HEAD
        // Menu items
        String[] menuItems = new String[]{
            "Manage Ticket", 
            "Sales Reports", 
            "Calender", 
            "User Management", 
            "Logout"
        };

        // Add menu items to the sidebar
        for (String menuItem : menuItems) {
            JLabel menuLabel = new JLabel(menuItem);
=======
        // Menu items with their corresponding route names
        String[][] menuItems = new String[][] {
                { "Dashboard", "AdminDashboard" },
                { "Manage Ticket", "BookingView" },
                { "Sales Reports", "ReportsView" },
                { "Calender", "CalendarView" },
                { "User Management", "UserManagementView" },
                { "Logout", "LoginView" }
        };

        // Add menu items to the sidebar
        for (int i = 0; i < menuItems.length; i++) {
            String menuText = menuItems[i][0];
            String routeName = menuItems[i][1];

            // Create label for menu item
            JLabel menuLabel = new JLabel(menuText);
>>>>>>> GUI-implementation
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            menuLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

<<<<<<< HEAD
            menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        menuLabel.setForeground(Color.WHITE);
                        menuLabel.setOpaque(true);
                        menuLabel.setBackground(new Color(64, 155, 219)); // Change to your desired color
                    }       

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        menuLabel.setForeground(Color.WHITE);
                        menuLabel.setBackground(null); // Reset to default
                    }

                });

            // Add logout functionality
            if (menuItem.equals("Logout")) {
                menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        parentFrame.dispose(); // Close the current frame
                        new LoginView().show(); // Show the login screen again
                    }
                });
            }

            if (menuItem.equals("Manage Ticket")) {
                menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        parentFrame.dispose(); // Close the current frame
                        new BookingView(); // Show the login screen again
                    }
                });
            }

=======
            // Add mouse listener for routing
            menuLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Router.showPage(routeName);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    menuLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                }
            });

            // Add extra space before Logout
            if (menuText.equals("Logout")) {
                this.add(Box.createVerticalStrut(40));
            }

            // Add the menu item
>>>>>>> GUI-implementation
            this.add(menuLabel);

            // Add small spacing between menu items (except after Logout)
            if (!menuText.equals("Logout")) {
                this.add(Box.createVerticalStrut(5));
            }
        }
    }
}
