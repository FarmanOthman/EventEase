package server;

import database.*;
import java.util.HashMap;
import java.util.Map;

public class EventService {

    private QueryBuilder queryBuilder;

    // Constructor initializes the QueryBuilder
    public EventService() {
        queryBuilder = new QueryBuilder(); // Initializes QueryBuilder for database operations
    }

    // Method to add an event to the database
    public void addEvent(String eventName, String eventDate, String teamA, String teamB,
            String eventDescription, String eventCategory, String eventType) throws Exception {

        // Validate that team_a and team_b are not the same before inserting
        if (teamA.equals(teamB)) {
            throw new IllegalArgumentException("Error: Team A and Team B cannot be the same!");
        }

        // Validate event category and type according to database constraints
        if (!validateEventCategory(eventCategory)) {
            throw new IllegalArgumentException("Error: Event category must be 'Regular' or 'VIP'");
        }

        if (!validateEventType(eventType)) {
            throw new IllegalArgumentException("Error: Event type must be 'Event' or 'Match'");
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

        // created_at and updated_at have DEFAULT CURRENT_TIMESTAMP in the database,
        // so we don't need to explicitly set them

        // Call the QueryBuilder to insert the event into the EVENT table
        queryBuilder.insert("Event", eventValues);
        System.out.println("Event added to the database successfully!");
    }

    // Validate that event category matches the database constraints
    private boolean validateEventCategory(String category) {
        return category.equals("Regular") || category.equals("VIP");
    }

    // Validate that event type matches the database constraints
    private boolean validateEventType(String type) {
        return type.equals("Event") || type.equals("Match");
    }

    // CustomInformationService class to handle customer data
    public static class CustomInformationService {

        private QueryBuilder queryBuilder;

        // Constructor initializes the QueryBuilder for the CustomInformationService
        public CustomInformationService() {
            queryBuilder = new QueryBuilder(); // Initializes QueryBuilder for database operations
        }

        // Method to add a customer's information to the database
        public void addCustomer(String firstName, String lastName, String contactNumber, String email)
                throws Exception {
            // Validate contact number and email
            if (!validateContactNumber(contactNumber)) {
                throw new IllegalArgumentException("Invalid contact number format. Must be between 10-15 digits.");
            }

            if (!validateEmail(email)) {
                throw new IllegalArgumentException("Invalid email format. Must contain @ and a domain.");
            }

            // Prepare the data to be inserted
            Map<String, Object> customerValues = new HashMap<>();
            customerValues.put("first_name", firstName);
            customerValues.put("last_name", lastName);
            customerValues.put("contact_number", contactNumber);
            customerValues.put("email", email);

            // created_at and updated_at have DEFAULT CURRENT_TIMESTAMP in the database,
            // so we don't need to explicitly set them

            // Call the QueryBuilder to insert the customer into the CUSTOMER table
            queryBuilder.insert("Customer", customerValues);
            System.out.println("Customer information added to the database successfully!");
        }

        // Validate contact number based on database constraints (10-15 digits)
        private boolean validateContactNumber(String contactNumber) {
            return contactNumber != null && contactNumber.length() >= 10 && contactNumber.length() <= 15;
        }

        // Basic email validation based on database CHECK constraint
        private boolean validateEmail(String email) {
            return email != null && email.contains("@") && email.contains(".");
        }
    }
}
