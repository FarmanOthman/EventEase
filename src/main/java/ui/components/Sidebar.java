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
        
        // Menu items
        String[] menuItems = new String[]{
            "Manage Ticket", 
            "Sales Reports", 
            "Calender", 
            "User Management", 
            "Logout"
        };
        
        // Add menu items to the sidebar
        for (int i = 0; i < menuItems.length; i++) {
            String menuItem = menuItems[i];
            
            // Create label for menu item
            JLabel menuLabel = new JLabel(menuItem);
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            menuLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
            menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Add extra space before Logout
            if (menuItem.equals("Logout")) {
                this.add(Box.createVerticalStrut(40));
            }
            
            // Add the menu item
            this.add(menuLabel);
            
            // Add small spacing between menu items (except after Logout)
            if (!menuItem.equals("Logout")) {
                this.add(Box.createVerticalStrut(5));
            }
        }
    }
}