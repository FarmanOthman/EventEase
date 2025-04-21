package ui.pages;

import javax.swing.*;
import java.awt.*;
import server.AuthenticationService;
import server.AuthenticationService.UserRole;
import services.UserService;
import services.NotificationService;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.Sidebar;
import ui.components.RoundedPasswordField;
import ui.Router;

public class LoginView extends JPanel {
  private UserService userService;
  private NotificationService notificationService;

  public LoginView() {
    setLayout(new BorderLayout());

    // Get service instances
    userService = UserService.getInstance();
    notificationService = NotificationService.getInstance();

    JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
    titleLabel.setForeground(new Color(50, 120, 200));
    add(titleLabel, BorderLayout.NORTH);

    JPanel loginBox = new JPanel();
    loginBox.setLayout(null);
    loginBox.setPreferredSize(new Dimension(350, 300));
    loginBox.setBackground(Color.white);
    loginBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

    JLabel userLabel = new JLabel("Username");
    userLabel.setBounds(40, 40, 270, 25);
    loginBox.add(userLabel);

    RoundedTextField userField = new RoundedTextField(25);
    userField.setText("admin"); // @OnlyForDevelopment - remove in production
    userField.setBounds(40, 65, 270, 35);
    userField.setBackground(Color.lightGray);
    loginBox.add(userField);

    JLabel passLabel = new JLabel("Password");
    passLabel.setBounds(40, 110, 270, 25);
    loginBox.add(passLabel);

    RoundedPasswordField passField = new RoundedPasswordField(25);
    passField.setText("admin"); // @OnlyForDevelopment - remove in production
    passField.setBounds(40, 135, 270, 35);
    passField.setBackground(Color.lightGray);
    loginBox.add(passField);

    RoundedButton loginButton = new RoundedButton("Login", 25);
    loginButton.setBounds(85, 195, 180, 40);
    loginButton.setBackground(new Color(50, 120, 200));
    loginButton.setForeground(Color.white);
    loginBox.add(loginButton);

    loginButton.addActionListener(e -> {
      String password = new String(passField.getPassword());
      String username = userField.getText();

      // Show loading indicator
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      loginButton.setEnabled(false);
      loginButton.setText("Logging in...");

      // Perform authentication
      boolean isAuthenticated = AuthenticationService.authenticate(username, password);

      // Reset UI state
      setCursor(Cursor.getDefaultCursor());
      loginButton.setEnabled(true);
      loginButton.setText("Login");

      if (!isAuthenticated) {
        JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Get the user role from the authentication service
      UserRole userRole = AuthenticationService.getCurrentUserRole();

      // Check if the user has a valid role
      if (userRole == UserRole.ADMIN || userRole == UserRole.MANAGER) {
        // Update notification service with the current user ID
        notificationService.setCurrentUserId(username);

        // Direct all authenticated users to the Dashboard
        Router.showPage("Dashboard");
      } else {
        // This shouldn't happen if properly authenticated, but just in case
        JOptionPane.showMessageDialog(this, "Unknown user role", "Error", JOptionPane.ERROR_MESSAGE);
        AuthenticationService.logout(); // Log them out
      }
    });

    JPanel wrapperPanel = new JPanel(new GridBagLayout());
    wrapperPanel.add(loginBox);
    add(wrapperPanel, BorderLayout.CENTER);

    // Add footer with version info
    JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    footerPanel.setBackground(Color.WHITE);
    JLabel versionLabel = new JLabel("EventEase v1.0");
    versionLabel.setForeground(Color.GRAY);
    footerPanel.add(versionLabel);
    add(footerPanel, BorderLayout.SOUTH);
  }
}
