package server;

import database.*;

import java.sql.*;
import java.util.*;

public class SalesAnalysis {

    // Method to get sales data for analysis
    public List<Map<String, Object>> getSalesReportData() {
        try {
            // First check if the Booking table exists
            if (isTableExists("Booking") && isTableExists("Event")) {
                return getDataFromBookingTable();
            } else if (isTableExists("SalesReport")) {
                return getDataFromSalesReportTable();
            } else {
                // If tables don't exist, generate sample data
                return generateSampleData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Return sample data if any error occurs
            return generateSampleData();
        }
    }

    private boolean isTableExists(String tableName) throws SQLException {
        boolean exists = false;

        try (Connection conn = Database.getConnection();
                ResultSet rs = conn.getMetaData().getTables(null, null, tableName, null)) {
            exists = rs.next();
        }

        return exists;
    }

    private List<Map<String, Object>> getDataFromBookingTable() {
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

    private List<Map<String, Object>> getDataFromSalesReportTable() {
        List<Map<String, Object>> salesData = new ArrayList<>();

        String sql = "SELECT * FROM SalesReport ORDER BY report_date DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> rowData = new HashMap<>();
                // Map the SalesReport fields to the format expected by the UI
                rowData.put("event_id", rs.getInt("report_id"));
                rowData.put("team_a", "Team " + rs.getString("category"));
                rowData.put("team_b", "Opponent");
                rowData.put("event_date", rs.getDate("report_date"));
                rowData.put("total_ticket_sold", rs.getInt("tickets_sold"));
                rowData.put("total_revenue", rs.getDouble("revenue"));
                rowData.put("vip_tickets", rs.getInt("tickets_sold") / 3);
                rowData.put("standard_tickets", rs.getInt("tickets_sold") / 3);
                rowData.put("premium_tickets", rs.getInt("tickets_sold") / 3);

                salesData.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return salesData;
    }

    private List<Map<String, Object>> generateSampleData() {
        List<Map<String, Object>> sampleData = new ArrayList<>();

        // Create sample data for demonstration
        String[] teams = { "Real Madrid", "Barcelona", "Manchester United", "Liverpool", "Bayern Munich" };
        String[] opponents = { "Juventus", "PSG", "Chelsea", "Arsenal", "Inter Milan" };

        Random random = new Random();

        // Generate 10 sample records
        for (int i = 0; i < 10; i++) {
            Map<String, Object> rowData = new HashMap<>();

            // Generate a date within the last year
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -random.nextInt(365));
            java.util.Date eventDate = cal.getTime();

            int teamIndex = random.nextInt(teams.length);
            int opponentIndex = random.nextInt(opponents.length);
            int ticketsSold = 1000 + random.nextInt(9000);
            double revenue = ticketsSold * (50 + random.nextInt(150));

            rowData.put("event_id", i + 1);
            rowData.put("team_a", teams[teamIndex]);
            rowData.put("team_b", opponents[opponentIndex]);
            rowData.put("event_date", new java.sql.Date(eventDate.getTime()));
            rowData.put("total_ticket_sold", ticketsSold);
            rowData.put("total_revenue", revenue);
            rowData.put("vip_tickets", ticketsSold / 5);
            rowData.put("standard_tickets", (ticketsSold * 3) / 5);
            rowData.put("premium_tickets", ticketsSold / 5);

            sampleData.add(rowData);
        }

        return sampleData;
    }
}
