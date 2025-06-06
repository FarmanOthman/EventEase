package ui;

import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import javax.swing.JOptionPane;
import server.AuthenticationServer;
import server.AuthenticationServer.UserRole;

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
      UserRole currentRole = AuthenticationServer.getCurrentUserRole();

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

    // First refresh the page if it implements Refreshable
    refreshPageIfNeeded(pageName);

    // Then show the page
    mainFrame.showPage(pageName);
  }

  // Method to refresh a page if it implements Refreshable
  private static void refreshPageIfNeeded(String pageName) {
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

          // If this is our target panel and it implements Refreshable, refresh it
          if (componentName.equals(pageName) && component instanceof Refreshable) {
            System.out.println("Refreshing page: " + pageName);
            ((Refreshable) component).refresh();
            break;
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