package services;

import java.util.*;
import database.QueryBuilder;

/**
 * Service layer class that handles all operations related to sales data.
 */
public class SalesDataService {
    private QueryBuilder queryBuilder;
    private String lastErrorMessage;

    public SalesDataService() {
        this.queryBuilder = new QueryBuilder();
        this.lastErrorMessage = "";
    }

    /**
     * Fetch all sales data from the database.
     */
    public List<Map<String, Object>> getAllSalesData() {
        try {
            return queryBuilder.select("Sales", new String[] {
                "sale_date", "tickets_sold", "revenue", "category"
            });
        } catch (Exception e) {
            lastErrorMessage = "Error fetching sales data: " + e.getMessage();
            return Collections.emptyList();
        }
    }

    /**
     * Filter sales data by date or event.
     */
    public List<Map<String, Object>> filterSalesData(String filterCriteria) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (filterCriteria != null && !filterCriteria.isEmpty()) {
                // Example: Assuming filterCriteria is a date or event name
                filters.put("event_name", filterCriteria);
            }

            return queryBuilder.selectWithFilters("Sales", filters, new String[] {
                "sale_date", "tickets_sold", "revenue", "category"
            });
        } catch (Exception e) {
            lastErrorMessage = "Error filtering sales data: " + e.getMessage();
            return Collections.emptyList();
        }
    }

    /**
     * Export sales data to a file (e.g., CSV).
     */
    public boolean exportSalesData(List<Map<String, Object>> salesData, String filePath) {
        try {
            // Logic to export data to a file
            System.out.println("Exporting sales data to: " + filePath);
            return true;
        } catch (Exception e) {
            lastErrorMessage = "Error exporting sales data: " + e.getMessage();
            return false;
        }
    }

    /**
     * Get the last error message.
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
}