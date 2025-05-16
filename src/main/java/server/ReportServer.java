package server;

import database.QueryBuilder;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class ReportServer {

    private final QueryBuilder queryBuilder;

    public ReportServer() {
        this.queryBuilder = new QueryBuilder();
    }

    // Method to insert a sales report into the database
    public void addSalesReport(String date, int ticketsSold, double revenue, String category) {
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("report_date", date);                 // e.g. "2025-04-15"
        reportData.put("tickets_sold", ticketsSold);         // e.g. 450
        reportData.put("revenue", revenue);                  // e.g. 22500.0
        reportData.put("category", category);                // e.g. "VIP"
        reportData.put("created_at", new Timestamp(System.currentTimeMillis()));
        reportData.put("updated_at", new Timestamp(System.currentTimeMillis()));

        try {
            queryBuilder.insert("SalesReport", reportData); // Table name must match your DB schema
            System.out.println("✅ Sales report inserted successfully.");
        } catch (Exception e) {
            System.err.println("❌ Failed to insert sales report:");
            e.printStackTrace();
        }
    }

    // Optional: you can add methods for selecting data if needed for filtering or exports
    public void close() {
        queryBuilder.closeConnection(); // Close when done
    }
}
