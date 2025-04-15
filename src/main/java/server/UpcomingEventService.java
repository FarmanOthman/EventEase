package server;


import database.QueryBuilder;
import java.sql.Timestamp;
import java.util.*;

/**
 * UpcomingEventService handles all upcoming event-related operations such as:
 * - Fetching all events
 * - Filtering by category, date, and location
 * - Getting detailed event info
 * - Booking events
 */
public class UpcomingEventService {

    private final QueryBuilder queryBuilder;

    // Constructor: Initializes QueryBuilder for database operations
    public UpcomingEventService() {
        this.queryBuilder = new QueryBuilder();
    }

    // ---------------------------- Fetch Events ----------------------------

    /**
     * Fetches all events from the database.
     *
     * @return List of all event records
     */
    public List<Map<String, Object>> getAllEvents() {
        return queryBuilder.select("Event",
            "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b", "location");
    }

    /**
     * Fetches events filtered by category.
     *
     * @param category Event category
     * @return List of matching events
     */
    public List<Map<String, Object>> getEventsByCategory(String category) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("category", category);

        return queryBuilder.selectWithFilters("Event", filter,
            new String[] { "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b", "location" });
    }

    /**
     * Fetches events filtered by a specific date.
     *
     * @param date Date in YYYY-MM-DD format
     * @return List of events on that date
     */
    public List<Map<String, Object>> getEventsByDate(String date) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("DATE(event_date)", date); // Assumes SQLite-style date handling

        return queryBuilder.selectWithFilters("Event", filter,
            new String[] { "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b", "location" });
    }

    /**
     * Fetches events filtered by location.
     *
     * @param location Event location
     * @return List of events in that location
     */
    public List<Map<String, Object>> getEventsByLocation(String location) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("location", location);

        return queryBuilder.selectWithFilters("Event", filter,
            new String[] { "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b", "location" });
    }

    // ---------------------------- Dropdown Helpers ----------------------------

    /**
     * Gets a list of all available categories (for dropdowns or filters).
     *
     * @return List of unique category names
     */
    public List<String> getEventCategories() {
        List<Map<String, Object>> result = queryBuilder.select("Event", "category");
        Set<String> categories = new HashSet<>();

        for (Map<String, Object> row : result) {
            if (row.get("category") != null) {
                categories.add(row.get("category").toString());
            }
        }

        return new ArrayList<>(categories);
    }

    /**
     * Gets a list of all available event locations (for dropdowns or filters).
     *
     * @return List of unique locations
     */
    public List<String> getEventLocations() {
        List<Map<String, Object>> result = queryBuilder.select("Event", "location");
        Set<String> locations = new HashSet<>();

        for (Map<String, Object> row : result) {
            if (row.get("location") != null) {
                locations.add(row.get("location").toString());
            }
        }

        return new ArrayList<>(locations);
    }

    // ---------------------------- Event Details ----------------------------

    /**
     * Fetches detailed information for a specific event.
     *
     * @param eventId Unique event identifier
     * @return Map containing detailed event information
     */
    public Map<String, Object> getEventDetails(int eventId) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("event_id", eventId);

        List<Map<String, Object>> result = queryBuilder.selectWithFilters("Event", filter,
            new String[] {
                "event_id", "event_name", "event_date", "category", "event_type",
                "team_a", "team_b", "location", "event_description"
            });

        return result.isEmpty() ? null : result.get(0);
    }

    // ---------------------------- Booking ----------------------------

    /**
     * Books an event for a customer.
     *
     * @param customerId        Customer's ID
     * @param eventId           Event's ID
     * @param numberOfTickets   Number of tickets to book
     * @throws Exception        If booking fails
     */
    public void bookEvent(int customerId, int eventId, int numberOfTickets) throws Exception {
        Map<String, Object> booking = new HashMap<>();
        booking.put("customer_id", customerId);
        booking.put("event_id", eventId);
        booking.put("tickets", numberOfTickets);
        booking.put("booked_at", new Timestamp(System.currentTimeMillis()));

        queryBuilder.insert("Booking", booking);
        System.out.println("Booking successful!");
    }
}

