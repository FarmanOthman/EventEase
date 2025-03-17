package ui.pages;

import javax.swing.*;
import java.awt.*;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
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
      Router.showPage("AdminDashboard"); // Navigate to AdminDashboard on successful login
    });

    JPanel wrapperPanel = new JPanel(new GridBagLayout());
    wrapperPanel.add(loginBox);
    add(wrapperPanel, BorderLayout.CENTER);
  }
}
