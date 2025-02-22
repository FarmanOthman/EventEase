package ui;
import javax.swing.*;
import java.awt.*;

public class LoginView {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800); 
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);
        
        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40)); 
        titleLabel.setBounds(300, 50, 400, 60); 
        panel.add(titleLabel);
        
        JTextField userField = new JTextField();
        userField.setBounds(400, 250, 200, 30); 
        userField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(userField);
        
        JPasswordField passField = new JPasswordField();
        passField.setBounds(400, 300, 200, 30); 
        passField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panel.add(passField);
        
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(400, 350, 200, 30); 
        loginButton.setBackground(new Color(50, 120, 200));
        loginButton.setForeground(Color.white);
        panel.add(loginButton);
        
        int centerY = (frame.getHeight() - (titleLabel.getHeight() + userField.getHeight() + passField.getHeight() + loginButton.getHeight() + 100)) / 2; 
        titleLabel.setBounds(300, centerY - 100, 400, 60); 
        userField.setBounds(400, centerY, 200, 30); 
        passField.setBounds(400, centerY + 50, 200, 30); 
        loginButton.setBounds(400, centerY + 100, 200, 30); 

        frame.setVisible(true);
    }
}