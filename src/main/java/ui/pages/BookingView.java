package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class BookingView extends JPanel {
    private JPanel mainPanel, contentPanel;

    public BookingView() {
        setLayout(new BorderLayout());

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Create main panel
        createMainPanel();

        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }

    // Custom rounded panel class (for the form panel only)
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
        mainPanel.setBackground(new Color(240, 240, 240));

        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(64, 143, 224));
        headerPanel.setPreferredSize(new Dimension(600, 50));

        JLabel headerLabel = new JLabel("Ticket Management");
        headerLabel.setForeground(new Color(240, 240, 255));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Create the booking system
        createBookingSystem();

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void createBookingSystem() {
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
        bookTicketLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bookTicketLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(bookTicketLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Name label and text field
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));

        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(800, 35));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setForeground(new Color(50, 50, 50));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));

        // Event label and combo
        JLabel eventLabel = new JLabel("Select Event:");
        eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        eventLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(eventLabel);
        formPanel.add(Box.createVerticalStrut(5));

        JComboBox<String> eventCombo = new JComboBox<>(new String[] { "Select Event",
                "Football Match - Team A vs Team B", "Concert - Artist X", "Basketball Game - Team C vs Team D" });
        eventCombo.setMaximumSize(new Dimension(800, 35));
        eventCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(eventCombo);
        formPanel.add(Box.createVerticalStrut(15));

        // Price label and combo
        JLabel priceLabel = new JLabel("Select Price Category:");
        priceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        priceLabel.setForeground(new Color(90, 90, 90));
        formPanel.add(priceLabel);
        formPanel.add(Box.createVerticalStrut(5));

        JComboBox<String> priceCombo = new JComboBox<>(
                new String[] { "Select Price Category", "VIP - $100", "Premium - $75", "Standard - $50" });
        priceCombo.setMaximumSize(new Dimension(800, 35));
        priceCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(priceCombo);
        formPanel.add(Box.createVerticalStrut(15));

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

        bookNowButton.setBackground(new Color(66, 133, 244));
        bookNowButton.setForeground(Color.WHITE);
        bookNowButton.setFocusPainted(false);
        bookNowButton.setBorderPainted(false);
        bookNowButton.setContentAreaFilled(false);
        bookNowButton.setPreferredSize(new Dimension(120, 35));
        bookNowButton.setFont(new Font("Arial", Font.PLAIN, 14));

        buttonPanel.add(bookNowButton);
        formPanel.add(buttonPanel);

        // Add components to content panel
        contentPanel.add(formPanel);
    }
}