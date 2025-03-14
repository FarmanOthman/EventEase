package server;

import database.QueryBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class EventService {
    private static final Logger logger = Logger.getLogger(EventService.class.getName());
    private QueryBuilder queryBuilder;

    public EventService() {
        this.queryBuilder = new QueryBuilder();
    }

    public void saveEvent(String eventName, String team1, String team2, String date, String category, String type, String eventDetails) {
        try {
            Map<String, Object> values = new HashMap<>();
            values.put("EventName", eventName);
            values.put("Team1", team1);
            values.put("Team2", team2);
            values.put("Date", date);
            values.put("Category", category);
            values.put("Type", type);
            values.put("EventDetails", eventDetails);

            queryBuilder.insert("Event", values);
            logger.info("✅ Event saved successfully");
        } catch (Exception e) {
            logger.severe("❌ Error saving event: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getAllEvents() {
        return queryBuilder.select("Event", "EventName", "Team1", "Team2", "Date", "Category", "Type", "EventDetails");
    }

    public void updateEvent(String eventName, String columnToUpdate, Object newValue) {
        try {
            Map<String, Object> values = new HashMap<>();
            values.put(columnToUpdate, newValue);
            queryBuilder.update("Event", values, "EventName", eventName);
            logger.info("✅ Event updated successfully");
        } catch (Exception e) {
            logger.severe("❌ Error updating event: " + e.getMessage());
        }
    }

    public void deleteEvent(String eventName) {
        try {
            queryBuilder.delete("Event", "EventName", eventName);
            logger.info("✅ Event deleted successfully");
        } catch (Exception e) {
            logger.severe("❌ Error deleting event: " + e.getMessage());
        }
    }
}
