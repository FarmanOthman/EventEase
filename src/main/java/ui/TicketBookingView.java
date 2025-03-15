package ui;

import javax.swing.*;
import java.awt.*;
import ui.components.RoundedButton;
import ui.components.RoundedTextField;

public class TicketBookingView {
    public static void main(String[] args) {
        // Set the screen size to 1920x1080
        int screenWidth = 1920;
        int screenHeight = 1080;

        JFrame frame = new JFrame("Ticket Booking System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(screenWidth, screenHeight);
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.getContentPane().setBackground(new Color(230, 230, 230));
        frame.setLayout(null);

        // Calculate the center position for the bookingBox
        int bookingBoxWidth = 500;  // Width of the booking panel
        int bookingBoxHeight = 520; // Height of the booking panel
        int bookingBoxX = (screenWidth - bookingBoxWidth) / 2; // Center horizontally
        int bookingBoxY = (screenHeight - bookingBoxHeight) / 4; // Adjust vertical position

        JPanel bookingBox = new JPanel();
        bookingBox.setLayout(null);
        bookingBox.setBounds(bookingBoxX, bookingBoxY, bookingBoxWidth, bookingBoxHeight);
        bookingBox.setBackground(Color.white);
        bookingBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        bookingBox.setOpaque(true);
        frame.add(bookingBox);

        JLabel titleLabel = new JLabel("Ticket Booking", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(30, 100, 180));
        titleLabel.setBounds(0, 20, bookingBoxWidth, 40);
        bookingBox.add(titleLabel);

        String[] labels = {"Name", "Category (VIP/Regular)", "Date (DD/MM/YYYY)", "Phone Number"};
        int y = 80;
        RoundedTextField[] fields = new RoundedTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setBounds(50, y, 400, 25);
            bookingBox.add(label);

            fields[i] = new RoundedTextField(25);
            fields[i].setBounds(50, y + 30, 400, 40);
            fields[i].setBackground(new Color(245, 245, 245));
            fields[i].setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
            bookingBox.add(fields[i]);
            y += 80;
        }

        RoundedButton bookButton = new RoundedButton("Book Now", 25);
        bookButton.setBounds(150, y, 200, 50);
        bookButton.setBackground(new Color(30, 100, 180));
        bookButton.setForeground(Color.white);
        bookingBox.add(bookButton);

        // Calculate the center position for the receiptBox
        int receiptBoxWidth = 500;  // Width of the receipt panel
        int receiptBoxHeight = 160; // Height of the receipt panel
        int receiptBoxX = (screenWidth - receiptBoxWidth) / 2; // Center horizontally
        int receiptBoxY = bookingBoxY + bookingBoxHeight + 40; // Place below bookingBox with spacing

        JPanel receiptBox = new JPanel();
        receiptBox.setLayout(null);
        receiptBox.setBounds(receiptBoxX, receiptBoxY, receiptBoxWidth, receiptBoxHeight);
        receiptBox.setBackground(Color.white);
        receiptBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        frame.add(receiptBox);

        JLabel receiptTitle = new JLabel("Booking Receipt", SwingConstants.CENTER);
        receiptTitle.setFont(new Font("Arial", Font.BOLD, 22));
        receiptTitle.setForeground(new Color(30, 100, 180));
        receiptTitle.setBounds(0, 10, receiptBoxWidth, 30);
        receiptBox.add(receiptTitle);

        // Modify the receipt label to match the requested format
        JLabel ticketLabel = new JLabel("Ticket: Match A | Category: VIP | Date: 02/04/2025 | Name: John Doe", SwingConstants.CENTER);
        ticketLabel.setBounds(50, 50, 400, 50);
        ticketLabel.setForeground(Color.darkGray);
        ticketLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        receiptBox.add(ticketLabel);

        RoundedButton printButton = new RoundedButton("Print", 20);
        printButton.setBounds(150, 110, 100, 40);
        printButton.setBackground(new Color(0, 102, 204));
        printButton.setForeground(Color.white);
        receiptBox.add(printButton);

        RoundedButton editButton = new RoundedButton("Edit", 20);
        editButton.setBounds(270, 110, 100, 40);
        editButton.setBackground(new Color(0, 153, 51));
        editButton.setForeground(Color.white);
        receiptBox.add(editButton);

        frame.setVisible(true);
    }
}
