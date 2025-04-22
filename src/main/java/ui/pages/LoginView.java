package ui.pages;

import javax.swing.*;
import java.awt.*;
import server.AuthenticationService;
import server.AuthenticationService.UserRole;
import services.NotificationService;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.RoundedPasswordField;
import ui.Router;

public class LoginView extends JPanel {
  private NotificationService notificationService;

  public LoginView() {
    setLayout(new BorderLayout());

    // Get service instances
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
    

    JLabel userLabel = new JLabel("Username:");
    userLabel.setBounds(40, 40, 270, 25);
    loginBox.add(userLabel);

    RoundedTextField userField = new RoundedTextField(25);
    userField.setBounds(40, 65, 270, 35);
    userField.setBackground(Color.lightGray);
    userField.setText("admin"); // Pre-fill for testing
    loginBox.add(userField);
    userField.setToolTipText("Enter Your Username"); 

    JLabel passLabel = new JLabel("Password:");
    passLabel.setBounds(40, 110, 270, 25);
    loginBox.add(passLabel);


    RoundedPasswordField passField = new RoundedPasswordField(25);
    passField.setBounds(40, 135, 270, 35);
    passField.setBackground(Color.lightGray);
    loginBox.add(passField);
    passField.setToolTipText("Enter Your Password");
    passField.setText("admin123"); // Pre-fill for testing

    RoundedButton loginButton = new RoundedButton("Login", 25);
    loginButton.setBounds(85, 195, 180, 40);
    loginButton.setBackground(new Color(50, 120, 200));
    loginButton.setForeground(Color.white);
    loginBox.add(loginButton);

    loginButton.addActionListener(e -> {
      String password = new String(passField.getPassword());
      String username = userField.getText();

      

      // Perform authentication
      boolean isAuthenticated = AuthenticationService.authenticate(username, password);

      // Reset UI state
      
       
      //  Check for empty fields

      if (username.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this,
          "Please enter both username and password.",
          "Input Required",
          JOptionPane.WARNING_MESSAGE
        );
        return;
      }
     
      

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
      } 
    });

    JPanel wrapperPanel = new JPanel(new GridBagLayout());
    wrapperPanel.add(loginBox);
    add(wrapperPanel, BorderLayout.CENTER);

   
  }
}
