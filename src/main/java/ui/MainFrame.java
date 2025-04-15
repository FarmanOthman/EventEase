package ui;

import javax.swing.*;
import java.awt.*;
import ui.pages.LoginView;
import ui.pages.ManagerDashboard;
import ui.pages.AdminDashboard;
import ui.pages.EventView;
import ui.pages.ReportsView;
import ui.pages.BookingView;
import ui.pages.CalendarView;
import ui.pages.AdminManagementView;
import ui.pages.DataPersistenceView;
import ui.pages.NotificationView;
import ui.pages.CustomerPage;
import ui.pages.UpcomingEvent;

public class MainFrame extends JFrame {
  private JPanel cardPanel;
  private CardLayout cardLayout;

  public MainFrame() {
    setTitle("EventEase");
    setSize(1000, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    cardPanel = new JPanel(cardLayout);

    cardPanel.add(new LoginView(), "LoginView");
    cardPanel.add(new AdminDashboard(), "AdminDashboard");
    cardPanel.add(new EventView(), "EventView");
    cardPanel.add(new CustomerPage(), "CustomerPage");
    cardPanel.add(new ReportsView(), "ReportsView");
    cardPanel.add(new BookingView(), "BookingView");
    cardPanel.add(new CalendarView(), "CalendarView");
    cardPanel.add(new ManagerDashboard(), "ManagerDashboard");
    cardPanel.add(new DataPersistenceView(), "DataPersistenceView");
    cardPanel.add(new NotificationView(), "NotificationView");
    cardPanel.add(new UpcomingEvent(), "UpcomingEvent");
    cardPanel.add(new AdminManagementView(), "AdminManagementView");

    add(cardPanel);

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
