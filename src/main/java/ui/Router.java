package ui;

public class Router {
    
    private static MainFrame mainFrame;

    public static void setMainFrame(MainFrame frame) {
        // Set the reference for the MainFrame
        mainFrame = frame;
    }

    public static void showPage(String pageName) {
        // Show the desired page if the main frame is set
        if (mainFrame != null) {
            mainFrame.showPage(pageName);
        }
    }
}