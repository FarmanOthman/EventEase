package services;

public class Booking {

    public boolean bookTicket(String customerName, String eventName, String priceCategory) {
        if (customerName == null || customerName.isEmpty() ||
            eventName == null || eventName.equals("Select Event") ||
            priceCategory == null || priceCategory.equals("Select Price Category")) {
            return false; // Invalid input
        }

        // Simulate booking logic (you'll later connect this to DB)
        System.out.println("Booking successful for: " + customerName + ", Event: " + eventName + ", Price: " + priceCategory);
        
        return true; // Booking succeeded
    }
}
