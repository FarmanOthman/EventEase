package ui;

import javax.swing.*;
import java.awt.*;
import ui.pages.LoginView;
import ui.pages.AdminDashboard;
import ui.pages.EventView;
import ui.pages.ReportsView;
import ui.pages.BookingView;
import ui.pages.CalendarView;
import ui.pages.UserManagementView;

public class MainFrame extends JFrame {
  private JPanel cardPanel;
  private CardLayout cardLayout;

  public MainFrame() {
    setTitle("Multi-Page Application");
    setSize(1000, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    // Add pages to the card panel
    cardPanel.add(new LoginView(), "LoginView");
    cardPanel.add(new AdminDashboard(), "AdminDashboard");
    cardPanel.add(new EventView(), "EventView");
    cardPanel.add(new ReportsView(), "ReportsView");
    cardPanel.add(new BookingView(), "BookingView");
    cardPanel.add(new CalendarView(), "CalendarView");
    cardPanel.add(new UserManagementView(), "UserManagementView");

    add(cardPanel);

    // Set up the Router with this frame
    Router.setMainFrame(this);
  }

  public void showPage(String pageName) {
    cardLayout.show(cardPanel, pageName);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame();
      mainFrame.setVisible(true);
    });
  }
}
