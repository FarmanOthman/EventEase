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
    public void addEvent(int eventId, String teamA, String teamB, String eventDate, int venueId, String eventStatus) {
        // Prepare the data to be inserted
        Map<String, Object> eventValues = new HashMap<>();
        
        eventValues.put("event_id", eventId);
        eventValues.put("team_a", teamA);
        eventValues.put("team_b", teamB);
        eventValues.put("event_date", eventDate);  // Assuming event_date is a string format: "YYYY-MM-DD HH:MM:SS"
        eventValues.put("venue_id", venueId);
        eventValues.put("event_status", eventStatus);
        eventValues.put("created_at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for created_at
        eventValues.put("updated_at", new Timestamp(System.currentTimeMillis()));  // Current timestamp for updated_at

        // Call the QueryBuilder to insert the event into the EVENT table
        queryBuilder.insert("EVENT", eventValues);
        System.out.println("Event added to the database successfully!");
    }

    // Main method to test adding an event
   
}
