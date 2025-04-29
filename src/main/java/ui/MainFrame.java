package ui;

import javax.swing.*;
import java.awt.*;

// Import custom views (each is a JPanel for a specific screen)
import ui.pages.LoginView;
import ui.pages.Dashboard;
import ui.pages.EventView;
import ui.pages.ReportsView;
import ui.pages.BookingView;
import ui.pages.CalendarView;
import ui.pages.UserManagementView;
import ui.pages.DataPersistenceView;
import ui.pages.NotificationView;
import ui.pages.UpcomingEvent;

public class MainFrame extends JFrame {
  // Main content panel that holds all views (cards)
  private JPanel cardPanel;

  // Layout manager to switch between cards
  private CardLayout cardLayout;

  // Scroll pane to enable scrolling if content overflows
  private JScrollPane scrollPane;

  public MainFrame() {
    // Set window properties
    setTitle("EventEase");
    setSize(1200, 800);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null); // center the window on screen
    setMinimumSize(new Dimension(1200, 800));

    // Initialize layout and panel
    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    // Add views to card panel with unique identifiers
    cardPanel.add(new LoginView(), "LoginView");
    cardPanel.add(new Dashboard(), "Dashboard");
    cardPanel.add(new EventView(), "EventView");
    cardPanel.add(new ReportsView(), "ReportsView");
    cardPanel.add(new BookingView(), "BookingView");
    cardPanel.add(new CalendarView(), "CalendarView");
    cardPanel.add(new DataPersistenceView(), "DataPersistenceView");
    cardPanel.add(new NotificationView(), "NotificationView");
    cardPanel.add(new UpcomingEvent(), "UpcomingEvent");
    cardPanel.add(new UserManagementView(), "UserManagementView");

    // Wrap the card panel in a scroll pane
    scrollPane = new JScrollPane(cardPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setBorder(null); // remove default border for cleaner look
    cardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // set hand cursor

    // Add scroll pane to the main frame
    add(scrollPane);

    // Register this frame with the Router for navigation control
    Router.setMainFrame(this);
  }

  // Method to switch to a different page by name
  public void showPage(String pageName) {
    cardLayout.show(cardPanel, pageName); // switch to desired view

    cardPanel.revalidate(); // update layout if needed
    cardPanel.repaint();    // repaint panel to reflect changes

    // Scroll to top of view after switching
    SwingUtilities.invokeLater(() -> {
      scrollPane.getVerticalScrollBar().setValue(0);
    });
  }

  // Getter to access the card panel (if needed externally)
  public JPanel getCardPanel() {
    return cardPanel;
  }

  // Main entry point of the application
  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame();
      mainFrame.setVisible(true); // display the main window
    });
  }
}
