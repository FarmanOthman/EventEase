package ui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class AdminManagementView extends JPanel {
    private JPanel mainPanel, contentPanel;
    private JTable adminTable;
    private DefaultTableModel tableModel;
    
    public AdminManagementView() {
        setLayout(new BorderLayout());
        
        // Initialize main components
        createMainPanel();
        
        // Add main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
    }
    
    // Custom rounded panel class for styled containers
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
    
    // Custom button with rounded corners
    private class RoundedButton extends JButton {
        private Color buttonColor;
        private int radius = 10;
        
        public RoundedButton(String text, Color bgColor) {
            super(text);
            this.buttonColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.BOLD, 12));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(buttonColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            FontMetrics fm = g2.getFontMetrics();
            Rectangle textRect = fm.getStringBounds(getText(), g2).getBounds();
            int textX = (getWidth() - textRect.width) / 2;
            int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        
        // Create header panel
        createHeader();
        
        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        contentPanel.setLayout(new BorderLayout());
        
        // Create the admin list panel
        createAdminList();
        
        // Add panels to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(66, 133, 244));
        headerPanel.setPreferredSize(new Dimension(600, 50));
        
        JLabel headerLabel = new JLabel("Admin Management");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createAdminList() {
        // Create the outer panel with rounded corners
        RoundedPanel listPanel = new RoundedPanel(new BorderLayout(), 15);
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Admin List title
        JLabel listTitle = new JLabel("Admin List");
        listTitle.setFont(new Font("Arial", Font.BOLD, 16));
        listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        listPanel.add(listTitle, BorderLayout.NORTH);
        
        // Create table model with columns
        String[] columns = {"Admin Name", "Email", "Role", "Actions"};
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only make the Actions column editable
            }
        };
        
        for (String column : columns) {
            tableModel.addColumn(column);
        }
        
        // Add sample data
        tableModel.addRow(new Object[]{"John Doe", "john@example.com", "Admin", ""});
        
        // Create table
        adminTable = new JTable(tableModel);
        adminTable.setRowHeight(40);
        adminTable.setShowGrid(false);
        adminTable.setIntercellSpacing(new Dimension(0, 0));
        adminTable.getTableHeader().setBackground(new Color(66, 133, 244));
        adminTable.getTableHeader().setForeground(Color.WHITE);
        adminTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        adminTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Set first row background color to light gray
        adminTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 3) {
                    return createActionPanel();
                }
                
                c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
                return c;
            }
        });
        
        // Custom renderer for the Actions column
        adminTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        adminTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor());
        
        // Create scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(adminTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create panel for the Add New Admin button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton addButton = new JButton("+ Add New Admin");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(150, 40));
        
        // Create custom button with rounded corners
        RoundedButton addNewButton = new RoundedButton("+ Add New Admin", new Color(39, 174, 96));
        addNewButton.setPreferredSize(new Dimension(150, 40));
        buttonPanel.add(addNewButton);
        
        // Add panels to content panel
        contentPanel.add(listPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panel.setBackground(Color.WHITE);
        
        RoundedButton editButton = new RoundedButton("Edit", new Color(242, 153, 0));
        editButton.setPreferredSize(new Dimension(60, 25));
        
        RoundedButton deleteButton = new RoundedButton("Delete", new Color(231, 76, 60));
        deleteButton.setPreferredSize(new Dimension(60, 25));
        
        panel.add(editButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    // Custom cell renderer for the Actions column
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
            removeAll();
            
            RoundedButton editButton = new RoundedButton("Edit", new Color(242, 153, 0));
            editButton.setPreferredSize(new Dimension(60, 25));
            
            RoundedButton deleteButton = new RoundedButton("Delete", new Color(231, 76, 60));
            deleteButton.setPreferredSize(new Dimension(60, 25));
            
            add(editButton);
            add(deleteButton);
            
            return this;
        }
    }
    
    // Custom cell editor for the Actions column
    class ButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private JPanel panel;
        
        public ButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            
            RoundedButton editButton = new RoundedButton("Edit", new Color(242, 153, 0));
            editButton.setPreferredSize(new Dimension(60, 25));
            editButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Edit clicked!");
                fireEditingStopped();
            });
            
            RoundedButton deleteButton = new RoundedButton("Delete", new Color(231, 76, 60));
            deleteButton.setPreferredSize(new Dimension(60, 25));
            deleteButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "Delete clicked!");
                fireEditingStopped();
            });
            
            panel.add(editButton);
            panel.add(deleteButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
    
    public static void main(String[] args) {
        // Create and display the form
        JFrame frame = new JFrame("Admin Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.add(new AdminManagementView());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}