package ui.pages;

import ui.components.Sidebar;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CalendarView extends JPanel {
  private JPanel mainPanel;

  public CalendarView() {
    setLayout(new BorderLayout());

    // Use the Sidebar component
    add(new Sidebar(), BorderLayout.WEST);

    // Create main panel
    createMainPanel();

    // Add main panel to this panel
    add(mainPanel, BorderLayout.CENTER);
  }

  private void createMainPanel() {
    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBackground(new Color(240, 240, 240));

    // Create header panel with rounded corners
    JPanel headerPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
        g2.dispose();
      }
    };
    headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    headerPanel.setBackground(new Color(64, 143, 224));
    headerPanel.setPreferredSize(new Dimension(600, 50));

    JLabel headerLabel = new JLabel("Calendar");
    headerLabel.setForeground(new Color(240, 240, 255));
    headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
    headerPanel.add(headerLabel);

    // Create content panel
    JPanel contentPanel = new JPanel();
    contentPanel.setBackground(new Color(240, 240, 240));
    contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

    // Add "Coming Soon" label
    JLabel comingSoonLabel = new JLabel("Calendar View - Coming Soon");
    comingSoonLabel.setFont(new Font("Arial", Font.BOLD, 16));
    comingSoonLabel.setForeground(new Color(90, 90, 90));
    contentPanel.add(comingSoonLabel);

    // Add panels to main panel
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(contentPanel, BorderLayout.CENTER);
  }
}