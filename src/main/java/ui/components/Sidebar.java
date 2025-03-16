package ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ui.LoginView;

public class Sidebar extends JPanel {

    // private JFrame parentFrame;

    public Sidebar(JFrame parentFrame) {
        // this.parentFrame = parentFrame;

        // Set layout to vertical BoxLayout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Set blue background color
        this.setBackground(new Color(64, 133, 219));

        // Set preferred size for the sidebar
        this.setPreferredSize(new Dimension(180, 500));

        // Add padding around the sidebar
        this.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

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
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            menuLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Add logout functionality
            if (menuItem.equals("Logout")) {
                this.add(Box.createVerticalStrut(40));

                menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        int confirm = JOptionPane.showConfirmDialog(
                            Sidebar.this,
                            "Are you sure you want to logout?",
                            "Logout Confirmation",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            parentFrame.dispose(); // Close the current frame
                            new LoginView().show(); // Show the login screen again
                        }
                    }

                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        menuLabel.setForeground(Color.YELLOW);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        menuLabel.setForeground(Color.WHITE);
                    }
                });
            }

            this.add(menuLabel);

            // Add small spacing between menu items (except after Logout)
            if (!menuItem.equals("Logout")) {
                this.add(Box.createVerticalStrut(5));
            }
        }
    }
}
