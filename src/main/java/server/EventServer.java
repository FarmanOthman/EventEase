package server;

import database.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventServer {

    private QueryBuilder queryBuilder;

    // Constructor initializes the QueryBuilder
    public EventServer() {
        queryBuilder = new QueryBuilder(); // Initializes QueryBuilder for database operations
    }

    // Method to add an event to the database
    public void addEvent(String eventName, String eventDate, String teamA, String teamB,
            String eventDescription, String eventCategory, String eventType) throws Exception {

        // Validate that team_a and team_b are not the same before inserting
        if (teamA.equals(teamB)) {
            throw new IllegalArgumentException("Error: Team A and Team B cannot be the same!");
        }

        // Prepare the data to be inserted
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put("event_name", eventName);
        eventValues.put("event_date", eventDate); // Assuming event_date is a string format: "YYYY-MM-DD HH:MM:SS"
        eventValues.put("event_description", eventDescription);
        eventValues.put("category", eventCategory);
        eventValues.put("event_type", eventType);
        eventValues.put("team_a", teamA);
        eventValues.put("team_b", teamB);
        eventValues.put("created_at", new Timestamp(System.currentTimeMillis())); // Current timestamp for created_at
        eventValues.put("updated_at", new Timestamp(System.currentTimeMillis())); // Current timestamp for updated_at

        // Call the QueryBuilder to insert the event into the EVENT table
        queryBuilder.insert("Event", eventValues);
        System.out.println("Event added to the database successfully!");
    }

    // CustomInformationService class to handle customer data
    public static class CustomInformationService {

        private QueryBuilder queryBuilder;

        // Constructor initializes the QueryBuilder for the CustomInformationService
        public CustomInformationService() {
            queryBuilder = new QueryBuilder(); // Initializes QueryBuilder for database operations
        }

        // Method to add a customer's information to the database
        public int addCustomer(String firstName, String lastName, String contactNumber, String email) throws Exception {
            // Prepare the data to be inserted
            Map<String, Object> customerValues = new HashMap<>();
            customerValues.put("first_name", firstName);
            customerValues.put("last_name", lastName);
            customerValues.put("contact_number", contactNumber);
            customerValues.put("email", email);
            customerValues.put("created_at", new Timestamp(System.currentTimeMillis())); // Current timestamp for
                                                                                         // created_at
            customerValues.put("updated_at", new Timestamp(System.currentTimeMillis())); // Current timestamp for
                                                                                         // updated_at

            // Call the QueryBuilder to insert the customer into the CUSTOMER table
            queryBuilder.insert("Customer", customerValues);
            System.out.println("Customer information added to the database successfully!");

            // Get the customer ID after insertion
            return getCustomerId(firstName, lastName, contactNumber, email);
        }

        // Method to retrieve a customer's ID based on their information
        public int getCustomerId(String firstName, String lastName, String contactNumber, String email)
                throws Exception {
            // Create filter for customer lookup
            Map<String, Object> filters = new HashMap<>();
            filters.put("first_name", firstName);
            filters.put("last_name", lastName);
            filters.put("contact_number", contactNumber);
            filters.put("email", email);

            // Query the database to find matching customer records
            List<Map<String, Object>> customers = queryBuilder.selectWithFilters("Customer", filters,
                    new String[] { "customer_id", "first_name", "last_name" });

            if (customers.isEmpty()) {
                throw new Exception("Customer not found in the database");
            }

            // Return the customer ID from the first match
            return (Integer) customers.get(0).get("customer_id");
        }

        // Method to retrieve a customer's details by ID
        public Map<String, Object> getCustomerDetails(int customerId) throws Exception {
            // Create filter for customer lookup by ID
            Map<String, Object> filters = new HashMap<>();
            filters.put("customer_id", customerId);

            // Query the database to find customer record
            List<Map<String, Object>> customers = queryBuilder.selectWithFilters("Customer", filters,
                    new String[] { "customer_id", "first_name", "last_name", "contact_number", "email" });

            if (customers.isEmpty()) {
                throw new Exception("Customer with ID " + customerId + " not found in the database");
            }

            // Return the customer details
            return customers.get(0);
        }
    }
}
