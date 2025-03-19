package server;

import database.QueryBuilder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class BookingService {

    private QueryBuilder queryBuilder;

    // Constructor initializes the QueryBuilder
    public BookingService() {
        queryBuilder = new QueryBuilder(); // Initializes QueryBuilder for database operations
    }

    // Method to add a customer booking event to the database
    public void addBooking(String customerName, String selectedEvent, String selectedPriceCategory, int customerId, int eventId, String ticketType) {
        // Prepare the data to be inserted
        Map<String, Object> ticketValues = new HashMap<>();  // Use ticketValues map
        
        // Assuming the price category corresponds to a price value
        double price = getPriceFromCategory(selectedPriceCategory);

        // Set ticket details
        ticketValues.put("customer-id", customerId); // customer ID
        ticketValues.put("event-id", eventId); // event ID
        ticketValues.put("ticket-type", ticketType); // ticket type
        ticketValues.put("ticket-date", new Timestamp(System.currentTimeMillis()));  // Current date for ticket date
        ticketValues.put("ticket-status", "Booked"); // status set as "Booked"
        ticketValues.put("price", price);  // Price based on selected category
        ticketValues.put("created-at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for created_at
        ticketValues.put("updated-at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for updated_at

        // Debugging: Print ticket details before insertion
        System.out.println("Inserting booking with data: " + ticketValues);

        // Call the QueryBuilder to insert the booking into the Ticket table
        queryBuilder.insert("Ticket", ticketValues);  // Insert using ticketValues

        System.out.println("Booking added to the database successfully!");
    }

    // Helper method to determine the price based on the price category
    private double getPriceFromCategory(String priceCategory) {
        switch (priceCategory) {
            case "VIP - $100":
                return 100.0;
            case "Premium - $75":
                return 75.0;
            case "Standard - $50":
                return 50.0;
            default:
                return 0.0;  // Default case in case of invalid category
        }
    }
}
