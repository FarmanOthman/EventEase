package ui.pages;

import javax.swing.*;
import java.awt.*;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.RoundedPasswordField;
import ui.Router;

/**
 * TODO: Authentication System Design
 * 1. Define the following structure:
 * services/
 * ├── auth/
 * │ ├── AuthService.java # Core logic for authentication
 * │ ├── SessionManager.java # Manages user sessions
 * │ ├── TokenManager.java # Handles JWT/Token management
 * │ └── PasswordService.java # Handles password hashing and validation
 * └── security/
 * ├── SecurityConfig.java # Configures security settings
 * └── RoleManager.java # Manages role-based access control
 *
 * 2. Security Features:
 * - Salted password hashing
 * - Limit login attempts (Rate limiting)
 * - Session timeout management
 * - Two-factor authentication (2FA)
 *
 * 3. Database Integration:
 * - Store encrypted passwords in the user table
 * - Track sessions in a session table
 * - Log login history for audit purposes
 *
 * 4. Authentication Features:
 * - OAuth integration for external logins
 * - Single Sign-On (SSO) support
 * - "Remember me" functionality for persistent login
 * - Password reset process
 *
 * 5. Security Measures:
 * - CAPTCHA to prevent bot logins
 * - IP-based blocking for suspicious activities
 * - Device fingerprinting for additional security
 * - Detect and handle suspicious activities
 *
 * 6. User Experience:
 * - Form validation during login/signup
 * - Clear error messages for failed attempts
 * - Loading indicators during authentication
 * - Auto-complete for login fields
 *
 * 7. Session Management:
 * - Use token-based authentication
 * - Persist sessions across devices
 * - Ensure secure logout functionality
 *
 * 8. Account Recovery:
 * - Security questions for account recovery
 * - Email verification process
 * - Phone verification for additional security
 * - Unlock process for locked accounts
 *
 * 9. Audit and Monitoring:
 * - Track login attempts for auditing
 * - Monitor user activity for security
 * - Send security alerts for suspicious activities
 * - Compliance reporting for security audits
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
      Router.showPage("AdminDashboard"); // Switch to AdminDashboard
    });

    JPanel wrapperPanel = new JPanel(new GridBagLayout());
    wrapperPanel.add(loginBox);
    add(wrapperPanel, BorderLayout.CENTER);
  }
}
