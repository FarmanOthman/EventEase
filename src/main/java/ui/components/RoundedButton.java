package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedButton extends JButton {
  private int radius;

  public RoundedButton(String text, int radius) {
    super(text);
    this.radius = radius;
    setContentAreaFilled(false); // Make the button transparent
    setFocusPainted(false); // Remove the focus border
    setBorderPainted(false); // Remove the border
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setColor(getBackground());
    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
    super.paintComponent(g);
  }
}