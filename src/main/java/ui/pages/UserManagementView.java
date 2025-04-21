package ui.pages;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;

import ui.Refreshable;
import ui.components.Sidebar;
import server.AuthenticationService;
import server.AuthenticationService.UserRole;
import services.UserService;

public class UserManagementView extends JPanel implements Refreshable {
  private JPanel mainPanel, contentPanel;
  private JTable adminTable;
  private DefaultTableModel tableModel;
  private UserService userService;

  public UserManagementView() {
    setLayout(new BorderLayout());

    // Get the user service instance
    userService = UserService.getInstance();

    // Add the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Initialize main components
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);
  }

  @Override
  public void refresh() {
    loadUserData();
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

    // Get current user role for personalized header
    UserRole userRole = AuthenticationService.getCurrentUserRole();
    String roleText = (userRole == UserRole.ADMIN) ? "Admin" : "Manager";

    JLabel headerLabel = new JLabel("User Management");
    headerLabel.setForeground(Color.WHITE);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);

    mainPanel.add(headerPanel, BorderLayout.NORTH);
  }

  private void createAdminList() {
    // Get current user role
    UserRole userRole = AuthenticationService.getCurrentUserRole();

    // Create the outer panel with rounded corners
    RoundedPanel listPanel = new RoundedPanel(new BorderLayout(), 15);
    listPanel.setBackground(Color.WHITE);
    listPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    // Admin List title
    JLabel listTitle = new JLabel("User List");
    listTitle.setFont(new Font("Arial", Font.BOLD, 16));
    listTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    listPanel.add(listTitle, BorderLayout.NORTH);

    // Create table model with columns
    String[] columns = { "Username", "Email", "Role", "Actions" };
    tableModel = new DefaultTableModel() {
      @Override
      public boolean isCellEditable(int row, int column) {
        return column == 3; // Only make the Actions column editable
      }
    };

    for (String column : columns) {
      tableModel.addColumn(column);
    }

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
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
          boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (column == 3) {
          return createActionPanel(row);
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

    // Create panel for the Add New User button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.setBackground(Color.WHITE);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

    // Create custom button with rounded corners
    RoundedButton addNewButton = new RoundedButton("+ Add New User", new Color(39, 174, 96));
    addNewButton.setPreferredSize(new Dimension(150, 40));
    addNewButton.addActionListener(e -> showAddUserDialog());
    buttonPanel.add(addNewButton);

    // Add panels to content panel
    contentPanel.add(listPanel, BorderLayout.CENTER);
    contentPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Load user data
    loadUserData();
  }

  /**
   * Load user data from the database via UserService
   */
  private void loadUserData() {
    // Clear existing data
    tableModel.setRowCount(0);

    // Load admin users
    List<Map<String, Object>> adminUsers = userService.getAllUsers(UserRole.ADMIN);
    for (Map<String, Object> user : adminUsers) {
      tableModel.addRow(new Object[] {
          user.get("username"),
          user.get("email"),
          "Admin",
          "" // Actions column handled by renderer
      });
    }

    // Load manager users
    List<Map<String, Object>> managerUsers = userService.getAllUsers(UserRole.MANAGER);
    for (Map<String, Object> user : managerUsers) {
      tableModel.addRow(new Object[] {
          user.get("username"),
          user.get("email"),
          "Manager",
          "" // Actions column handled by renderer
      });
    }
  }

  /**
   * Show dialog to add a new user
   */
  private void showAddUserDialog() {
    JDialog dialog = new JDialog();
    dialog.setTitle("Add New User");
    dialog.setSize(400, 300);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo(this);
    dialog.setModal(true);

    JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel usernameLabel = new JLabel("Username:");
    JTextField usernameField = new JTextField(20);

    JLabel passwordLabel = new JLabel("Password:");
    JPasswordField passwordField = new JPasswordField(20);

    JLabel emailLabel = new JLabel("Email:");
    JTextField emailField = new JTextField(20);

    JLabel roleLabel = new JLabel("Role:");
    String[] roles = { "Admin", "Manager" };
    JComboBox<String> roleComboBox = new JComboBox<>(roles);

    formPanel.add(usernameLabel);
    formPanel.add(usernameField);
    formPanel.add(passwordLabel);
    formPanel.add(passwordField);
    formPanel.add(emailLabel);
    formPanel.add(emailField);
    formPanel.add(roleLabel);
    formPanel.add(roleComboBox);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());

    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> {
      String username = usernameField.getText().trim();
      String password = new String(passwordField.getPassword());
      String email = emailField.getText().trim();
      UserRole role = roleComboBox.getSelectedItem().equals("Admin") ? UserRole.ADMIN : UserRole.MANAGER;

      if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      boolean success = userService.addUser(username, password, email, role);
      if (success) {
        dialog.dispose();
        loadUserData(); // Refresh data
        JOptionPane.showMessageDialog(this, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(dialog, "Failed to add user. Username may already exist.", "Error",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  /**
   * Show dialog to edit a user
   */
  private void showEditUserDialog(String username, String email, String role) {
    JDialog dialog = new JDialog();
    dialog.setTitle("Edit User");
    dialog.setSize(400, 300);
    dialog.setLayout(new BorderLayout());
    dialog.setLocationRelativeTo(this);
    dialog.setModal(true);

    JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel usernameLabel = new JLabel("Username:");
    JTextField usernameField = new JTextField(username);
    usernameField.setEditable(false); // Username cannot be changed

    JLabel passwordLabel = new JLabel("New Password (optional):");
    JPasswordField passwordField = new JPasswordField();

    JLabel emailLabel = new JLabel("Email:");
    JTextField emailField = new JTextField(email);

    formPanel.add(usernameLabel);
    formPanel.add(usernameField);
    formPanel.add(passwordLabel);
    formPanel.add(passwordField);
    formPanel.add(emailLabel);
    formPanel.add(emailField);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> dialog.dispose());

    JButton saveButton = new JButton("Save");
    saveButton.addActionListener(e -> {
      String newEmail = emailField.getText().trim();
      String newPassword = new String(passwordField.getPassword());
      UserRole userRole = role.equals("Admin") ? UserRole.ADMIN : UserRole.MANAGER;

      if (newEmail.isEmpty()) {
        JOptionPane.showMessageDialog(dialog, "Email is required", "Error", JOptionPane.ERROR_MESSAGE);
        return;
      }

      // Only set password if it's not empty
      if (newPassword.isEmpty()) {
        newPassword = null;
      }

      boolean success = userService.updateUser(username, newEmail, newPassword, userRole);
      if (success) {
        dialog.dispose();
        loadUserData(); // Refresh data
        JOptionPane.showMessageDialog(this, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
      } else {
        JOptionPane.showMessageDialog(dialog, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
      }
    });

    buttonPanel.add(cancelButton);
    buttonPanel.add(saveButton);

    dialog.add(formPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
  }

  private JPanel createActionPanel(int row) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
    panel.setBackground(Color.WHITE);

    // Both roles have full access with View, Edit, Delete buttons
    RoundedButton viewButton = new RoundedButton("View", new Color(66, 133, 244));
    viewButton.setPreferredSize(new Dimension(60, 25));

    RoundedButton editButton = new RoundedButton("Edit", new Color(242, 153, 0));
    editButton.setPreferredSize(new Dimension(60, 25));

    RoundedButton deleteButton = new RoundedButton("Delete", new Color(231, 76, 60));
    deleteButton.setPreferredSize(new Dimension(60, 25));

    panel.add(viewButton);
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
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
      setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
      removeAll();

      // Both roles have full access with View, Edit, Delete buttons
      RoundedButton viewButton = new RoundedButton("View", new Color(66, 133, 244));
      viewButton.setPreferredSize(new Dimension(60, 25));

      RoundedButton editButton = new RoundedButton("Edit", new Color(242, 153, 0));
      editButton.setPreferredSize(new Dimension(60, 25));

      RoundedButton deleteButton = new RoundedButton("Delete", new Color(231, 76, 60));
      deleteButton.setPreferredSize(new Dimension(60, 25));

      add(viewButton);
      add(editButton);
      add(deleteButton);

      return this;
    }
  }

  class ButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
    private JPanel panel;

    public ButtonEditor() {
      panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

      // Both roles have full access
      RoundedButton viewButton = new RoundedButton("View", new Color(66, 133, 244));
      viewButton.setPreferredSize(new Dimension(60, 25));
      viewButton.addActionListener(e -> {
        int row = adminTable.getSelectedRow();
        if (row >= 0) {
          String username = (String) adminTable.getValueAt(row, 0);
          String email = (String) adminTable.getValueAt(row, 1);
          String role = (String) adminTable.getValueAt(row, 2);
          JOptionPane.showMessageDialog(null,
              "User Details:\nUsername: " + username +
                  "\nEmail: " + email +
                  "\nRole: " + role,
              "User Details",
              JOptionPane.INFORMATION_MESSAGE);
        }
        fireEditingStopped();
      });

      RoundedButton editButton = new RoundedButton("Edit", new Color(242, 153, 0));
      editButton.setPreferredSize(new Dimension(60, 25));
      editButton.addActionListener(e -> {
        int row = adminTable.getSelectedRow();
        if (row >= 0) {
          String username = (String) adminTable.getValueAt(row, 0);
          String email = (String) adminTable.getValueAt(row, 1);
          String role = (String) adminTable.getValueAt(row, 2);
          showEditUserDialog(username, email, role);
        }
        fireEditingStopped();
      });

      RoundedButton deleteButton = new RoundedButton("Delete", new Color(231, 76, 60));
      deleteButton.setPreferredSize(new Dimension(60, 25));
      deleteButton.addActionListener(e -> {
        int row = adminTable.getSelectedRow();
        if (row >= 0) {
          String username = (String) adminTable.getValueAt(row, 0);
          String role = (String) adminTable.getValueAt(row, 2);
          UserRole userRole = role.equals("Admin") ? UserRole.ADMIN : UserRole.MANAGER;

          int option = JOptionPane.showConfirmDialog(null,
              "Are you sure you want to delete user '" + username + "'?",
              "Confirm Delete",
              JOptionPane.YES_NO_OPTION);

          if (option == JOptionPane.YES_OPTION) {
            boolean success = userService.deleteUser(username, userRole);
            if (success) {
              loadUserData(); // Refresh data
              JOptionPane.showMessageDialog(null, "User deleted successfully", "Success",
                  JOptionPane.INFORMATION_MESSAGE);
            } else {
              JOptionPane.showMessageDialog(null, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
        fireEditingStopped();
      });

      panel.add(viewButton);
      panel.add(editButton);
      panel.add(deleteButton);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
        int column) {
      panel.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
      return panel;
    }

    @Override
    public Object getCellEditorValue() {
      return "";
    }
  }
}