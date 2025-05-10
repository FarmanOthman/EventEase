package ui.pages;

import ui.components.Sidebar;
import ui.components.RoundedButton;
import ui.Refreshable;
import services.DataPersistenceService;
import services.DataPersistenceService.BackupInfo;
import services.DataPersistenceService.BackupResult;
import services.DataPersistenceService.ImportResult;

import javax.swing.*;
import javax.swing.SpinnerDateModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.text.SimpleDateFormat;

public class DataPersistenceView extends JPanel implements Refreshable {
  private JPanel mainPanel;
  private JSpinner fromDateSpinner;
  private JSpinner toDateSpinner;
  private DataPersistenceService dataPersistenceService;
  private JPanel historyPanel;

  public DataPersistenceView() {
    setName("DataPersistenceView"); // Set the name for the Router to identify this panel
    setLayout(new BorderLayout());

    // Initialize the service
    dataPersistenceService = new DataPersistenceService();

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Create main panel
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);

    // Refresh backup history
    refreshBackupHistory();
  }

  /**
   * Implements the Refreshable interface to refresh data when navigating to this
   * view.
   */
  @Override
  public void refresh() {
    // Reset date spinners to current date
    Date currentDate = new Date();
    fromDateSpinner.setValue(currentDate);
    toDateSpinner.setValue(currentDate);

    // Refresh backup history
    refreshBackupHistory();

    // Refresh sidebar
    Component sidebarComponent = null;
    for (Component component : getComponents()) {
      if (component instanceof Sidebar) {
        sidebarComponent = component;
        break;
      }
    }

    if (sidebarComponent != null) {
      // Remove old sidebar
      remove(sidebarComponent);

      // Add new sidebar
      Sidebar sidebar = new Sidebar();
      add(sidebar, BorderLayout.WEST);
    }

    revalidate();
    repaint();
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

    JButton manageButton = createStyledButton("Create Backup Now", new Color(64, 133, 219));
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
    historyPanel = new JPanel();
    historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
    historyPanel.setBackground(Color.WHITE);

    panel.add(title);
    panel.add(Box.createVerticalStrut(15));
    panel.add(historyPanel);

    parent.add(panel);
  }

  private JButton createStyledButton(String text, Color backgroundColor) {
    RoundedButton button = new RoundedButton(text, 25);
    button.setBackground(backgroundColor);
    button.setFont(new Font("Arial", Font.BOLD, 14));
    button.setForeground(Color.white);
    button.setPreferredSize(new Dimension(120, 40));
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    return button;
  }

  private void createHistoryItem(JPanel parent, BackupInfo backup) {
    JPanel itemPanel = new JPanel();
    itemPanel.setLayout(new BorderLayout());

    // Set background color based on age (newer backups are greener)
    long ageInDays = (new Date().getTime() - backup.getCreationDate().getTime()) / (1000 * 60 * 60 * 24);
    Color backgroundColor;
    if (ageInDays < 7) {
      backgroundColor = new Color(220, 255, 220); // Green for recent backups
    } else if (ageInDays < 30) {
      backgroundColor = new Color(255, 243, 205); // Yellow for older backups
    } else {
      backgroundColor = new Color(255, 220, 220); // Red for very old backups
    }

    itemPanel.setBackground(backgroundColor);
    itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    itemPanel.setMaximumSize(new Dimension(800, 40));

    // Format date for display
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    String dateText = sdf.format(backup.getCreationDate());

    // Format file size
    String sizeText = formatFileSize(backup.getSize());

    JLabel label = new JLabel(backup.getName() + " - " + dateText + " (" + sizeText + ")");
    label.setFont(new Font("Arial", Font.PLAIN, 14));
    itemPanel.add(label, BorderLayout.CENTER);

    // Add restore button (could be implemented in a future version)
    RoundedButton restoreButton = new RoundedButton("Restore", 25);
    restoreButton.setBackground(new Color(64, 133, 219));
    restoreButton.setFont(new Font("Arial", Font.BOLD, 14));
    restoreButton.setForeground(Color.white);
    restoreButton.setPreferredSize(new Dimension(80, 25));
    restoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    itemPanel.add(restoreButton, BorderLayout.EAST);

    parent.add(itemPanel);
    parent.add(Box.createVerticalStrut(5));
  }

  private String formatFileSize(long size) {
    if (size < 1024) {
      return size + " B";
    } else if (size < 1024 * 1024) {
      return String.format("%.1f KB", size / 1024.0);
    } else if (size < 1024 * 1024 * 1024) {
      return String.format("%.1f MB", size / (1024.0 * 1024));
    } else {
      return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
  }

  private void refreshBackupHistory() {
    // Clear existing items
    historyPanel.removeAll();

    // Get backup list from service
    List<BackupInfo> backups = dataPersistenceService.listBackups();

    if (backups.isEmpty()) {
      JLabel noBackupsLabel = new JLabel("No backups found");
      noBackupsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
      noBackupsLabel.setForeground(Color.GRAY);
      historyPanel.add(noBackupsLabel);
    } else {
      // Add backup items to history panel
      for (BackupInfo backup : backups) {
        createHistoryItem(historyPanel, backup);
      }
    }

    // Refresh panel
    historyPanel.revalidate();
    historyPanel.repaint();
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

    // Step 1: Show entity type selection dialog
    String[] entityTypes = { "Event", "Ticket" };
    String selectedEntityType = (String) JOptionPane.showInputDialog(
        this,
        "Select what you want to import:",
        "Import Selection",
        JOptionPane.QUESTION_MESSAGE,
        null,
        entityTypes,
        entityTypes[0]);

    if (selectedEntityType == null) {
      return; // User canceled
    }

    // Step 2: Show file format information
    StringBuilder formatInfo = new StringBuilder();
    formatInfo.append("Please ensure your Excel file has the following format:\n\n");

    if (selectedEntityType.equals("Event")) {
      formatInfo.append("Required columns:\n");
      formatInfo.append("- event_name: Name of the event (text)\n");
      formatInfo.append("- event_date: Date of the event (YYYY-MM-DD)\n");
      formatInfo.append("- team_a: First team or participant (text)\n");
      formatInfo.append("- team_b: Second team or participant (text)\n");
      formatInfo.append("- category: Must be either 'Regular' or 'VIP' (text)\n");
      formatInfo.append("- event_type: Must be either 'Event' or 'Match' (text)\n\n");

      formatInfo.append("Optional columns:\n");
      formatInfo.append("- event_description: Description of the event (text)\n");
    } else if (selectedEntityType.equals("Ticket")) {
      formatInfo.append("Required columns:\n");
      formatInfo.append("- event_id: ID of the associated event (number)\n");
      formatInfo.append("- ticket_type: Must be either 'Regular' or 'VIP' (text)\n");
      formatInfo.append("- ticket_date: Date of the ticket (YYYY-MM-DD)\n");
      formatInfo.append("- price: Ticket price (number, must be positive)\n\n");

      formatInfo.append("Optional columns:\n");
      formatInfo
          .append("- ticket_status: Must be 'Available', 'Sold', or 'Canceled' (text, defaults to 'Available')\n");
    }

    JOptionPane.showMessageDialog(
        this,
        formatInfo.toString(),
        "File Format Requirements",
        JOptionPane.INFORMATION_MESSAGE);

    // Step 3: Show file chooser dialog
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Select Excel File to Import");
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        "Excel Files", "xlsx", "xls");
    fileChooser.setFileFilter(filter);

    int result = fileChooser.showOpenDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
      return; // User canceled
    }

    // Step 4: Process the import
    File selectedFile = fileChooser.getSelectedFile();
    ImportResult importResult = dataPersistenceService.importFromExcel(
        selectedFile.getAbsolutePath(),
        selectedEntityType);

    // Step 5: Show result message
    if (importResult.isSuccess()) {
      JOptionPane.showMessageDialog(
          this,
          importResult.getMessage(),
          "Import Successful",
          JOptionPane.INFORMATION_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(
          this,
          "Import failed: " + importResult.getMessage(),
          "Import Error",
          JOptionPane.ERROR_MESSAGE);
    }
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

    // Step 1: Select what to export
    String[] exportTypes = { "Events", "Tickets", "Sales Report" };
    String selectedType = (String) JOptionPane.showInputDialog(
        this,
        "Select what you want to export:",
        "Export Selection",
        JOptionPane.QUESTION_MESSAGE,
        null,
        exportTypes,
        exportTypes[0]);

    if (selectedType == null) {
      return; // User canceled
    }

    // Step 2: Select export format
    String[] exportFormats = { "Excel (.xlsx)", "PDF (.pdf)" };
    String selectedFormat = (String) JOptionPane.showInputDialog(
        this,
        "Select export format:",
        "Export Format",
        JOptionPane.QUESTION_MESSAGE,
        null,
        exportFormats,
        exportFormats[0]);

    if (selectedFormat == null) {
      return; // User canceled
    }

    // Step 3: Choose destination
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Export File");

    // Set extension filter based on selected format
    String extension = selectedFormat.contains("Excel") ? "xlsx" : "pdf";
    FileNameExtensionFilter filter = new FileNameExtensionFilter(
        selectedFormat, extension);
    fileChooser.setFileFilter(filter);

    // Set default file name
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String defaultFileName = selectedType + "_" + sdf.format(fromDate) + "_to_" + sdf.format(toDate) + "." + extension;
    fileChooser.setSelectedFile(new File(defaultFileName));

    int result = fileChooser.showSaveDialog(this);
    if (result != JFileChooser.APPROVE_OPTION) {
      return; // User canceled
    }

    // Ensure file has correct extension
    final File selectedFile;
    if (!fileChooser.getSelectedFile().getName().endsWith("." + extension)) {
      selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath() + "." + extension);
    } else {
      selectedFile = fileChooser.getSelectedFile();
    }

    // Show progress dialog
    JDialog progressDialog = new JDialog(
        (Frame) SwingUtilities.getWindowAncestor(this),
        "Exporting Data",
        true);
    progressDialog.setLayout(new BorderLayout());
    JProgressBar progressBar = new JProgressBar();
    progressBar.setIndeterminate(true);
    JLabel statusLabel = new JLabel("Preparing export...", SwingConstants.CENTER);
    progressDialog.add(statusLabel, BorderLayout.NORTH);
    progressDialog.add(progressBar, BorderLayout.CENTER);
    progressDialog.setSize(300, 100);
    progressDialog.setLocationRelativeTo(this);

    // Create a worker thread for the export
    SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
      @Override
      protected Boolean doInBackground() throws Exception {
        String title = selectedType + " Report (" + sdf.format(fromDate) + " to " + sdf.format(toDate) + ")";
        String[] columnNames = getColumnNames(selectedType);
        boolean success = false;

        try {
          // Get the data first
          List<Map<String, Object>> data = dataPersistenceService.getExportData(selectedType, fromDate, toDate);

          if (data.isEmpty()) {
            publish("No data found for the selected date range");
            return false;
          }

          publish("Retrieved " + data.size() + " records. Creating export file...");

          // Perform the export
          if (selectedFormat.contains("Excel")) {
            success = dataPersistenceService.exportToExcel(
                data,
                selectedFile.getAbsolutePath(),
                selectedType,
                columnNames);
          } else {
            success = dataPersistenceService.exportToPDF(
                data,
                selectedFile.getAbsolutePath(),
                title,
                columnNames);
          }

          return success;
        } catch (Exception e) {
          e.printStackTrace();
          publish("Error: " + e.getMessage());
          return false;
        }
      }

      @Override
      protected void process(List<String> chunks) {
        // Update the status label with the latest message
        if (!chunks.isEmpty()) {
          statusLabel.setText(chunks.get(chunks.size() - 1));
        }
      }

      @Override
      protected void done() {
        progressDialog.dispose();
        try {
          boolean success = get();
          if (success) {
            JOptionPane.showMessageDialog(
                DataPersistenceView.this,
                "Export completed successfully.\nFile saved to: " + selectedFile.getAbsolutePath(),
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE);
          } else {
            String errorMsg = statusLabel.getText();
            if (errorMsg.startsWith("No data found")) {
              JOptionPane.showMessageDialog(
                  DataPersistenceView.this,
                  "No data available for export in the selected date range.\n" +
                      "Please try a different date range.",
                  "No Data Available",
                  JOptionPane.WARNING_MESSAGE);
            } else {
              JOptionPane.showMessageDialog(
                  DataPersistenceView.this,
                  "Failed to export data: " + errorMsg + "\n" +
                      "Please check the logs for more details.",
                  "Export Error",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(
              DataPersistenceView.this,
              "An error occurred during export: " + e.getMessage() + "\n" +
                  "Please check the logs for more details.",
              "Export Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    };

    // Start the worker and show the progress dialog
    worker.execute();
    progressDialog.setVisible(true);
  }

  private String[] getColumnNames(String dataType) {
    switch (dataType) {
      case "Events":
        return new String[] { "Event Name", "Date", "Team A", "Team B", "Category", "Event Type", "Description" };
      case "Tickets":
        return new String[] { "Event ID", "Ticket Type", "Date", "Price", "Status" };
      case "Sales Report":
        return new String[] { "Date", "Event", "Team A", "Team B", "Tickets Sold", "Revenue" };
      default:
        return new String[] { "Column 1", "Column 2", "Column 3" };
    }
  }

  private void handleManageBackups() {
    // Generate a backup name based on the current timestamp
    String backupName = "backup_" + System.currentTimeMillis();

    // Show a dialog to let the user customize the backup name
    String customName = JOptionPane.showInputDialog(
        this,
        "Enter a name for this backup:",
        backupName);

    // If user provided a name, create the backup
    if (customName != null && !customName.trim().isEmpty()) {
      // Show a progress dialog
      JDialog progressDialog = new JDialog();
      progressDialog.setTitle("Creating Backup");
      progressDialog.setLayout(new BorderLayout());
      progressDialog.setSize(300, 100);
      progressDialog.setLocationRelativeTo(this);

      JLabel progressLabel = new JLabel("Creating backup... Please wait.", JLabel.CENTER);
      progressDialog.add(progressLabel, BorderLayout.CENTER);

      // Show the dialog on a separate thread to avoid blocking UI
      SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));

      // Create the backup on a background thread
      new Thread(() -> {
        // Perform the backup
        BackupResult result = dataPersistenceService.createBackup(customName);

        // Close the progress dialog
        SwingUtilities.invokeLater(() -> progressDialog.dispose());

        // Show the result message
        if (result.isSuccess()) {
          SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                result.getMessage(),
                "Backup Created",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh the backup history
            refreshBackupHistory();
          });
        } else {
          SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                this,
                "Failed to create backup: " + result.getMessage(),
                "Backup Error",
                JOptionPane.ERROR_MESSAGE);
          });
        }
      }).start();
    }
  }
}
