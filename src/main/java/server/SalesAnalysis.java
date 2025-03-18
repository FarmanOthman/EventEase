package server;
import database.*;

import java.sql.*;
import java.util.*;

public class SalesAnalysis {

    // Method to get sales data for analysis
    public List<Map<String, Object>> getSalesReportData() {
        List<Map<String, Object>> salesData = new ArrayList<>();

        // Query to fetch relevant sales data from the Booking and Event tables
        String sql = "SELECT e.event_id, e.team_a, e.team_b, e.event_date, " +
                     "COUNT(b.booking_id) AS total_ticket_sold, " +
                     "SUM(b.price) AS total_revenue, " +
                     "SUM(CASE WHEN b.ticket_category = 'VIP' THEN 1 ELSE 0 END) AS vip_tickets, " +
                     "SUM(CASE WHEN b.ticket_category = 'Standard' THEN 1 ELSE 0 END) AS standard_tickets, " +
                     "SUM(CASE WHEN b.ticket_category = 'Premium' THEN 1 ELSE 0 END) AS premium_tickets " +
                     "FROM Booking b " +
                     "JOIN Event e ON b.event_id = e.event_id " +
                     "WHERE b.status = 'confirmed' AND b.payment_status = 'paid' AND e.event_status = 'active' " +
                     "GROUP BY e.event_id, e.team_a, e.team_b, e.event_date " +
                     "ORDER BY e.event_date DESC;";

        // Fetch data from the database
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();
                rowData.put("event_id", rs.getInt("event_id"));
                rowData.put("team_a", rs.getString("team_a"));
                rowData.put("team_b", rs.getString("team_b"));
                rowData.put("event_date", rs.getDate("event_date"));
                rowData.put("total_ticket_sold", rs.getInt("total_ticket_sold"));
                rowData.put("total_revenue", rs.getDouble("total_revenue"));
                rowData.put("vip_tickets", rs.getInt("vip_tickets"));
                rowData.put("standard_tickets", rs.getInt("standard_tickets"));
                rowData.put("premium_tickets", rs.getInt("premium_tickets"));

                salesData.add(rowData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salesData;
    }
}
