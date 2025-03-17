package ui;

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
}