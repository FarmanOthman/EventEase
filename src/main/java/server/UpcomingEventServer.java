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
 * - Editing events
 * - Deleting events
 */
public class UpcomingEventServer {

    private final QueryBuilder queryBuilder;

    // Constructor: Initializes QueryBuilder for database operations
    public UpcomingEventServer() {
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
                "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b");
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
                new String[] { "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b" });
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
                new String[] { "event_id", "event_name", "event_date", "category", "event_type", "team_a", "team_b" });
    }

    /**
     * Fetches events filtered by location.
     *
     * @param location Event location
     * @return List of events in that location
     */
    public List<Map<String, Object>> getEventsByLocation(String location) {
        // Since location column doesn't exist, return empty list for now
        return new ArrayList<>();
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
        // Return empty list since location column doesn't exist
        return new ArrayList<>();
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
                        "team_a", "team_b", "event_description"
                });

        return result.isEmpty() ? null : result.get(0);
    }

    // ---------------------------- Booking ----------------------------

    /**
     * Books an event for a customer.
     *
     * @param customerId      Customer's ID
     * @param eventId         Event's ID
     * @param numberOfTickets Number of tickets to book
     * @throws Exception If booking fails
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

    // ---------------------------- Edit Event ----------------------------

    /**
     * Edits an existing event's details.
     *
     * @param eventId          The ID of the event to update
     * @param eventName        New event name
     * @param eventDate        New event date
     * @param teamA            New team A name
     * @param teamB            New team B name
     * @param eventDescription New event description
     * @param eventCategory    New event category
     * @param eventType        New event type
     * @param location         New location (optional, can be null)
     * @return True if the update was successful
     */
    public boolean editEvent(int eventId, String eventName, String eventDate,
            String teamA, String teamB, String eventDescription,
            String eventCategory, String eventType, String location) {
        try {
            // Validate that team_a and team_b are not the same before updating
            if (teamA.equals(teamB)) {
                throw new IllegalArgumentException("Error: Team A and Team B cannot be the same!");
            }

            // Create values map for the update operation
            Map<String, Object> updateValues = new HashMap<>();
            updateValues.put("event_name", eventName);
            updateValues.put("event_date", eventDate);
            updateValues.put("team_a", teamA);
            updateValues.put("team_b", teamB);
            updateValues.put("event_description", eventDescription);
            updateValues.put("category", eventCategory);
            updateValues.put("event_type", eventType);
            updateValues.put("updated_at", new Timestamp(System.currentTimeMillis()));

            // Check if location column exists before adding it to update values
            if (location != null && columnExists("Event", "location")) {
                updateValues.put("location", location);
            }

            // Perform the update using the correct method signature
            queryBuilder.update("Event", updateValues, "event_id", eventId);

            return true;
        } catch (Exception e) {
            System.out.println("Error updating event: " + e.getMessage());

            // If location column is causing problems, try again without it
            if (e.getMessage() != null && e.getMessage().contains("no such column: location") && location != null) {
                try {
                    // Create values map without location
                    Map<String, Object> updateValues = new HashMap<>();
                    updateValues.put("event_name", eventName);
                    updateValues.put("event_date", eventDate);
                    updateValues.put("team_a", teamA);
                    updateValues.put("team_b", teamB);
                    updateValues.put("event_description", eventDescription);
                    updateValues.put("category", eventCategory);
                    updateValues.put("event_type", eventType);
                    updateValues.put("updated_at", new Timestamp(System.currentTimeMillis()));

                    // Try update again without location
                    queryBuilder.update("Event", updateValues, "event_id", eventId);
                    return true;
                } catch (Exception ex) {
                    System.out.println("Error in retry update: " + ex.getMessage());
                    return false;
                }
            }

            return false;
        }
    }

    /**
     * Checks if a column exists in a table
     * 
     * @param tableName  The name of the table to check
     * @param columnName The name of the column to check
     * @return True if the column exists, false otherwise
     */
    private boolean columnExists(String tableName, String columnName) {
        try {
            // Use metadata query approach rather than direct selection
            // Query sqlite_master for table info to extract column information
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("type", "table");
            conditions.put("name", tableName);

            // Get the SQL that created the table
            List<Map<String, Object>> result = queryBuilder.selectWithFilters(
                    "sqlite_master",
                    conditions,
                    new String[] { "sql" });

            if (result.isEmpty() || result.get(0).get("sql") == null) {
                return false;
            }

            // Extract the SQL that created the table and check if our column name is in it
            String tableSql = result.get(0).get("sql").toString().toLowerCase();
            String columnToFind = columnName.toLowerCase();

            // Pattern to look for column definition:
            // Either `columnName` or columnName followed by type definition
            return tableSql.contains("`" + columnToFind + "`") ||
                    tableSql.contains("\"" + columnToFind + "\"") ||
                    tableSql.contains(" " + columnToFind + " ");
        } catch (Exception e) {
            System.out.println("Error checking if column exists: " + e.getMessage());
            return false;
        }
    }

    // ---------------------------- Delete Event ----------------------------

    /**
     * Deletes an event from the database.
     *
     * @param eventId The ID of the event to delete
     * @return True if the deletion was successful
     */
    public boolean deleteEvent(int eventId) {
        try {
            // Check if Booking table exists before attempting to check for bookings
            boolean bookingTableExists = tableExists("Booking");

            // Only check for bookings if the table exists
            if (bookingTableExists) {
                try {
                    // Check if the event has any bookings
                    Map<String, Object> conditions = new HashMap<>();
                    conditions.put("event_id", eventId);

                    List<Map<String, Object>> bookings = queryBuilder.selectWithFilters(
                            "Booking", conditions, new String[] { "booking_id" });

                    if (!bookings.isEmpty()) {
                        // Delete the associated bookings first
                        queryBuilder.delete("Booking", "event_id", eventId);
                    }
                } catch (Exception e) {
                    // If there's an error with the booking deletion, log it but continue with event
                    // deletion
                    System.out.println("Warning: Could not delete associated bookings: " + e.getMessage());
                }
            }

            // Perform the deletion of the event
            queryBuilder.delete("Event", "event_id", eventId);
            System.out.println("Record deleted from table: Event");
            return true;
        } catch (Exception e) {
            System.out.println("Error deleting event: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a table exists in the database
     * 
     * @param tableName The name of the table to check
     * @return True if the table exists, false otherwise
     */
    private boolean tableExists(String tableName) {
        try {
            // Query the sqlite_master table to check if our table exists
            Map<String, Object> conditions = new HashMap<>();
            conditions.put("type", "table");
            conditions.put("name", tableName);

            List<Map<String, Object>> result = queryBuilder.selectWithFilters(
                    "sqlite_master", conditions, new String[] { "name" });

            return !result.isEmpty();
        } catch (Exception e) {
            System.out.println("Error checking if table exists: " + e.getMessage());
            return false;
        }
    }
}
