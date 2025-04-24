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
import ui.Refreshable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sidebar extends JPanel implements Refreshable {

    @Override
    public void refresh() {
        updateSidebar();
    }

    public void updateSidebar() {
        this.removeAll(); // Remove old components
        initSidebarMenu(); // Rebuild the menu
        this.revalidate(); // Refresh UI
        this.repaint(); // Redraw UI
    }

    public Sidebar() {
        // Set up the layout for the sidebar
        initSidebarLayout();

        // Set sidebar properties like background color and dimensions
        customizeSidebarAppearance();

        // Initialize the sidebar menu with current dashboard choice
        initSidebarMenu();
    }

    // Method to initialize sidebar layout
    private void initSidebarLayout() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setPreferredSize(new Dimension(180, 500));
        this.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
    }

    // Method to customize sidebar appearance
    private void customizeSidebarAppearance() {
        this.setBackground(new Color(64, 133, 219)); // Set sidebar background to blue
    }

    // Method to initialize sidebar menu items and routing
    private void initSidebarMenu() {
        // Create menu items list for all users
        List<String[]> menuItemsList = new ArrayList<>(Arrays.asList(
                new String[] { "Dashboard", "Dashboard" },
                new String[] { "Manage Ticket", "BookingView" },
                new String[] { "Sales Reports", "ReportsView" },
                new String[] { "Calendar", "CalendarView" },
                new String[] { "User Management", "UserManagementView" })); // Show for all roles

        // Add Logout for all users
        menuItemsList.add(new String[] { "Logout", "LoginView" });

        // Convert list to array
        String[][] menuItems = menuItemsList.toArray(new String[0][]);

        // Loop through the menu items and create labels for them
        for (String[] menuItem : menuItems) {
            String menuText = menuItem[0];
            String routeName = menuItem[1];

            // Create a new label for each menu item
            JLabel menuLabel = createMenuLabel(menuText, routeName);

            // Add spacing before "Logout" menu item
            if (menuText.equals("Logout")) {
                this.add(Box.createVerticalStrut(40));
            }

            // Add the label to the sidebar
            this.add(menuLabel);

            // Add small spacing between menu items (except after Logout)
            if (!menuText.equals("Logout")) {
                this.add(Box.createVerticalStrut(5));
            }
        }
    }

    // Method to create a label for a menu item with proper formatting and mouse
    // listener
    private JLabel createMenuLabel(String menuText, String routeName) {
        JLabel menuLabel = new JLabel(menuText);
        menuLabel.setForeground(Color.WHITE);
        menuLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add mouse listener for routing
        menuLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Router.showPage(routeName); // Navigate to the corresponding route
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                menuLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); // Change cursor on hover
            }
        });

        return menuLabel;
    }
}