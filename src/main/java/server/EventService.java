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
    public void addEvent( String eventName, String eventDate,String teamA, String teamB,String eventDescription,  String eventCategory, String eventType) {
        // Prepare the data to be inserted
        Map<String, Object> eventValues = new HashMap<>();
        
        // eventValues.put("event_id", eventId);
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

    // Main method to test adding an event
   
}
