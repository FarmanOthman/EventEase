package ui;

import javax.swing.*;
import java.awt.*;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.RoundedPasswordField;

public class LoginView {
  public static void main(String[] args) {
    // Create the frame for the login view
    JFrame frame = new JFrame("Login");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1000, 800);
    frame.setLocationRelativeTo(null);
    frame.getContentPane().setBackground(new Color(242, 242, 242)); // Light grey background

    // Create a panel to hold the components
    JPanel panel = new JPanel();
    panel.setLayout(null);
    panel.setOpaque(false); // Make the panel background transparent
    frame.add(panel);

    // Create a rounded login box
    JPanel loginBox = new JPanel();
    loginBox.setLayout(null);
    loginBox.setBounds(300, 150, 400, 400); // Position and size of the login box
    loginBox.setBackground(Color.white);
    loginBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Light border
    loginBox.setOpaque(true); // Make the box opaque
    panel.add(loginBox);

    // Create and configure the title label
    JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 40)); // Modern font and size
    titleLabel.setForeground(new Color(50, 120, 200)); // Calming blue color
    titleLabel.setBounds(0, 20, 400, 60); // Centered in the login box
    loginBox.add(titleLabel);

    // Create and configure the username label and field
    JLabel userLabel = new JLabel("Username");
    userLabel.setBounds(50, 100, 300, 30);
    loginBox.add(userLabel);

    RoundedTextField userField = new RoundedTextField(25); // 25 is the radius for rounded corners
    userField.setBounds(50, 130, 300, 40); // Increased height for better readability
    userField.setBackground(Color.lightGray);
    userField.setForeground(Color.darkGray);
    userField.setBorder(BorderFactory.createLineBorder(Color.darkGray)); // Darker grey border
    userField.setCaretColor(Color.darkGray); // Cursor color userField.setFont(new Font("Arial", Font.PLAIN, 16)); //
                                             // Larger font for clarity
    loginBox.add(userField);

    // Create and configure the password label and field
    JLabel passLabel = new JLabel("Password");
    passLabel.setBounds(50, 180, 300, 30);
    loginBox.add(passLabel);

    RoundedPasswordField passField = new RoundedPasswordField(25); // 25 is the radius for rounded corners
    passField.setBounds(50, 210, 300, 40); // Increased height for better readability
    passField.setBackground(Color.lightGray);
    passField.setForeground(Color.darkGray);
    passField.setBorder(BorderFactory.createLineBorder(Color.darkGray)); // Darker grey border
    passField.setCaretColor(Color.darkGray); // Cursor color
    passField.setFont(new Font("Arial", Font.PLAIN, 16)); // Larger font for clarity
    loginBox.add(passField);

    // Use the custom RoundedButton
    RoundedButton loginButton = new RoundedButton("Login", 25); // 25 is the radius for rounded corners
    loginButton.setBounds(100, 270, 200, 50); // Adjusted size for better balance
    loginButton.setBackground(new Color(50, 120, 200)); // Blue button
    loginButton.setForeground(Color.white);
    loginBox.add(loginButton);

    // Make the frame visible
    frame.setVisible(true);
  }
}
