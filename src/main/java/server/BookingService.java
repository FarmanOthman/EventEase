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
    public boolean addBooking(String customerName, String selectedEvent, String selectedPriceCategory, int customerId,
            int eventId, String ticketType) {
        // Validate all inputs
        if (!validateBookingInputs(customerName, selectedEvent, selectedPriceCategory, customerId, eventId,
                ticketType)) {
            System.out.println("Invalid booking inputs detected");
            return false;
        }

        // Prepare the data to be inserted
        Map<String, Object> ticketValues = new HashMap<>();

        // Assuming the price category corresponds to a price value
        double price = getPriceFromCategory(selectedPriceCategory);

        // Set ticket details - use the actual column names from the database schema
        ticketValues.put("event_id", eventId);
        ticketValues.put("customer_id", customerId);
        ticketValues.put("ticket_type", ticketType);
        ticketValues.put("ticket_date", new Timestamp(System.currentTimeMillis()));
        ticketValues.put("ticket_status", "Sold"); // Using the correct value "Sold" from schema CHECK constraint
        ticketValues.put("price", price);

        // Note: created_at and updated_at have DEFAULT CURRENT_TIMESTAMP in the
        // database so we don't need to explicitly set them

        // Debugging: Print ticket details before insertion
        System.out.println("Inserting booking with data: " + ticketValues);

        try {
            // Call the QueryBuilder to insert the booking into the Ticket table
            queryBuilder.insert("Ticket", ticketValues);
            System.out.println("Booking added to the database successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Failed to add booking to the database: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates all booking input parameters
     */
    private boolean validateBookingInputs(String customerName, String selectedEvent, String selectedPriceCategory,
            int customerId, int eventId, String ticketType) {
        // Check if strings are valid
        if (!InputValidator.isValidString(customerName) ||
                !InputValidator.isValidString(selectedEvent) ||
                !InputValidator.isValidString(ticketType)) {
            return false;
        }

        // Validate price category
        if (!InputValidator.isValidPriceCategory(selectedPriceCategory)) {
            return false;
        }

        // Validate customer and event IDs
        if (!InputValidator.isPositiveInteger(customerId) ||
                !InputValidator.isPositiveInteger(eventId)) {
            return false;
        }

        // Validate ticket type - make sure it matches the schema constraints (Regular
        // or VIP)
        if (!ticketType.equals("Regular") && !ticketType.equals("VIP")) {
            System.out.println("Invalid ticket type: " + ticketType + ". Must be 'Regular' or 'VIP'");
            return false;
        }

        return true;
    }

    // Helper method to determine the price based on the price category
    private double getPriceFromCategory(String priceCategory) {
        if (priceCategory.contains("VIP")) {
            return 100.0;
        } else if (priceCategory.contains("Premium")) {
            return 75.0;
        } else if (priceCategory.contains("Standard")) {
            return 50.0;
        } else {
            // Extract price value using regex if explicit category match fails
            try {
                // Try to find a value in the $ format (e.g., $100)
                String priceStr = priceCategory.replaceAll(".*\\$(\\d+).*", "$1");
                if (priceStr.matches("\\d+")) {
                    return Double.parseDouble(priceStr);
                }
            } catch (Exception e) {
                System.out.println("Could not parse price from: " + priceCategory);
            }
            return 50.0; // Safer default value (something > 0)
        }
    }

    public boolean bookTicket(String customerName, String eventName, String priceCategory) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'bookTicket'");
    }
}
