package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;

public class DataPersistenceView extends JPanel {
  private JPanel mainPanel;

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
    mainPanel.setBackground(new Color(240, 240, 240));

    // Create header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    headerPanel.setBackground(new Color(64, 143, 224));
    headerPanel.setPreferredSize(new Dimension(600, 50));

    JLabel headerLabel = new JLabel("Data Persistence Import/Export");
    headerLabel.setForeground(new Color(240, 240, 255));
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);

    // Create content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(new Color(240, 240, 240));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    // Add "Coming Soon" label
    JLabel comingSoonLabel = new JLabel("Data Persistence Import/Export - Coming Soon");
    comingSoonLabel.setFont(new Font("Arial", Font.BOLD, 16));
    comingSoonLabel.setForeground(new Color(90, 90, 90));
    contentPanel.add(comingSoonLabel);

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
  }
}