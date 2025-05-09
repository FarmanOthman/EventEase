package server;

import database.QueryBuilder;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
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

        // Get the price
        double price = getPriceFromCategory(selectedPriceCategory);

        try {
            // Check if a ticket for this event and ticket type already exists
            Map<String, Object> filters = new HashMap<>();
            filters.put("event_id", eventId);
            filters.put("ticket_type", ticketType);
            
            List<Map<String, Object>> existingTickets = queryBuilder.selectWithFilters(
                "Ticket", 
                filters, 
                new String[]{"ticket_id", "customer_id"}
            );
            
            if (!existingTickets.isEmpty()) {
                // A ticket for this event and type already exists
                // We'll update the sales data but won't create a new ticket
                System.out.println("A ticket of type " + ticketType + " for event ID " + eventId + " already exists. Updating sales only.");
                
                // Update the Sales table to record this sale
                updateSalesTable(ticketType, price);
                return true;
            }
            
            // If we get here, no ticket exists yet, so create a new one
            
            // Prepare the data to be inserted
            Map<String, Object> ticketValues = new HashMap<>();

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

            // Call the QueryBuilder to insert the booking into the Ticket table
            queryBuilder.insert("Ticket", ticketValues);
            System.out.println("Booking added to the database successfully!");
            
            // Update the Sales table
            updateSalesTable(ticketType, price);
            
            return true;
        } catch (Exception e) {
            System.out.println("Failed to add booking to the database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update the Sales table when a ticket is sold
     * Aggregates sales by date and category
     */
    private void updateSalesTable(String ticketType, double price) {
        try {
            // Today's date
            String today = Date.valueOf(LocalDate.now()).toString();
            String category = getCategoryFromTicketType(ticketType, price);
            
            // Check if a record for today and this category already exists
            Map<String, Object> filters = new HashMap<>();
            filters.put("sale_date", today);
            filters.put("category", category);
            
            List<Map<String, Object>> existingSales = queryBuilder.selectWithFilters(
                "Sales", 
                filters,
                new String[]{"sale_id", "tickets_sold", "revenue"}
            );
            
            if (!existingSales.isEmpty()) {
                // Update existing record
                Map<String, Object> existingSale = existingSales.get(0);
                int saleId = (Integer) existingSale.get("sale_id");
                int currentTickets = (Integer) existingSale.get("tickets_sold");
                double currentRevenue = (Double) existingSale.get("revenue");
                
                // Increment the count and revenue
                Map<String, Object> updateValues = new HashMap<>();
                updateValues.put("tickets_sold", currentTickets + 1);
                updateValues.put("revenue", currentRevenue + price);
                updateValues.put("updated_at", new Timestamp(System.currentTimeMillis()));
                
                // Use the correct update method that matches QueryBuilder's signature
                queryBuilder.update("Sales", updateValues, "sale_id", saleId);
                
                System.out.println("Updated existing sales record for " + today + " and category " + category);
            } else {
                // Insert new record
                Map<String, Object> newSale = new HashMap<>();
                newSale.put("sale_date", today);
                newSale.put("tickets_sold", 1);
                newSale.put("revenue", price);
                newSale.put("category", category);
                
                queryBuilder.insert("Sales", newSale);
                System.out.println("Created new sales record for " + today + " and category " + category);
            }
        } catch (Exception e) {
            System.out.println("Error updating Sales table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Maps ticket type and price to a sales category
     */
    private String getCategoryFromTicketType(String ticketType, double price) {
        if (ticketType.equals("VIP")) {
            return "VIP";
        } else if (price == 15.0) {
            return "Premium";  // Regular-Premium
        } else {
            return "Regular";  // Regular-Standard
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
            return 25.0;
        } else if (priceCategory.contains("Premium")) {
            return 15.0;
        } else if (priceCategory.contains("Standard")) {
            return 10.0;  // Fixed price for standard tickets
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
            return 10.0; // Default to standard ticket price
        }
    }

    public boolean bookTicket(String customerName, String eventName, String priceCategory) {
        throw new UnsupportedOperationException("Unimplemented method 'bookTicket'");
    }
}
