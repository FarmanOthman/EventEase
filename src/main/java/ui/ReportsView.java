package ui;

import ui.components.Sidebar; // Import your Sidebar class
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class ReportsView extends JFrame {
    private JPanel mainPanel, contentPanel;
    private Sidebar leftPanel; // Changed from JPanel to Sidebar
    private JTable salesTable;
    private JFrame frame;
    
    public ReportsView() {
        setTitle("Sales Reporting Analytics");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create sidebar using the Sidebar class
        mainPanel.add(new Sidebar(frame), BorderLayout.WEST);
        
        // Create main panel with sales reporting content
        createMainPanel();
        
        // Add panels to frame
        add(leftPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Create header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(64, 133, 219)); // Same blue as left panel
        headerPanel.setPreferredSize(new Dimension(600, 50));
        JLabel headerLabel = new JLabel("Reports"); // Keeping the typo from the image
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);
        
        // Create content panel for the sales reporting
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Create sales reporting components
        createSalesReportingComponents();
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }
    
    private void createSalesReportingComponents() {
        // Sales Reporting Analytics title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(64, 133, 219)); // Blue background
        titlePanel.setMaximumSize(new Dimension(800, 40));
        titlePanel.setPreferredSize(new Dimension(800, 40));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Sales Reporting Analytics");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titlePanel.add(titleLabel);
        
        // Sales Data panel
        JPanel salesDataPanel = new JPanel();
        salesDataPanel.setLayout(new BorderLayout());
        salesDataPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        salesDataPanel.setMaximumSize(new Dimension(800, 300));
        salesDataPanel.setPreferredSize(new Dimension(800, 300));
        salesDataPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Sales Data title
        JLabel salesDataLabel = new JLabel("Sales Data");
        salesDataLabel.setFont(new Font("Arial", Font.BOLD, 14));
        salesDataPanel.add(salesDataLabel, BorderLayout.NORTH);

        // Create custom table model
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[][] {
                {"12 Feb 2025", 450, "$22,500", "VIP"},
                {"15 Feb 2025", 620, "$31,000", "Standard"},
                {"20 Feb 2025", 320, "$16,000", "Premium"}
            },
            new String[] {"Date", "Tickets Sold", "Revenue ($)", "Category"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table with custom header
        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(30);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(Color.LIGHT_GRAY);
        salesTable.setFillsViewportHeight(true);
        
        // Alternating row colors
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(new Color(240, 240, 240));
                } else {
                    c.setBackground(Color.WHITE);
                }
                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                }
                setHorizontalAlignment(column == 0 ? LEFT : CENTER);
                return c;
            }
        });
        
        // Create custom header panel that matches the image exactly
        JPanel headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(new Color(64, 133, 219));
        
        String[] headerLabels = {"Date", "Tickets Sold", "Revenue ($)", "Category"};
        for (String label : headerLabels) {
            JLabel headerLabel = new JLabel(label, SwingConstants.CENTER);
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            headerLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            headerPanel.add(headerLabel);
        }
        
        // Add custom header and table to panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(headerPanel, BorderLayout.NORTH);
        tablePanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        
        salesDataPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setMaximumSize(new Dimension(800, 50));
        filterPanel.setPreferredSize(new Dimension(800, 50));
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Filter components
        JLabel filterLabel = new JLabel("Filter by Date/Event:");
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"[Select Date/Event]"});
        
        // Apply filter button
        JButton applyButton = createStyledButton("Apply Filter", new Color(64, 133, 219));
        
        // Export button
        JButton exportButton = createStyledButton("Export With Analyzing", new Color(46, 204, 113));
        
        // Add components to filter panel
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(filterCombo);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(applyButton);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(exportButton);
        
        // Add all panels to content
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(salesDataPanel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(filterPanel);
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
                return new Dimension(150, 35);
            }
        };
        
        // Remove default button styling
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new ReportsView());
    }
}