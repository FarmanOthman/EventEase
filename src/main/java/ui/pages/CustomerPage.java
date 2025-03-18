package ui.pages;

import ui.Router;
import javax.swing.*;
import java.awt.*;

/**
 * TODO: Customer Management System Architecture
 * 1. Create the following structure:
 * services/
 * ├── customer/
 * │ ├── CustomerService.java # Core customer functionality
 * │ ├── ProfileManager.java # Profile management
 * │ ├── PreferenceService.java # Customer preferences
 * │ └── LoyaltyService.java # Loyalty program
 * └── communication/
 * ├── NotificationManager.java # Customer notifications
 * └── FeedbackService.java # Customer feedback
 *
 * 2. Customer Features:
 * - Profile management
 * - Purchase history
 * - Loyalty points
 * - Preferences tracking
 *
 * 3. Integration Points:
 * - Booking system
 * - Payment system
 * - Email service
 * - Analytics system
 */
public class CustomerPage extends JPanel {
  private JPanel mainPanel;
  private JPanel contentPanel;
  private JTextField firstNameField;
  private JTextField lastNameField;
  private JTextField contactNumberField;
  private JTextField emailField;

  public CustomerPage() {
    setLayout(new BorderLayout());

    // Create main panel with customer form
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);
  }

  private void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create header panel
    JPanel headerPanel = new JPanel(new BorderLayout());
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(600, 50));

    // Add back button
    JButton backButton = createStyledButton("← Back", new Color(64, 133, 219));
    backButton.addActionListener(e -> {
      Router.showPage("BookingView");
    });
    JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    backButtonPanel.setBackground(new Color(64, 133, 219));
    backButtonPanel.add(backButton);
    headerPanel.add(backButtonPanel, BorderLayout.WEST);

    // Add title
    JLabel headerLabel = new JLabel("Customer Information");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    JPanel titlePanel = new JPanel();
    titlePanel.setBackground(new Color(64, 133, 219));
    titlePanel.add(headerLabel);
    headerPanel.add(titlePanel, BorderLayout.CENTER);

    // Create content panel for the form
    contentPanel = new JPanel();
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    // Create form components
    createFormComponents();

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  private void createFormComponents() {
    // First Name
    JPanel firstNamePanel = createFormField("First Name:", "Enter first name...");
    firstNameField = (JTextField) firstNamePanel.getComponent(1);

    // Last Name
    JPanel lastNamePanel = createFormField("Last Name:", "Enter last name...");
    lastNameField = (JTextField) lastNamePanel.getComponent(1);

    // Contact Number
    JPanel contactNumberPanel = createFormField("Contact Number:", "Enter contact number...");
    contactNumberField = (JTextField) contactNumberPanel.getComponent(1);

    // Email
    JPanel emailPanel = createFormField("Email:", "Enter email...");
    emailField = (JTextField) emailPanel.getComponent(1);

    // Save Button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.setMaximumSize(new Dimension(600, 40));

    JButton saveButton = createStyledButton("Save", new Color(64, 133, 219));
    saveButton.addActionListener(e -> saveCustomerInformation());
    buttonPanel.add(saveButton);

    // Add components to content panel with spacing
    contentPanel.add(firstNamePanel);
    contentPanel.add(Box.createVerticalStrut(15));
    contentPanel.add(lastNamePanel);
    contentPanel.add(Box.createVerticalStrut(15));
    contentPanel.add(contactNumberPanel);
    contentPanel.add(Box.createVerticalStrut(15));
    contentPanel.add(emailPanel);
    contentPanel.add(Box.createVerticalStrut(20));
    contentPanel.add(buttonPanel);
  }

  private JPanel createFormField(String labelText, String placeholder) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.setMaximumSize(new Dimension(600, 60));

    // Label
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("Arial", Font.PLAIN, 14));
    panel.add(label, BorderLayout.NORTH);

    // Text Field
    JTextField textField = new JTextField();
    textField.setFont(new Font("Arial", Font.PLAIN, 14));
    textField.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    panel.add(textField, BorderLayout.CENTER);

    return panel;
  }

  private JButton createStyledButton(String text, Color backgroundColor) {
    JButton button = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the rounded background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Paint the text
        FontMetrics fm = g2.getFontMetrics();
        Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());

        int x = (textRect.width - fm.stringWidth(text)) / 2;
        int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();

        g2.setColor(Color.WHITE);
        g2.drawString(text, x, y);
        g2.dispose();
      }

      @Override
      public Dimension getPreferredSize() {
        return new Dimension(100, 35);
      }
    };

    // Remove default button styling
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return button;
  }

  private void saveCustomerInformation() {
    String firstName = firstNameField.getText();
    String lastName = lastNameField.getText();
    String contactNumber = contactNumberField.getText();
    String email = emailField.getText();

    // Here you would typically save the information to a database
    // For now, just show a confirmation dialog
    JOptionPane.showMessageDialog(this,
        "Customer Information Saved Successfully!\n" +
            "First Name: " + firstName + "\n" +
            "Last Name: " + lastName + "\n" +
            "Contact Number: " + contactNumber + "\n" +
            "Email: " + email,
        "Success",
        JOptionPane.INFORMATION_MESSAGE);

    // Clear the fields after saving
    firstNameField.setText("");
    lastNameField.setText("");
    contactNumberField.setText("");
    emailField.setText("");

    // Return to EventView
    Router.showPage("EventView");
  }
}
