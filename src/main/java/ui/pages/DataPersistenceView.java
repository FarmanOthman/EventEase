package ui.pages;

import ui.components.Sidebar;
import ui.components.RoundedButton;
import ui.Refreshable;
import services.DataPersistenceService;
import services.DataPersistenceService.BackupInfo;
import services.DataPersistenceService.ImportResult;

import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SpinnerDateModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
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
    panel.setMaximumSize(new Dimension(800, 120));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Title
    JLabel title = new JLabel("Database Backup");
    title.setFont(new Font("Arial", Font.BOLD, 16));
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Settings panel
    JPanel settingsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    settingsPanel.setBackground(Color.WHITE);

    JLabel descriptionLabel = new JLabel("Create a backup of your database to prevent data loss:");
    descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));

    JButton createBackupButton = createStyledButton("Create Backup Now", new Color(64, 133, 219));
    createBackupButton.addActionListener(e -> handleManageBackups());

    settingsPanel.add(descriptionLabel);
    settingsPanel.add(Box.createHorizontalStrut(20));
    settingsPanel.add(createBackupButton);

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

    // Create button panel for restore and delete
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
    buttonPanel.setOpaque(false);
    
    // Add restore button 
    RoundedButton restoreButton = new RoundedButton("Restore", 25);
    restoreButton.setBackground(new Color(64, 133, 219));
    restoreButton.setFont(new Font("Arial", Font.BOLD, 14));
    restoreButton.setForeground(Color.white);
    restoreButton.setPreferredSize(new Dimension(80, 25));
    restoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    restoreButton.addActionListener(e -> {
      int option = JOptionPane.showConfirmDialog(
          DataPersistenceView.this,
          "Are you sure you want to restore this backup?\nThis will replace your current database.",
          "Confirm Restore",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
        // Show loading message
        JDialog loadingDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(DataPersistenceView.this), "Restoring Database", true);
        loadingDialog.setLayout(new BorderLayout());
        JLabel loadingLabel = new JLabel("Restoring database from backup, please wait...", JLabel.CENTER);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(DataPersistenceView.this);
        
        // Run the restore operation in a background thread to not freeze the UI
        SwingWorker<DataPersistenceService.BackupResult, Void> worker = new SwingWorker<>() {
            @Override
            protected DataPersistenceService.BackupResult doInBackground() {
                return dataPersistenceService.restoreDatabase(backup.getPath());
            }
            
            @Override
            protected void done() {
                loadingDialog.dispose();
                try {
                    DataPersistenceService.BackupResult result = get();
                    if (result.isSuccess()) {
                        JOptionPane.showMessageDialog(
                            DataPersistenceView.this,
                            result.getMessage(),
                            "Restore Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh the UI after successful restore
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(
                            DataPersistenceView.this,
                            "Restore failed: " + result.getMessage(),
                            "Restore Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        DataPersistenceView.this,
                        "An unexpected error occurred: " + e.getMessage(),
                        "Restore Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        // Start the worker and show the loading dialog
        worker.execute();
        loadingDialog.setVisible(true);
      }
    });
    
    // Add delete button
    RoundedButton deleteButton = new RoundedButton("Delete", 25);
    deleteButton.setBackground(new Color(220, 53, 69));
    deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
    deleteButton.setForeground(Color.white);
    deleteButton.setPreferredSize(new Dimension(80, 25));
    deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    deleteButton.addActionListener(e -> {
      int option = JOptionPane.showConfirmDialog(
          DataPersistenceView.this,
          "Are you sure you want to delete this backup?\nThis cannot be undone.",
          "Confirm Delete",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.WARNING_MESSAGE);
      
      if (option == JOptionPane.YES_OPTION) {
        // Delete the backup file
        File backupFile = new File(backup.getPath());
        boolean deleted = backupFile.delete();
        
        if (deleted) {
          JOptionPane.showMessageDialog(
              DataPersistenceView.this,
              "Backup deleted successfully.",
              "Delete Successful",
              JOptionPane.INFORMATION_MESSAGE);
          
          // Refresh the backup history
          refreshBackupHistory();
        } else {
          JOptionPane.showMessageDialog(
              DataPersistenceView.this,
              "Failed to delete the backup file. It may be in use or protected.",
              "Delete Failed",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    
    buttonPanel.add(restoreButton);
    buttonPanel.add(deleteButton);
    
    itemPanel.add(buttonPanel, BorderLayout.EAST);

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
    }    // Step 1: Select what to export
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
        
        // Get the column names for the selected type
        String[] columnNames = getColumnNames(selectedType);
        
        // Get the data to export
        List<Map<String, Object>> data = dataPersistenceService.getExportData(selectedType, fromDate, toDate);
        
        boolean success = false;
        if (selectedFormat.contains("Excel")) {
          success = dataPersistenceService.exportToExcel(data, selectedFile.getAbsolutePath(), selectedType, columnNames);
        } else {
          success = dataPersistenceService.exportToPDF(data, selectedFile.getAbsolutePath(), title, columnNames);
        }
        
        return success;
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
            JOptionPane.showMessageDialog(
                DataPersistenceView.this,
                "Export failed. Please check logs for details.",
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
          }
        } catch (Exception e) {
          JOptionPane.showMessageDialog(
              DataPersistenceView.this,
              "Error during export: " + e.getMessage(),
              "Export Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    };
    
    // Start the worker and show the progress dialog
    worker.execute();
    progressDialog.setVisible(true);
  }

  private String[] getColumnNames(String type) {
    switch (type) {
      case "Events":
        return new String[] {"ID", "Event Name", "Date", "Description", "Team A", "Team B", "Category", "Event Type"};
      case "Tickets":
        return new String[] {"ID", "Event ID", "Ticket Type", "Date", "Price", "Status"};
      case "Sales Report":
        return new String[] {"ID", "Date", "Tickets Sold", "Revenue", "Category"};
      default:
        return new String[0];
    }
  }

  private void handleManageBackups() {
    // Ask for backup name
    String backupName = JOptionPane.showInputDialog(
        this,
        "Enter a name for this backup (or leave blank for timestamp name):",
        "Create Backup",
        JOptionPane.QUESTION_MESSAGE);
    
    // If user canceled, return
    if (backupName == null) {
      return;
    }
    
    // Create a backup with the provided name
    DataPersistenceService.BackupResult result = dataPersistenceService.createBackup(backupName);
    
    // Show result to user
    if (result.isSuccess()) {
      JOptionPane.showMessageDialog(
          this,
          result.getMessage(),
          "Backup Successful",
          JOptionPane.INFORMATION_MESSAGE);
      
      // Refresh backup history display
      refreshBackupHistory();
    } else {
      JOptionPane.showMessageDialog(
          this,
          "Backup failed: " + result.getMessage(),
          "Backup Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
