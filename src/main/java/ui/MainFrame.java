package ui;

import javax.swing.*;
import java.awt.*;
import ui.pages.LoginView;
import ui.pages.AdminDashboard;
import ui.pages.EventView;
import ui.pages.ReportsView;
import ui.pages.BookingView;

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

    add(cardPanel);
  }

  public void showPage(String pageName) {
    cardLayout.show(cardPanel, pageName);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame();
      Router.setMainFrame(mainFrame);
      mainFrame.setVisible(true);
    });
  }
}
