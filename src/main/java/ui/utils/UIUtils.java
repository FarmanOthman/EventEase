package ui.utils;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class providing helper methods for UI components
 */
public class UIUtils {

  /**
   * Creates a round border button with specified colors and text
   * 
   * @param text            Button text
   * @param backgroundColor Button background color
   * @param textColor       Button text color
   * @return A styled JButton
   */
  public static JButton createRoundedButton(String text, Color backgroundColor, Color textColor) {
    JButton button = new JButton(text) {
      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        g2.setColor(textColor);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(getText())) / 2;
        int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
        g2.drawString(getText(), x, y);
        g2.dispose();
      }
    };

    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

    return button;
  }

  /**
   * Create a styled panel with a title and optional border
   * 
   * @param title           Panel title
   * @param backgroundColor Panel background color
   * @param withBorder      Whether to add a border to the panel
   * @return A styled JPanel
   */
  public static JPanel createTitledPanel(String title, Color backgroundColor, boolean withBorder) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(backgroundColor);

    if (withBorder) {
      panel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(new Color(200, 200, 200)),
          BorderFactory.createEmptyBorder(10, 10, 10, 10)));
    } else {
      panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    if (title != null && !title.isEmpty()) {
      JLabel titleLabel = new JLabel(title);
      titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
      titleLabel.setForeground(Color.WHITE);
      panel.add(titleLabel, BorderLayout.NORTH);
    }

    return panel;
  }

  /**
   * Create a custom cell renderer for JTable
   * 
   * @param alternateRowColor Color for alternate rows (null for default)
   * @param selectedBgColor   Background color when selected (null for default)
   * @param numberColumns     Array of column indices that should be right-aligned
   * @return A DefaultTableCellRenderer
   */
  public static DefaultCellRenderer createTableCellRenderer(Color alternateRowColor,
      Color selectedBgColor,
      int[] numberColumns) {
    return new DefaultCellRenderer(alternateRowColor, selectedBgColor, numberColumns);
  }

  /**
   * Custom DefaultTableCellRenderer with formatting options
   */
  public static class DefaultCellRenderer extends DefaultListCellRenderer {
    private Color alternateRowColor;
    private Color selectedBgColor;

    public DefaultCellRenderer(Color alternateRowColor, Color selectedBgColor, int[] numberColumns) {
      this.alternateRowColor = alternateRowColor;
      this.selectedBgColor = selectedBgColor;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
        int index, boolean isSelected,
        boolean cellHasFocus) {
      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

      // Alternate row coloring
      if (alternateRowColor != null && index % 2 == 0) {
        c.setBackground(alternateRowColor);
      }

      // Selection coloring
      if (isSelected && selectedBgColor != null) {
        c.setBackground(selectedBgColor);
      }

      return c;
    }
  }
}