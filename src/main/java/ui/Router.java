package ui;

import javax.swing.JFrame;

import ui.pages.LoginView;

public class Router {
  private static MainFrame mainFrame;

  public static void setMainFrame(MainFrame frame) {
    mainFrame = frame;
  }

  public static void showPage(String pageName) {
    if (mainFrame != null) {
      mainFrame.showPage(pageName);
    }
  }
public static void goTo(String viewName) {
    if ("LoginView".equals(viewName)) {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.add(new LoginView()); // Replace with your LoginView class
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
}