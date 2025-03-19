package server;

import database.QueryBuilder;
import java.util.HashMap;
import java.util.Map;

public class TicketService {

    // Instance of QueryBuilder to interact with the database
    private QueryBuilder queryBuilder;
    private TicketManager ticketManager;
    private PricingService pricingService;
    private PaymentProcessor paymentProcessor;

    public TicketService() {
        this.queryBuilder = new QueryBuilder();
        this.ticketManager = new TicketManager();
        this.pricingService = new PricingService();
        this.paymentProcessor = new PaymentProcessor();
    }

    // Method to create a booking and insert it into the database
    public String createBooking(String customerName, String event, String priceCategory) {
        // Get the ticket price based on the selected price category
        double price = pricingService.getPrice(priceCategory);

        // Check availability for the event
        boolean isAvailable = ticketManager.checkAvailability(event);

        if (!isAvailable) {
            return "Sorry, no tickets available for this event.";
        }

        // Create the ticket object
        Ticket ticket = new Ticket(customerName, event, price);

        // Store the ticket information into the database using QueryBuilder
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("customerName", customerName);
        bookingData.put("event", event);
        bookingData.put("priceCategory", priceCategory);
        bookingData.put("price", price);

        // Insert data into the bookings table
        queryBuilder.insert("bookings", bookingData);

        // Book the ticket and return a success message
        ticketManager.bookTicket(ticket);

        return "Booking confirmed for " + customerName + " to attend " + event + " with price " + priceCategory;
    }

    // Method to process payment
    public String processPayment(double amount) {
        boolean paymentSuccessful = paymentProcessor.processPayment(amount);

        if (paymentSuccessful) {
            return "Payment processed successfully.";
        } else {
            return "Payment failed. Please try again.";
        }
    }

    // Inner class to manage tickets (availability, booking)
    public class TicketManager {

        public boolean checkAvailability(String event) {
            // In a real scenario, check the availability from the database or inventory
            // For now, let's assume tickets are available for all events
            return true;
        }

        public void bookTicket(Ticket ticket) {
            // In a real scenario, store the booking in the database or inventory
            System.out.println("Ticket booked: " + ticket);
        }
    }

    // Inner class to manage pricing
    public class PricingService {

        // Get the price for a specific category
        public double getPrice(String priceCategory) {
            switch (priceCategory) {
                case "VIP - $100":
                    return 100.0;
                case "Premium - $75":
                    return 75.0;
                case "Standard - $50":
                    return 50.0;
                default:
                    return 0.0; // Default if price category is not recognized
            }
        }
    }

    // Inner class representing a Ticket for a booking
    public class Ticket {
        private String customerName;
        private String event;
        private double price;

        public Ticket(String customerName, String event, double price) {
            this.customerName = customerName;
            this.event = event;
            this.price = price;
        }

        @Override
        public String toString() {
            return "Ticket [customerName=" + customerName + ", event=" + event + ", price=" + price + "]";
        }

        // Getter and Setter methods for each field
        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }

    // Inner class for payment processing
    public class PaymentProcessor {

        public boolean processPayment(double amount) {
            // In a real scenario, integrate with a payment gateway
            System.out.println("Processing payment of $" + amount);
            return true; // Assume payment is successful for now
        }
    }
}
