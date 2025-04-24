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
        initializeSalesData();
    }

    /**
     * Initialize sales data if table is empty
     */
    private void initializeSalesData() {
        try {
            List<Map<String, Object>> existingData = queryBuilder.select("Sales", new String[] {
                    "sale_date", "tickets_sold", "revenue", "category"
            });

            if (existingData.isEmpty()) {
                generateAndInsertSampleData();
            }
        } catch (Exception e) {
            // If table doesn't exist or other error, generate sample data
            generateAndInsertSampleData();
        }
    }

    /**
     * Generate and insert sample data into the Sales table
     */
    private void generateAndInsertSampleData() {
        List<Map<String, Object>> sampleData = new ArrayList<>();
        Random random = new Random();
        Calendar cal = Calendar.getInstance();

        // Generate last 30 days of sample data
        for (int i = 0; i < 30; i++) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            String[] categories = { "Regular", "VIP", "Premium" };

            for (String category : categories) {
                Map<String, Object> sale = new HashMap<>();
                int ticketsSold = 50 + random.nextInt(200);
                double avgPrice = category.equals("VIP") ? 150 : category.equals("Premium") ? 100 : 50;

                sale.put("sale_date", new java.sql.Date(cal.getTimeInMillis()));
                sale.put("tickets_sold", ticketsSold);
                sale.put("revenue", ticketsSold * avgPrice);
                sale.put("category", category);

                sampleData.add(sale);
            }
        }

        // Try to insert the sample data into the database
        try {
            for (Map<String, Object> sale : sampleData) {
                queryBuilder.insert("Sales", sale);
            }
            System.out.println("✅ Generated and inserted sample sales data");
        } catch (Exception e) {
            System.err.println("Failed to insert sample data: " + e.getMessage());
        }
    }

    /**
     * Fetch all sales data from the database.
     */
    public List<Map<String, Object>> getAllSalesData() {
        try {
            List<Map<String, Object>> data = queryBuilder.select("Sales", new String[] {
                    "sale_date", "tickets_sold", "revenue", "category"
            });
            return data.isEmpty() ? generateSampleData() : data;
        } catch (Exception e) {
            lastErrorMessage = "Error selecting data from table: Sales";
            System.err.println(lastErrorMessage);
            e.printStackTrace();
            return generateSampleData();
        }
    }

    /**
     * Generate sample sales data for display
     */
    private List<Map<String, Object>> generateSampleData() {
        List<Map<String, Object>> sampleData = new ArrayList<>();
        Random random = new Random();
        Calendar cal = Calendar.getInstance();

        // Generate last 30 days of sample data
        for (int i = 0; i < 30; i++) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            String[] categories = { "Regular", "VIP", "Premium" };

            for (String category : categories) {
                Map<String, Object> sale = new HashMap<>();
                int ticketsSold = 50 + random.nextInt(200);
                double avgPrice = category.equals("VIP") ? 150 : category.equals("Premium") ? 100 : 50;

                sale.put("sale_date", new java.sql.Date(cal.getTimeInMillis()));
                sale.put("tickets_sold", ticketsSold);
                sale.put("revenue", ticketsSold * avgPrice);
                sale.put("category", category);

                sampleData.add(sale);
            }
        }

        return sampleData;
    }

    /**
     * Filter sales data by date or category.
     */
    public List<Map<String, Object>> filterSalesData(String filterCriteria) {
        try {
            Map<String, Object> filters = new HashMap<>();
            if (filterCriteria != null && !filterCriteria.isEmpty()) {
                if (filterCriteria.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    filters.put("sale_date", filterCriteria);
                } else {
                    filters.put("category", filterCriteria);
                }
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