package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import javax.swing.SpinnerDateModel;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DataPersistenceView extends JPanel {
  private JPanel mainPanel;
  private JSpinner fromDateSpinner;
  private JSpinner toDateSpinner;

  public DataPersistenceView() {
    setLayout(new BorderLayout());

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Create main panel
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);
  }

  private void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(Color.WHITE);

    // Create header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setBackground(new Color(64, 133, 219));
    headerPanel.setPreferredSize(new Dimension(600, 50));
    JLabel headerLabel = new JLabel("Data Persistence");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);

    // Create content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

    // Create sections
    createImportExportSection(contentPanel);
    createBackupSettingsSection(contentPanel);
    createBackupHistorySection(contentPanel);

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
  }

  private void createImportExportSection(JPanel parent) {
    // Create main panel for this section
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    panel.setMaximumSize(new Dimension(800, 200));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Title
    JLabel title = new JLabel("Import/Export Data");
    title.setFont(new Font("Arial", Font.BOLD, 16));
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Create grid panel for buttons and date selection
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    contentPanel.setBackground(Color.WHITE);
    contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Create date selection panel
    JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    datePanel.setBackground(Color.WHITE);

    // From date
    JLabel fromLabel = new JLabel("From:");
    Date initDate = new Date();
    SpinnerDateModel fromModel = new SpinnerDateModel(initDate, null, null, Calendar.DAY_OF_MONTH);
    fromDateSpinner = new JSpinner(fromModel);
    JSpinner.DateEditor fromEditor = new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd");
    fromDateSpinner.setEditor(fromEditor);
    fromDateSpinner.setPreferredSize(new Dimension(150, 30));

    // To date
    JLabel toLabel = new JLabel("To:");
    SpinnerDateModel toModel = new SpinnerDateModel(initDate, null, null, Calendar.DAY_OF_MONTH);
    toDateSpinner = new JSpinner(toModel);
    JSpinner.DateEditor toEditor = new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd");
    toDateSpinner.setEditor(toEditor);
    toDateSpinner.setPreferredSize(new Dimension(150, 30));

    datePanel.add(fromLabel);
    datePanel.add(fromDateSpinner);
    datePanel.add(Box.createHorizontalStrut(20));
    datePanel.add(toLabel);
    datePanel.add(toDateSpinner);

    // Create buttons panel
    JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonsPanel.setBackground(Color.WHITE);

    // Create buttons
    JButton importButton = createStyledButton("Import Data", new Color(64, 133, 219));
    JButton exportButton = createStyledButton("Export Data", new Color(40, 167, 69));

    // Add action listeners
    importButton.addActionListener(e -> handleImport());
    exportButton.addActionListener(e -> handleExport());

    buttonsPanel.add(importButton);
    buttonsPanel.add(Box.createHorizontalStrut(10));
    buttonsPanel.add(exportButton);

    contentPanel.add(datePanel);
    contentPanel.add(Box.createVerticalStrut(15));
    contentPanel.add(buttonsPanel);

    panel.add(title);
    panel.add(Box.createVerticalStrut(15));
    panel.add(contentPanel);

    parent.add(panel);
    parent.add(Box.createVerticalStrut(20));
  }

  private void createBackupSettingsSection(JPanel parent) {
    // Create main panel for this section
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    panel.setMaximumSize(new Dimension(800, 150));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Title
    JLabel title = new JLabel("Automatic Backup Settings");
    title.setFont(new Font("Arial", Font.BOLD, 16));
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Settings panel
    JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    settingsPanel.setBackground(Color.WHITE);

    JLabel frequencyLabel = new JLabel("Backup Frequency:");
    String[] frequencies = { "Daily", "Weekly", "Monthly", "Quarterly", "Yearly" };
    JComboBox<String> frequencyCombo = new JComboBox<>(frequencies);
    frequencyCombo.setPreferredSize(new Dimension(150, 30));

    JButton manageButton = createStyledButton("Manage Backups", new Color(64, 133, 219));
    manageButton.addActionListener(e -> handleManageBackups());

    settingsPanel.add(frequencyLabel);
    settingsPanel.add(Box.createHorizontalStrut(10));
    settingsPanel.add(frequencyCombo);
    settingsPanel.add(Box.createHorizontalStrut(20));
    settingsPanel.add(manageButton);

    panel.add(title);
    panel.add(Box.createVerticalStrut(15));
    panel.add(settingsPanel);

    parent.add(panel);
    parent.add(Box.createVerticalStrut(20));
  }

  private void createBackupHistorySection(JPanel parent) {
    // Create main panel for this section
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(15, 15, 15, 15)));
    panel.setMaximumSize(new Dimension(800, 200));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Title
    JLabel title = new JLabel("Backup History");
    title.setFont(new Font("Arial", Font.BOLD, 16));
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    // History items panel
    JPanel historyPanel = new JPanel();
    historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
    historyPanel.setBackground(Color.WHITE);

    // Create history items with different background colors
    createHistoryItem(historyPanel, "Backup - Feb 12, 2025", new Color(220, 255, 220));
    createHistoryItem(historyPanel, "Backup - Feb 05, 2025", new Color(255, 243, 205));
    createHistoryItem(historyPanel, "Backup - Jan 28, 2025", new Color(255, 220, 220));

    panel.add(title);
    panel.add(Box.createVerticalStrut(15));
    panel.add(historyPanel);

    parent.add(panel);
  }

  private JButton createStyledButton(String text, Color backgroundColor) {
    JButton button = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (backgroundColor.equals(Color.WHITE)) {
          g2.setColor(backgroundColor);
          g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
          g2.setColor(Color.LIGHT_GRAY);
          g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
          g2.setColor(Color.BLACK);
        } else {
          g2.setColor(backgroundColor);
          g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
          g2.setColor(Color.WHITE);
        }

        FontMetrics fm = g2.getFontMetrics();
        Rectangle textRect = new Rectangle(0, 0, getWidth(), getHeight());
        int x = (textRect.width - fm.stringWidth(text)) / 2;
        int y = (textRect.height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        g2.dispose();
      }
    };

    button.setPreferredSize(new Dimension(text.equals("Select from when to when") ? 200 : 150, 35));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  private void createHistoryItem(JPanel parent, String text, Color backgroundColor) {
    JPanel itemPanel = new JPanel();
    itemPanel.setLayout(new BorderLayout());
    itemPanel.setBackground(backgroundColor);
    itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    itemPanel.setMaximumSize(new Dimension(800, 40));

    JLabel label = new JLabel(text);
    label.setFont(new Font("Arial", Font.PLAIN, 14));
    itemPanel.add(label, BorderLayout.CENTER);

    parent.add(itemPanel);
    parent.add(Box.createVerticalStrut(5));
  }

  private void handleImport() {
    Date fromDate = (Date) fromDateSpinner.getValue();
    Date toDate = (Date) toDateSpinner.getValue();

    if (fromDate.after(toDate)) {
      JOptionPane.showMessageDialog(this,
          "Start date must be before end date",
          "Invalid Date Range",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    // TODO: Data Import Implementation
    // 1. Create DataImportService in services/data/DataImportService.java
    // - Implement methods for different data formats (CSV, JSON, XML)
    // - Add validation for imported data
    // - Handle large file imports with progress tracking
    //
    // 2. Data Processing Steps:
    // a) Show file chooser dialog for import source
    // b) Validate file format and content
    // c) Parse data according to format
    // d) Validate data against database schema
    // e) Begin transaction
    // f) Import data in chunks
    // g) Commit transaction
    // h) Show success/error message
    //
    // 3. Error Handling:
    // - Handle invalid file formats
    // - Handle corrupted data
    // - Implement rollback mechanism
    // - Log errors for debugging
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String message = String.format("Importing data from %s to %s",
        sdf.format(fromDate),
        sdf.format(toDate));
    JOptionPane.showMessageDialog(this, message);
  }

  private void handleExport() {
    Date fromDate = (Date) fromDateSpinner.getValue();
    Date toDate = (Date) toDateSpinner.getValue();

    if (fromDate.after(toDate)) {
      JOptionPane.showMessageDialog(this,
          "Start date must be before end date",
          "Invalid Date Range",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    // TODO: Data Export Implementation
    // 1. Create DataExportService in services/data/DataExportService.java
    // - Support multiple export formats (CSV, JSON, XML)
    // - Implement data filtering by date range
    // - Add progress tracking for large exports
    //
    // 2. Export Process:
    // a) Query data within date range
    // b) Create export file with appropriate format
    // c) Write data in chunks
    // d) Validate exported file
    // e) Compress if needed
    // f) Save to user-selected location
    //
    // 3. Features to Add:
    // - Export templates
    // - Scheduled exports
    // - Email notification when export is complete
    // - Export history tracking
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String message = String.format("Exporting data from %s to %s",
        sdf.format(fromDate),
        sdf.format(toDate));
    JOptionPane.showMessageDialog(this, message);
  }

  private void handleManageBackups() {
    // TODO: Backup Management Implementation
    // 1. Create BackupService in services/backup/BackupService.java
    // - Implement automatic backup scheduling
    // - Add backup verification
    // - Support incremental backups
    // - Implement backup rotation
    //
    // 2. Features to Implement:
    // a) Backup scheduling configuration
    // b) Backup location management
    // c) Backup size estimation
    // d) Backup integrity checking
    // e) Restore functionality
    // f) Backup encryption
    //
    // 3. UI Components to Add:
    // - Backup progress indicator
    // - Backup size calculator
    // - Restore wizard
    // - Backup comparison tool
    JOptionPane.showMessageDialog(this,
        "Opening backup management...",
        "Manage Backups",
        JOptionPane.INFORMATION_MESSAGE);
  }

  // TODO: Additional Features to Implement
  // 1. Data Validation
  // - Create ValidationService for import/export data
  // - Add schema validation
  // - Implement data type checking
  //
  // 2. Security
  // - Add encryption for sensitive data
  // - Implement access control
  // - Add audit logging
  //
  // 3. Performance
  // - Implement data caching
  // - Add batch processing
  // - Optimize large data operations
  //
  // 4. User Experience
  // - Add progress indicators
  // - Implement cancel operations
  // - Add detailed error messages
  // - Create help documentation
}
