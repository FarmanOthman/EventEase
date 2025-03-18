package server;

import database.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class EventService {

    private QueryBuilder queryBuilder;

    // Constructor initializes the QueryBuilder
    public EventService() {
        queryBuilder = new QueryBuilder(); // Initializes QueryBuilder for database operations
    }

    // Method to add an event to the database
    public void addEvent(String eventName, String eventDate, String teamA, String teamB, String eventDescription, String eventCategory, String eventType) {
        // Prepare the data to be inserted
        Map<String, Object> eventValues = new HashMap<>();

        eventValues.put("event_name", eventName);
        eventValues.put("event_date", eventDate);  // Assuming event_date is a string format: "YYYY-MM-DD HH:MM:SS"
        eventValues.put("event_description", eventDescription);
        eventValues.put("category", eventCategory);
        eventValues.put("event_type", eventType);
        eventValues.put("team_a", teamA);
        eventValues.put("team_b", teamB);
        eventValues.put("created_at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for created_at
        eventValues.put("updated_at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for updated_at

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
        public void addCustomer(String firstName, String lastName, String contactNumber, String email) {
            // Prepare the data to be inserted
            Map<String, Object> customerValues = new HashMap<>();

            customerValues.put("first_name", firstName);
            customerValues.put("last_name", lastName);
            customerValues.put("contact_number", contactNumber);
            customerValues.put("email", email);
            customerValues.put("created_at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for created_at
            customerValues.put("updated_at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for updated_at

            // Call the QueryBuilder to insert the customer into the CUSTOMER table
            queryBuilder.insert("Customer", customerValues);
            System.out.println("Customer information added to the database successfully!");
        }
    }
}
