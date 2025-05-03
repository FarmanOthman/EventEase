package ui.pages;

import services.SalesDataService;
import ui.components.Sidebar;
import ui.components.RoundedButton;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * The main report viewing UI for displaying sales data, applying filters, and
 * exporting data.
 */
public class ReportsView extends JPanel {
    private JPanel mainPanel, contentPanel;
    private JTable salesTable;
    private SalesDataService salesDataService;
    private List<Map<String, Object>> currentSalesData;
    private JComboBox<String> filterCombo;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel statusLabel;
    private Color primaryColor = new Color(64, 133, 219);
    private Color accentColor = new Color(46, 204, 113);
    private Color lightGrayColor = new Color(245, 245, 245);

    public ReportsView() {
        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        currentSalesData = new ArrayList<>();

        // Initialize the service
        salesDataService = new SalesDataService();

        // Add the Sidebar component
        add(new Sidebar(), BorderLayout.WEST);

        // Initialize the main UI components
        createMainPanel();

        // Add window resize listener to ensure responsiveness
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                revalidate();
                repaint();
            }
        });
    }

    private void createMainPanel() {
        mainPanel.setBackground(Color.WHITE);

        // Create header panel
        createHeader();

        // Create content panel with status bar first
        // This ensures statusLabel is initialized before any data operations
        createContentPanel();

        // Now populate the content with data
        populateContent();

        add(mainPanel, BorderLayout.CENTER);
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(0, 50));

        // Create title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(primaryColor);
        

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(primaryColor);
        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(130, 28));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));

        RoundedButton searchButton = new RoundedButton("Search", 25);
        searchButton.setBackground(primaryColor.darker());
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setForeground(Color.white);
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add search action
        ActionListener searchAction = e -> performSearch(searchField.getText());
        searchButton.addActionListener(searchAction);
        searchField.addActionListener(searchAction);
        
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        headerPanel.add(searchIcon, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private void createContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Create and add status bar first to ensure it's initialized
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(lightGrayColor);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(Color.DARK_GRAY);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
    }

    private void populateContent() {
        // Sales Reporting Title
        JPanel titlePanel = createStyledPanel("", true);
        JLabel titleLabel = new JLabel("Sales Report Analytics");
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        // Add filter/export controls
        JPanel controlsPanel = createFilterPanel();
        contentPanel.add(controlsPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Add sales data table
        JPanel salesDataPanel = createSalesDataPanel();
        contentPanel.add(salesDataPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add event calendar panel
        JPanel calendarPanel = createCalendarPanel();
        contentPanel.add(calendarPanel);
    }

    private JPanel createStyledPanel(String title, boolean isHeader) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(isHeader ? primaryColor : Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(isHeader ? Color.WHITE : Color.BLACK);
        titleLabel.setFont(new Font("Arial", isHeader ? Font.BOLD : Font.PLAIN, 16));
        panel.add(titleLabel, BorderLayout.WEST);

        return panel;
    }

    private JPanel createSalesDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        createSalesTable(panel);

        return panel;
    }

    private void createSalesTable(JPanel parent) {
        // Fetch sales data from the service
        try {
            updateSalesData(salesDataService.getAllSalesData());
        } catch (Exception e) {
            System.err.println("Error loading sales data: " + e.getMessage());
            e.printStackTrace();
            updateSalesData(new ArrayList<>());
        }

        // Define columns that match the database structure
        String[] columnNames = {
                "Date", "Category", "Tickets Sold", "Revenue ($)"
        };

        // Convert the data into a table-friendly format
        Object[][] data = formatTableData(currentSalesData, columnNames);

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 2)
                    return Integer.class;
                if (column == 3)
                    return Double.class;
                return Object.class;
            }
        };

        salesTable = new JTable(model);
        setupTableProperties();

        // Add sorting capability
        tableSorter = new TableRowSorter<>(model);
        salesTable.setRowSorter(tableSorter);

        // Create table header with tooltips
        JPanel tableHeaderPanel = createTableHeader();

        parent.add(tableHeaderPanel, BorderLayout.NORTH);
        parent.add(new JScrollPane(salesTable), BorderLayout.CENTER);
    }

    private Object[][] formatTableData(List<Map<String, Object>> salesData, String[] columnNames) {
        Object[][] formattedData = new Object[salesData.size()][columnNames.length];

        for (int i = 0; i < salesData.size(); i++) {
            Map<String, Object> sale = salesData.get(i);
            formattedData[i][0] = sale.get("sale_date"); // Date
            formattedData[i][1] = sale.get("category"); // Category
            formattedData[i][2] = sale.get("tickets_sold"); // Tickets Sold
            formattedData[i][3] =  sale.get("revenue"); // Revenue
        }

        return formattedData;
    }

    private void setupTableProperties() {
        salesTable.setRowHeight(30);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(Color.LIGHT_GRAY);
        salesTable.setFillsViewportHeight(true);
        salesTable.getTableHeader().setReorderingAllowed(false);
        salesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Add double-click event handler for row details
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = salesTable.convertRowIndexToModel(salesTable.getSelectedRow());
                    if (row >= 0 && row < currentSalesData.size()) {
                        showDetailDialog(currentSalesData.get(row));
                    }
                }
            }
        });

        // Set custom cell renderer for each column
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? lightGrayColor : Color.WHITE);

                if (isSelected) {
                    c.setBackground(new Color(185, 209, 234));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setForeground(Color.BLACK);
                }

                // Right-align number columns
                if (column == 2 || column == 3) {
                    setHorizontalAlignment(JLabel.RIGHT);

                    // Format currency for revenue column
                    if (column == 3 && value != null) {
                        setText("$" + value);
                    }
                } else {
                    setHorizontalAlignment(JLabel.LEFT);
                }

                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        });
    }

    private JPanel createTableHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel tableTitle = new JLabel("Sales Data");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(tableTitle);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(Color.WHITE);
        JLabel infoLabel = new JLabel("Double-click a row for details • Click column headers to sort");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        infoPanel.add(infoLabel);

        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(infoPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Filter section title
        JLabel sectionTitle = new JLabel("Filter & Export Options");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 14));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Create filter controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel filterLabel = new JLabel("Filter by Date/Event:");

        // Populate filter combo with dates and events from the data
        populateFilterCombo();

        JButton applyButton = createStyledButton("Apply Filter", primaryColor);
        
        JButton exportButton = createStyledButton("Export With Analysis", accentColor);
        exportButton.setPreferredSize(new Dimension(150, 30));

        // Action Listener for the Apply Filter button
        applyButton.addActionListener(e -> {
            String filterCriteria = (String) filterCombo.getSelectedItem();
            applyFilter(filterCriteria);
        });

      

        // Action Listener for the Export button
        exportButton.addActionListener(e -> {
            exportSalesData();
        });

        controlsPanel.add(filterLabel);
        controlsPanel.add(Box.createHorizontalStrut(10));
        controlsPanel.add(filterCombo);
        controlsPanel.add(Box.createHorizontalStrut(15));
        controlsPanel.add(applyButton);
        controlsPanel.add(Box.createHorizontalStrut(10));
       
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(exportButton);

        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(controlsPanel);

        return panel;
    }

    private void populateFilterCombo() {
        Set<String> filterOptions = new TreeSet<>(); // Use TreeSet for automatic sorting

        // Add default option
        filterOptions.add("[Select Date/Event]");

        try {
            List<Map<String, Object>> allData = salesDataService.getAllSalesData();

            // Extract unique dates and event names
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            for (Map<String, Object> row : allData) {
                // Add date
                if (row.containsKey("sale_date") && row.get("sale_date") != null) {
                    Object dateObj = row.get("sale_date");
                    if (dateObj instanceof java.sql.Date) {
                        filterOptions.add(dateFormat.format(dateObj));
                    } else if (dateObj != null) {
                        filterOptions.add(dateObj.toString());
                    }
                }

                // Add event name (category)
                if (row.containsKey("category")) {
                    filterOptions.add(row.get("category").toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Error populating filter options: " + e.getMessage());
            e.printStackTrace();
        }

        // Create and populate combo box
        filterCombo = new JComboBox<>(filterOptions.toArray(new String[0]));
        filterCombo.setPreferredSize(new Dimension(180, 28));
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        RoundedButton button = new RoundedButton(text, 25);
        button.setBackground(backgroundColor);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.white);
        button.setPreferredSize(new Dimension(130, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void updateSalesData(List<Map<String, Object>> newData) {
        if (newData != null) {
            currentSalesData = newData;
            if (statusLabel != null) {
                statusLabel.setText("Displaying " + currentSalesData.size() + " records");
            }
        }
    }

    private void refreshTable() {
        if (salesTable == null)
            return;

        DefaultTableModel model = (DefaultTableModel) salesTable.getModel();
        String[] columnNames = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            columnNames[i] = model.getColumnName(i);
        }

        Object[][] data = formatTableData(currentSalesData, columnNames);
        model.setDataVector(data, columnNames);

        // Restore custom renderers after model change
        setupTableProperties();
    }

    private void applyFilter(String filterCriteria) {
        if (filterCriteria == null || "[Select Date/Event]".equals(filterCriteria)) {
            JOptionPane.showMessageDialog(this, "Please select a valid filter.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Fetch filtered data from the service
            List<Map<String, Object>> filteredData = salesDataService.filterSalesData(filterCriteria);

            if (filteredData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No data found for the selected filter.", "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Update the table with filtered data
            updateSalesData(filteredData);
            refreshTable();
            statusLabel.setText("Filtered: " + filteredData.size() + " records found");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error applying filter: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return;
        }

        try {
            // Use row filter for quick searching
            RowFilter<DefaultTableModel, Object> rowFilter = RowFilter.regexFilter("(?i)" + searchTerm);
            tableSorter.setRowFilter(rowFilter);

            int displayedRowCount = salesTable.getRowCount();
            statusLabel.setText("Search results: " + displayedRowCount + " records found");

            if (displayedRowCount == 0) {
                JOptionPane.showMessageDialog(this,
                        "No matches found for: " + searchTerm, "Search Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error in search: " + e.getMessage(),
                    "Search Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDetailDialog(Map<String, Object> rowData) {
        JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Event Details", true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(450, 320);
        detailDialog.setLocationRelativeTo(this);

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        detailPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        detailPanel.setBackground(Color.WHITE);

        // Create header
        JLabel headerLabel = new JLabel("Sales Event Details");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Format the details
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Add fields
        addDetailField(gridPanel, "Event Date:", rowData.get("sale_date").toString());
        addDetailField(gridPanel, "Category:", rowData.get("category").toString());
        addDetailField(gridPanel, "Tickets Sold:", rowData.get("tickets_sold").toString());
        addDetailField(gridPanel, "Revenue:", "$" + rowData.get("revenue").toString());

        // Add close button
        JButton closeButton = createStyledButton("Close", primaryColor);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> detailDialog.dispose());

        detailPanel.add(headerLabel);
        detailPanel.add(gridPanel);
        detailPanel.add(Box.createVerticalGlue());
        detailPanel.add(closeButton);

        detailDialog.add(detailPanel);
        detailDialog.setVisible(true);
    }

    private void addDetailField(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(labelComp);
        panel.add(valueComp);
    }

    private void exportSalesData() {
        if (currentSalesData.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No data to export.", "Export Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Sales Report As");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        fileChooser.setSelectedFile(new java.io.File("SalesReport_" +
                new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            final String filePath = fileChooser.getSelectedFile().getAbsolutePath().endsWith(".xlsx")
                    ? fileChooser.getSelectedFile().getAbsolutePath()
                    : fileChooser.getSelectedFile().getAbsolutePath() + ".xlsx";

            // Show progress indicator
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            statusLabel.setText("Exporting data to Excel...");

            // Export on a background thread to avoid UI freezing
            SwingWorker<Boolean, Void> exportWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return salesDataService.exportSalesData(currentSalesData, filePath);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ReportsView.this,
                                    "Sales data exported successfully to " + filePath,
                                    "Export Successful", JOptionPane.INFORMATION_MESSAGE);
                            statusLabel.setText("Export completed successfully");
                        } else {
                            JOptionPane.showMessageDialog(ReportsView.this,
                                    "Failed to export sales data: " + salesDataService.getLastErrorMessage(),
                                    "Export Failed", JOptionPane.ERROR_MESSAGE);
                            statusLabel.setText("Export failed");
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ReportsView.this,
                                "Error during export: " + e.getMessage(),
                                "Export Error", JOptionPane.ERROR_MESSAGE);
                        statusLabel.setText("Export error: " + e.getMessage());
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            };

            exportWorker.execute();
        }
    }

    private JPanel createCalendarPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Panel title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Event Calendar");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton viewFullCalendarButton = createStyledButton("View Full Calendar", accentColor);
        viewFullCalendarButton.setPreferredSize(new Dimension(150, 30));
        viewFullCalendarButton.addActionListener(e -> openCalendarView());

        headerPanel.add(viewFullCalendarButton, BorderLayout.EAST);

        // Create mini calendar view - simplified version
        JPanel miniCalendarPanel = new JPanel(new BorderLayout());
        miniCalendarPanel.setBackground(Color.WHITE);

        // Add month label and controls
        JPanel monthControlPanel = new JPanel(new BorderLayout());
        monthControlPanel.setBackground(primaryColor);
        monthControlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel monthLabel = new JLabel(java.time.YearMonth.now().format(
                java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")),
                SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthLabel.setForeground(Color.WHITE);

        monthControlPanel.add(monthLabel, BorderLayout.CENTER);

        miniCalendarPanel.add(monthControlPanel, BorderLayout.NORTH);

        // Placeholder for calendar grid - in real implementation would show key dates
        JLabel placeholderLabel = new JLabel("Click 'View Full Calendar' to see the detailed event schedule",
                SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        placeholderLabel.setForeground(Color.GRAY);
        miniCalendarPanel.add(placeholderLabel, BorderLayout.CENTER);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(miniCalendarPanel, BorderLayout.CENTER);

        return panel;
    }

    private void openCalendarView() {
        // Get the parent window
        Window parentWindow = SwingUtilities.getWindowAncestor(this);

        // Create a new JFrame for the calendar
        JFrame calendarFrame = new JFrame("Event Calendar");
        calendarFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        calendarFrame.setSize(800, 600);
        calendarFrame.setLocationRelativeTo(parentWindow);

        // Add the calendar view
        calendarFrame.add(new ui.pages.CalendarView());

        // Pack the frame to optimal size instead of fixed size
        calendarFrame.pack();
        // Show the frame
        calendarFrame.setVisible(true);
    }
}