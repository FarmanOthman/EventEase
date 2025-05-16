package ui.pages;

import javax.swing.*;
import java.awt.*;
import server.AuthenticationServer;
import server.AuthenticationServer.UserRole;
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

    // Add show password checkbox
    JCheckBox showPasswordCheckbox = new JCheckBox("Show Password");
    showPasswordCheckbox.setBounds(40, 170, 270, 20);
    showPasswordCheckbox.setBackground(Color.white);
    showPasswordCheckbox.setFocusPainted(false);
    loginBox.add(showPasswordCheckbox);

    // Add action listener to toggle password visibility
    showPasswordCheckbox.addActionListener(e -> {
        if (showPasswordCheckbox.isSelected()) {
            passField.setEchoChar((char) 0); // Show the password
        } else {
            passField.setEchoChar('\u2022'); // Hide the password (bullet character)
        }
    });

    RoundedButton loginButton = new RoundedButton("Login", 25);
    loginButton.setBounds(85, 195, 180, 40); // Move the button down slightly
    loginButton.setBackground(new Color(50, 120, 200));
    loginButton.setForeground(Color.white);
    loginBox.add(loginButton);
    loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


    loginButton.addActionListener(e -> {
      String password = new String(passField.getPassword());
      String username = userField.getText();

      

      // Perform authentication
      boolean isAuthenticated = AuthenticationServer.authenticate(username, password);

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
      UserRole userRole = AuthenticationServer.getCurrentUserRole();

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
