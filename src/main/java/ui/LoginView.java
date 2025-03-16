package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;
import ui.components.RoundedPasswordField;
import server.AuthenticationService;

public class LoginView {
    private JFrame frame;
    private RoundedTextField userField;
    private RoundedPasswordField passField;
    private RoundedButton loginButton;
    private JPanel panel;

    public LoginView() {
        // Initialize the frame
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(242, 242, 242)); // Light grey background

        // Create a panel to hold the components
        panel = new JPanel();
        panel.setLayout(null);
        panel.setOpaque(false);
        frame.add(panel);

        // Create a rounded login box
        JPanel loginBox = new JPanel();
        loginBox.setLayout(null);
        loginBox.setBounds(300, 150, 400, 400);
        loginBox.setBackground(Color.white);
        loginBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        loginBox.setOpaque(true);
        panel.add(loginBox);

        // Create and configure the title label
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(50, 120, 200));
        titleLabel.setBounds(0, 20, 400, 60);
        loginBox.add(titleLabel);

        // Create and configure the username label and field
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(50, 100, 300, 30);
        loginBox.add(userLabel);

        userField = new RoundedTextField(25);
        userField.setBounds(50, 130, 300, 40);
        userField.setBackground(Color.lightGray);
        userField.setForeground(Color.darkGray);
        userField.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        userField.setFont(new Font("Arial", Font.PLAIN, 16));
        loginBox.add(userField);

        // Create and configure the password label and field
        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(50, 180, 300, 30);
        loginBox.add(passLabel);

        passField = new RoundedPasswordField(25);
        passField.setBounds(50, 210, 300, 40);
        passField.setBackground(Color.lightGray);
        passField.setForeground(Color.darkGray);
        passField.setBorder(BorderFactory.createLineBorder(Color.darkGray));
        passField.setFont(new Font("Arial", Font.PLAIN, 16));
        loginBox.add(passField);

        // Use the custom RoundedButton
        loginButton = new RoundedButton("Login", 25);
        loginButton.setBounds(100, 270, 200, 50);
        loginButton.setBackground(new Color(50, 120, 200));
        loginButton.setForeground(Color.white);
        loginBox.add(loginButton);

        // Add action listener for the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                boolean result = AuthenticationService.authenticate(username, password);

                if (result) {
                    // Clear the frame and set new content
                    frame.getContentPane().removeAll();
                    frame.setLayout(new BorderLayout()); // Ensure proper layout for dashboard
                    frame.setContentPane(new AdminDashboard(frame).getPanel());
                    // Refresh UI
                    frame.revalidate();
                    frame.repaint();
                }
                
                else {
                    JOptionPane.showMessageDialog(frame, "Login failed. Please check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }

    public void show() {
        frame.setVisible(true);
    }
}
