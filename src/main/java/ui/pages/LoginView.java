package ui.pages;

import javax.swing.*;
import java.awt.*;
import server.AuthenticationService;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.RoundedPasswordField;
import ui.Router;

/**
 * TODO: Authentication System Architecture
 * 1. Create the following structure:
 * services/
 * ├── auth/
 * │ ├── AuthService.java # Core authentication logic
 * │ ├── SessionManager.java # Session handling
 * │ ├── TokenManager.java # JWT/Token management
 * │ └── PasswordService.java # Password hashing/validation
 * └── security/
 * ├── SecurityConfig.java # Security settings
 * └── RoleManager.java # Role-based access control
 *
 * 2. Security Features to Implement:
 * - Password hashing with salt
 * - Rate limiting for login attempts
 * - Session timeout management
 * - Two-factor authentication
 *
 * 3. Database Integration:
 * - User table with encrypted passwords
 * - Session tracking table
 * - Login history for auditing
 *
 * 4. Authentication Features:
 * - OAuth integration
 * - SSO support
 * - Remember me functionality
 * - Password reset flow
 *
 * 5. Security Measures:
 * - CAPTCHA integration
 * - IP-based blocking
 * - Device fingerprinting
 * - Suspicious activity detection
 *
 * 6. User Experience:
 * - Form validation
 * - Error messaging
 * - Loading indicators
 * - Auto-complete support
 *
 * 7. Session Management:
 * - Token-based auth
 * - Session persistence
 * - Multi-device handling
 * - Secure logout
 *
 * 8. Account Recovery:
 * - Security questions
 * - Email verification
 * - Phone verification
 * - Account unlock process
 *
 * 9. Audit Features:
 * - Login attempts logging
 * - Activity tracking
 * - Security alerts
 * - Compliance reporting
 */
public class LoginView extends JPanel {
  public LoginView() {
    setLayout(new BorderLayout());

    JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
    titleLabel.setForeground(new Color(50, 120, 200));
    add(titleLabel, BorderLayout.NORTH);

    JPanel loginBox = new JPanel();
    loginBox.setLayout(null);
    loginBox.setPreferredSize(new Dimension(400, 350));
    loginBox.setBackground(Color.white);
    loginBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

    JLabel userLabel = new JLabel("Username");
    userLabel.setBounds(50, 50, 300, 30);
    loginBox.add(userLabel);

    RoundedTextField userField = new RoundedTextField(25);
    userField.setBounds(50, 80, 300, 40);
    userField.setBackground(Color.lightGray);
    loginBox.add(userField);

    JLabel passLabel = new JLabel("Password");
    passLabel.setBounds(50, 140, 300, 30);
    loginBox.add(passLabel);

    RoundedPasswordField passField = new RoundedPasswordField(25);
    passField.setBounds(50, 170, 300, 40);
    passField.setBackground(Color.lightGray);
    loginBox.add(passField);

    RoundedButton loginButton = new RoundedButton("Login", 25);
    loginButton.setBounds(100, 240, 200, 50);
    loginButton.setBackground(new Color(50, 120, 200));
    loginButton.setForeground(Color.white);
    loginBox.add(loginButton);

    loginButton.addActionListener(e -> {
      String password = new String(passField.getPassword());
      String username = userField.getText();
      boolean isAuthenticated = AuthenticationService.authenticate(username, password);

      if (!isAuthenticated) {
        JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      
      Router.showPage("AdminDashboard"); // Switch to AdminDashboard
    });

    JPanel wrapperPanel = new JPanel(new GridBagLayout());
    wrapperPanel.add(loginBox);
    add(wrapperPanel, BorderLayout.CENTER);
  }
}
