package ui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import server.AuthenticationService;
import server.AuthenticationService.UserRole;

public class Router {
  private static MainFrame mainFrame;
  private static Map<String, Object> sharedData = new HashMap<>();

  public static void setMainFrame(MainFrame frame) {
    mainFrame = frame;
  }

  public static void showPage(String pageName) {
    if (pageName == null || mainFrame == null) {
      return;
    }

    // Restrict access to UserManagementView
    if (pageName.equals("UserManagementView")) {
      UserRole currentRole = AuthenticationService.getCurrentUserRole();

      // Only allow MANAGER to access UserManagementView
      if (currentRole == UserRole.ADMIN) {
        JOptionPane.showMessageDialog(
            mainFrame,
            "Access Denied: User Management is restricted to Manager roles only. Admins cannot access this feature.",
            "Access Restricted",
            JOptionPane.WARNING_MESSAGE);
        return;
      } else if (currentRole != UserRole.MANAGER) {
        JOptionPane.showMessageDialog(
            mainFrame,
            "Access Denied: You must be logged in as a Manager to access User Management",
            "Access Restricted",
            JOptionPane.WARNING_MESSAGE);
        return;
      }
    }

    // First try to refresh the page if it's already loaded
    tryRefreshPage(pageName);

    // Then show the page
    mainFrame.showPage(pageName);
  }

  // Method to try refreshing a page if it supports the Refreshable interface
  private static void tryRefreshPage(String pageName) {
    if (mainFrame != null) {
      JPanel cardPanel = mainFrame.getCardPanel();

      // Find the component with the given name
      for (Component component : cardPanel.getComponents()) {
        if (component instanceof JPanel) {
          // Check if this is the target panel
          String componentName = ((JPanel) component).getName();
          if (componentName == null) {
            // If no name is set, use the class name
            componentName = component.getClass().getSimpleName();
          }

          // If this is our target panel, refresh it
          if (componentName.equals(pageName)) {
            // Force a repaint on the target panel
            component.invalidate();
            component.revalidate();
            component.repaint();

            // Use reflection to call refresh method if available
            try {
              if (component.getClass().getMethod("refresh") != null) {
                component.getClass().getMethod("refresh").invoke(component);
              }
            } catch (Exception e) {
              // Method doesn't exist or other exception, just continue
              // Silent catch is intentional - not all panels will have refresh method
            }
          }
        }
      }
    }
  }

  // Method to store data for passing between pages
  public static void putData(String key, Object value) {
    sharedData.put(key, value);
  }

  // Method to retrieve data passed between pages
  public static Object getData(String key) {
    return sharedData.get(key);
  }

  // Method to clear specific data
  public static void clearData(String key) {
    sharedData.remove(key);
  }

  // Method to clear all shared data
  public static void clearAllData() {
    sharedData.clear();
  }
}