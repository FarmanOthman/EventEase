package server;

import ui.MainFrame;
import javax.swing.SwingUtilities;;

public class Server {
    public static void main(String[] args) {
        SalesAnalysis salesAnalysis = new SalesAnalysis();
        
        System.out.println(salesAnalysis.getSalesReportData());

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
