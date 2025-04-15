package ui.pages;

import services.SalesDataService;
import ui.components.Sidebar;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The main report viewing UI for displaying sales data, applying filters, and exporting data.
 */
public class ReportsView extends JPanel {
    private JPanel mainPanel, contentPanel;
    private JTable salesTable;
    private SalesDataService salesDataService;

    public ReportsView() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());

        // Initialize the service
        salesDataService = new SalesDataService();

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Initialize the main UI components
        createMainPanel();
    }

    private void createMainPanel() {
        mainPanel.setBackground(Color.WHITE);

        // Create header panel
        createHeader();

        // Create content panel
        createContent();

        add(mainPanel, BorderLayout.CENTER);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(64, 133, 219));
        headerPanel.setPreferredSize(new Dimension(600, 50));
        JLabel headerLabel = new JLabel("Reports");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createContent() {
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        createSalesReportingComponents();

        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }

    private void createSalesReportingComponents() {
        // Sales Reporting Title
        JPanel titlePanel = createStyledPanel("Sales Reporting Analytics", true);
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Sales Data panel
        JPanel salesDataPanel = createSalesDataPanel();
        contentPanel.add(salesDataPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Filter panel
        JPanel filterPanel = createFilterPanel();
        contentPanel.add(filterPanel);
    }

    private JPanel createStyledPanel(String title, boolean isHeader) {
        JPanel panel = new JPanel();
        panel.setBackground(isHeader ? new Color(64, 133, 219) : Color.WHITE);
        panel.setMaximumSize(new Dimension(800, 40));
        panel.setPreferredSize(new Dimension(800, 40));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(title);
        label.setForeground(isHeader ? Color.WHITE : Color.BLACK);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label);

        return panel;
    }

    private JPanel createSalesDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(800, 300));
        panel.setPreferredSize(new Dimension(800, 300));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createSalesTable(panel);

        return panel;
    }

    private void createSalesTable(JPanel parent) {
        // Fetch sales data from the service
        List<Map<String, Object>> salesData = salesDataService.getAllSalesData();

        // Convert the data into a table-friendly format
        String[] columnNames = { "Date", "Tickets Sold", "Revenue ($)", "Category" };
        Object[][] data = new Object[salesData.size()][columnNames.length];

        for (int i = 0; i < salesData.size(); i++) {
            Map<String, Object> sale = salesData.get(i);
            data[i][0] = sale.get("sale_date"); // Date
            data[i][1] = sale.get("tickets_sold"); // Tickets Sold
            data[i][2] = "$" + sale.get("revenue"); // Revenue
            data[i][3] = sale.get("category"); // Category
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(model);
        setupTableProperties();

        parent.add(createTableHeader(), BorderLayout.NORTH);
        parent.add(new JScrollPane(salesTable), BorderLayout.CENTER);
    }

    private void setupTableProperties() {
        salesTable.setRowHeight(30);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(Color.LIGHT_GRAY);
        salesTable.setFillsViewportHeight(true);

        // Set custom cell renderer for each column
   salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
        if (isSelected) {
            c.setBackground(table.getSelectionBackground());
        }

        if (column == 0) { // Align date column to the left
            setHorizontalAlignment(SwingConstants.LEFT);
        } else { // Align other columns (e.g., revenue, tickets sold) to the center
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        return c;
    }
});
    }

    private JPanel createTableHeader() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 4));
        headerPanel.setBackground(new Color(64, 133, 219));

        String[] headerLabels = { "Date", "Tickets Sold", "Revenue ($)", "Category" };
        for (String label : headerLabels) {
            JLabel headerLabel = new JLabel(label, SwingConstants.CENTER);
            headerLabel.setForeground(Color.WHITE);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            headerLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
            headerPanel.add(headerLabel);
        }

        return headerPanel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(800, 50));
        panel.setPreferredSize(new Dimension(800, 50));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel filterLabel = new JLabel("Filter by Date/Event:");
        JComboBox<String> filterCombo = new JComboBox<>(new String[] { "[Select Date/Event]" });
        JButton applyButton = createStyledButton("Apply Filter", new Color(64, 133, 219));
        JButton exportButton = createStyledButton("Export With Analyzing", new Color(46, 204, 113));

        // Action Listener for the Apply Filter button
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filterCriteria = (String) filterCombo.getSelectedItem();
                if (filterCriteria == null || "[Select Date/Event]".equals(filterCriteria)) {
                    JOptionPane.showMessageDialog(ReportsView.this, "Please select a valid filter.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Fetch filtered data from the service
                List<Map<String, Object>> filteredData = salesDataService.filterSalesData(filterCriteria);

                if (filteredData.isEmpty()) {
                    JOptionPane.showMessageDialog(ReportsView.this, "No data found for the selected filter.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Update the table with filtered data
                updateSalesTable(filteredData);
            }
        });

        // Action Listener for the Export button
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Fetch current table data
                DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
                List<Map<String, Object>> salesData = Collections.emptyList();

                for (int i = 0; i < model.getRowCount(); i++) {
                    Map<String, Object> sale = Map.of(
                        "sale_date", model.getValueAt(i, 0),
                        "tickets_sold", model.getValueAt(i, 1),
                        "revenue", model.getValueAt(i, 2).toString().replace("$", ""),
                        "category", model.getValueAt(i, 3)
                    );
                    salesData.add(sale);
                }

                // Export data using the service
                boolean success = salesDataService.exportSalesData(salesData, "sales_data.csv");
                if (success) {
                    JOptionPane.showMessageDialog(ReportsView.this, "Sales data exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(ReportsView.this, "Failed to export sales data: " + salesDataService.getLastErrorMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(filterLabel);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(filterCombo);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(applyButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(exportButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setPreferredSize(new Dimension(150, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void updateSalesTable(List<Map<String, Object>> salesData) {
        DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
        model.setRowCount(0); // Clear existing rows

        for (Map<String, Object> sale : salesData) {
            model.addRow(new Object[] {
                sale.get("sale_date"),
                sale.get("tickets_sold"),
                "$" + sale.get("revenue"),
                sale.get("category")
            });
        }
    }
}