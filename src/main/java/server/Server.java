package server;

import ui.MainFrame;
import javax.swing.SwingUtilities;;

public class Server {
    public static void main(String[] args) {
        // SalesAnalysis salesAnalysis = new SalesAnalysis();
        

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
