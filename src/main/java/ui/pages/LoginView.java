package ui.pages;

import javax.swing.*;
import java.awt.*;
import server.AuthenticationService;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.Sidebar;
import ui.components.RoundedPasswordField;
import ui.Router;

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
    userField.setText("admin"); // @OnlyForDevelopment
    userField.setBounds(50, 80, 300, 40);
    userField.setBackground(Color.lightGray);
    loginBox.add(userField);

    JLabel passLabel = new JLabel("Password");
    passLabel.setBounds(50, 140, 300, 30);
    loginBox.add(passLabel);

    RoundedPasswordField passField = new RoundedPasswordField(25);
    passField.setText("admin"); // @OnlyForDevelopment
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

      // Get the existing Sidebar instance from MyApp (NOT a new one)
      Sidebar s = new Sidebar();
      s.updateSidebar();

      if (username.equals("admin")) {
        s.setDashboardChoice("ManagerDashboard");
        Router.showPage("ManagerDashboard"); // Navigate to Manager Dashboard
      } else {
        s.setDashboardChoice("AdminDashboard");
        Router.showPage("AdminDashboard"); // Navigate to Admin Dashboard
      }
    });

    JPanel wrapperPanel = new JPanel(new GridBagLayout());
    wrapperPanel.add(loginBox);
    add(wrapperPanel, BorderLayout.CENTER);
  }
}
