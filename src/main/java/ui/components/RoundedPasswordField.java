package ui.components;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPasswordField extends JPasswordField {
  private int radius;

  public RoundedPasswordField(int radius) {
    super();
    this.radius = radius;
    setOpaque(false); // Make the text field transparent
    setBorder(BorderFactory.createEmptyBorder()); // Remove default border
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
    super.paintComponent(g);
  }

  @Override
  public void setBorder(Border border) {
    // Prevent setting a border
  }
}