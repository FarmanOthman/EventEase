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

/**
 * TODO: Application Architecture Overview
 * 1. Project Structure:
 * - src/main/java/
 * ├── ui/ # UI components and routing
 * ├── models/ # Data models and business logic
 * ├── services/ # Business services and API integration
 * ├── utils/ # Utility classes and helpers
 * └── database/ # Database connection and queries
 * 
 * 2. Design Patterns to Implement:
 * - MVC Pattern: Separate views (ui/), models (models/), and controllers
 * (services/)
 * - Observer Pattern: For real-time updates (notifications, data changes)
 * - Singleton: For database connection and session management
 * - Factory: For creating different types of views/components
 * 
 * 3. Future Enhancements:
 * - Implement dependency injection for better testing
 * - Add logging framework for debugging
 * - Create configuration management system
 * - Implement caching for better performance
 */
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

    // TODO: View Management
    // 1. Implement view lifecycle management (creation, destruction, state
    // preservation)
    // 2. Add loading indicators for async operations
    // 3. Implement proper error handling and user feedback
    // 4. Add transition animations between views
    cardPanel.add(new LoginView(), "LoginView");
    cardPanel.add(new AdminDashboard(), "AdminDashboard");
    cardPanel.add(new EventView(), "EventView");
    cardPanel.add(new CustomerPage(), "CustomerPage");
    cardPanel.add(new ReportsView(), "ReportsView");
    cardPanel.add(new BookingView(), "BookingView");
    cardPanel.add(new CalendarView(), "CalendarView");
    cardPanel.add(new ManagerDashboard(), "UserManagementView");
    cardPanel.add(new DataPersistenceView(), "DataPersistenceView");
    cardPanel.add(new NotificationView(), "NotificationView");
    cardPanel.add(new UpcomingEvent(), "UpcomingEvent");
    cardPanel.add(new AdminManagementView(), "AdminManagementView");


    add(cardPanel);

    // TODO: Router Implementation
    // 1. Add navigation history management
    // 2. Implement route guards for authentication
    // 3. Add route parameters support
    // 4. Implement deep linking capability
    Router.setMainFrame(this);
  }

  public void showPage(String pageName) {
    // TODO: View Transition Logic
    // 1. Add pre-transition validation
    // 2. Implement data preservation between views
    // 3. Add loading states
    // 4. Handle transition errors
    cardLayout.show(cardPanel, pageName);
  }

  public static void main(String[] args) {
    // TODO: Application Bootstrap
    // 1. Initialize configuration
    // 2. Set up logging
    // 3. Initialize database connection
    // 4. Set up error handlers
    SwingUtilities.invokeLater(() -> {
      MainFrame mainFrame = new MainFrame();
      mainFrame.setVisible(true);
    });
  }
}
