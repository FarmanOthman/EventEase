package ui;

import ui.components.Sidebar;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class BookingView extends JFrame {
    private JPanel mainPanel, contentPanel;
    
    public BookingView() {
        setTitle("Ticket Management");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Use the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);
        
        // Create main panel with ticket booking form
        createMainPanel();
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    // Custom rounded panel class
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 15;
        
        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            cornerRadius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
        }
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Light gray background
        
        // Create header panel with rounded corners
        JPanel headerPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER), 15);
        headerPanel.setBackground(new Color(64, 143, 224)); // Same blue as left panel
        headerPanel.setPreferredSize(new Dimension(600, 50));
        JLabel headerLabel = new JLabel("Ticket Management");
        headerLabel.setForeground(new Color(240, 240, 255)); // Lighter text color
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);
        
        // Create content panel for the booking system
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 240, 240)); // Light gray
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Create the booking system panel
        createBookingSystem();
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createBookingSystem() {
        // Ticket Booking System header with rounded corners
        JPanel bookingHeaderPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER), 15);
        bookingHeaderPanel.setBackground(new Color(64, 143, 224)); // Blue
        bookingHeaderPanel.setMaximumSize(new Dimension(800, 50));
        bookingHeaderPanel.setPreferredSize(new Dimension(600, 50));
        bookingHeaderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookingHeaderPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        
        JLabel bookingHeaderLabel = new JLabel("Ticket Booking System");
        bookingHeaderLabel.setForeground(new Color(240, 240, 255)); // Lighter text color
        bookingHeaderLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bookingHeaderPanel.add(bookingHeaderLabel);
        
        // Booking form panel with rounded corners
        JPanel formPanel = new RoundedPanel(null, 15);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(800, 400));
        
        // Book a Ticket title
        JLabel bookTicketLabel = new JLabel("Book a Ticket");
        bookTicketLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bookTicketLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bookTicketLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center text in the label
        bookTicketLabel.setForeground(new Color(90, 90, 90)); // Lighter text color
        formPanel.add(bookTicketLabel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Name field
        JTextField nameField = new JTextField("Enter name for ticket...");
        nameField.setMaximumSize(new Dimension(800, 35));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameField.setHorizontalAlignment(JTextField.CENTER); // Center text in the field
        nameField.setForeground(new Color(150, 150, 150)); // Lighter text color
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Event ID field
        JTextField eventIdField = new JTextField("Enter Id of Event");
        eventIdField.setMaximumSize(new Dimension(800, 35));
        eventIdField.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventIdField.setHorizontalAlignment(JTextField.CENTER); // Center text in the field
        eventIdField.setForeground(new Color(150, 150, 150)); // Lighter text color
        formPanel.add(eventIdField);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Price field
        JTextField priceField = new JTextField("Enter Price");
        priceField.setMaximumSize(new Dimension(800, 35));
        priceField.setAlignmentX(Component.CENTER_ALIGNMENT);
        priceField.setHorizontalAlignment(JTextField.CENTER); // Center text in the field
        priceField.setForeground(new Color(150, 150, 150)); // Lighter text color
        formPanel.add(priceField);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Type label
        JLabel typeLabel = new JLabel("Type");
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Keep original left alignment
        typeLabel.setForeground(new Color(90, 90, 90)); // Lighter text color
        formPanel.add(typeLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        // Type dropdown - keep original alignment and text format
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"chosse"});
        typeCombo.setMaximumSize(new Dimension(800, 35));
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT); // Keep original left alignment
        formPanel.add(typeCombo);
        formPanel.add(Box.createVerticalStrut(30));
        
        // Button panel for centering the Book Now button
        JPanel buttonPanel = new RoundedPanel(new FlowLayout(FlowLayout.CENTER), 15);
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(800, 50));
        
        // Create a custom rounded button that matches the image
        JButton bookNowButton = new JButton("Book Now") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(getForeground());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, 
                              (getHeight() - textHeight) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        
        bookNowButton.setBackground(new Color(66, 133, 244)); // Blue color from the image
        bookNowButton.setForeground(Color.WHITE); // White text as in the image
        bookNowButton.setFocusPainted(false);
        bookNowButton.setBorderPainted(false);
        bookNowButton.setContentAreaFilled(false);
        bookNowButton.setPreferredSize(new Dimension(120, 35));
        bookNowButton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        buttonPanel.add(bookNowButton);
        
        formPanel.add(buttonPanel);
        
        // Add components to content panel
        contentPanel.add(bookingHeaderPanel);
        contentPanel.add(formPanel);
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new BookingView());
    }
}