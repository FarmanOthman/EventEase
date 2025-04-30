package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Main class that handles the taste tracking system functionality
 */
public class TasteTracker {
    private List<FoodItem> foodItems;
    private List<TasteEntry> tasteEntries;
    private Map<String, TasteProfile> userProfiles;
    private DatabaseManager dbManager;
    private AnalyticsEngine analyticsEngine;
    private static final String DATA_DIRECTORY = "taste_tracker_data";
    
    /**
     * Constructor initializes the taste tracker with empty collections
     */
    public TasteTracker() {
        this.foodItems = new ArrayList<>();
        this.tasteEntries = new ArrayList<>();
        this.userProfiles = new HashMap<>();
        this.dbManager = new DatabaseManager();
        this.analyticsEngine = new AnalyticsEngine();
        
        try {
            Files.createDirectories(Paths.get(DATA_DIRECTORY));
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }
    
    /**
     * Adds a new food item to the system
     * @param foodItem The food item to add
     * @return true if added successfully
     */
    public boolean addFoodItem(FoodItem foodItem) {
        if (foodItem == null) {
            return false;
        }
        
        for (FoodItem item : foodItems) {
            if (item.getName().equalsIgnoreCase(foodItem.getName())) {
                return false; // Item already exists
            }
        }
        
        foodItems.add(foodItem);
        dbManager.saveFoodItem(foodItem);
        return true;
    }
    
    /**
     * Adds a taste entry to the system
     * @param entry The taste entry to add
     * @return true if added successfully
     */
    public boolean addTasteEntry(TasteEntry entry) {
        if (entry == null) {
            return false;
        }
        
        tasteEntries.add(entry);
        
        // Update user profile
        TasteProfile profile = userProfiles.getOrDefault(entry.getUserId(), new TasteProfile(entry.getUserId()));
        profile.updateWithEntry(entry);
        userProfiles.put(entry.getUserId(), profile);
        
        // Save to database
        dbManager.saveTasteEntry(entry);
        
        // Update analytics
        analyticsEngine.processEntry(entry);
        
        return true;
    }
    
    /**
     * Retrieves all taste entries for a specific user
     * @param userId The user ID to look up
     * @return List of taste entries for the user
     */
    public List<TasteEntry> getUserEntries(String userId) {
        List<TasteEntry> userEntries = new ArrayList<>();
        
        for (TasteEntry entry : tasteEntries) {
            if (entry.getUserId().equals(userId)) {
                userEntries.add(entry);
            }
        }
        
        return userEntries;
    }
    
    /**
     * Gets a user's taste profile
     * @param userId The user ID to look up
     * @return The user's taste profile
     */
    public TasteProfile getUserProfile(String userId) {
        return userProfiles.getOrDefault(userId, new TasteProfile(userId));
    }
    
    /**
     * Gets food item recommendations for a user
     * @param userId The user ID
     * @param count Number of recommendations to return
     * @return List of recommended food items
     */
   
    
    /**
     * Searches for food items matching the query
     * @param query The search query
     * @return List of matching food items
     */
    public List<FoodItem> searchFoodItems(String query) {
        List<FoodItem> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (FoodItem item : foodItems) {
            if (item.getName().toLowerCase().contains(lowerQuery) || 
                item.getCategory().toLowerCase().contains(lowerQuery)) {
                results.add(item);
            }
        }
        
        return results;
    }
    
    /**
     * Exports user data to a file
     * @param userId The user ID
     * @param filePath Path to export the data to
     * @throws IOException If an I/O error occurs
     */
    public void exportUserData(String userId, String filePath) throws IOException {
        TasteProfile profile = getUserProfile(userId);
        List<TasteEntry> entries = getUserEntries(userId);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Taste Profile for User: " + userId);
            writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            writer.println("\nPreference Summary:");
            writer.println("Favorite Category: " + profile.getFavoriteCategory());
            writer.println("Average Rating: " + profile.getAverageRating());
            writer.println("Total Entries: " + entries.size());
            
            writer.println("\nFlavor Preferences:");
            Map<FlavorProfile, Double> flavorRatings = profile.getFlavorRatings();
            for (Map.Entry<FlavorProfile, Double> entry : flavorRatings.entrySet()) {
                writer.println("- " + entry.getKey() + ": " + String.format("%.2f", entry.getValue()));
            }
            
            writer.println("\nTaste Entries:");
            for (TasteEntry entry : entries) {
                writer.println(entry.toString());
            }
        }
    }
    
    /**
     * Loads all data from the database
     */
    public void loadData() {
        foodItems = dbManager.loadFoodItems();
        tasteEntries = dbManager.loadTasteEntries();
        
        // Rebuild user profiles
        userProfiles.clear();
        for (TasteEntry entry : tasteEntries) {
            TasteProfile profile = userProfiles.getOrDefault(entry.getUserId(), new TasteProfile(entry.getUserId()));
            profile.updateWithEntry(entry);
            userProfiles.put(entry.getUserId(), profile);
        }
        
        // Rebuild analytics
        analyticsEngine.rebuildAnalytics(tasteEntries);
    }
    
    /**
     * Generates a summary report of taste trends
     * @return String containing the trend report
     */
  
}

/**
 * Represents a food item in the taste tracking system
 */
class FoodItem {
    private String id;
    private String name;
    private String category;
    private FlavorProfile flavorProfile;
    private Map<String, Double> nutritionalInfo;
    private List<String> ingredients;
    private String description;
    private String imageUrl;
    
    /**
     * Constructor for a food item
     * @param name Name of the food
     * @param category Category of the food
     */
    public FoodItem(String name, String category) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.flavorProfile = new FlavorProfile();
        this.nutritionalInfo = new HashMap<>();
        this.ingredients = new ArrayList<>();
        this.description = "";
        this.imageUrl = "";
    }
    
    /**
     * Full constructor for a food item
     * @param name Name of the food
     * @param category Category of the food
     * @param flavorProfile Flavor profile of the food
     * @param ingredients List of ingredients
     * @param description Description of the food
     */
    public FoodItem(String name, String category, FlavorProfile flavorProfile, List<String> ingredients, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.flavorProfile = flavorProfile;
        this.nutritionalInfo = new HashMap<>();
        this.ingredients = ingredients;
        this.description = description;
        this.imageUrl = "";
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public FlavorProfile getFlavorProfile() {
        return flavorProfile;
    }
    
    public void setFlavorProfile(FlavorProfile flavorProfile) {
        this.flavorProfile = flavorProfile;
    }
    
    public Map<String, Double> getNutritionalInfo() {
        return nutritionalInfo;
    }
    
    public void addNutritionalInfo(String key, Double value) {
        this.nutritionalInfo.put(key, value);
    }
    
    public List<String> getIngredients() {
        return ingredients;
    }
    
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
    
    public void addIngredient(String ingredient) {
        this.ingredients.add(ingredient);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    @Override
    public String toString() {
        return name + " (" + category + ")";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodItem foodItem = (FoodItem) o;
        return id.equals(foodItem.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

/**
 * Represents the flavor profile of a food item
 */
class FlavorProfile implements Serializable {
    private double sweetness;
    private double sourness;
    private double saltiness;
    private double bitterness;
    private double umami;
    private double spiciness;
    private Map<String, Double> additionalFlavors;
    
    /**
     * Default constructor initializes with neutral values
     */
    public FlavorProfile() {
        this.sweetness = 0.0;
        this.sourness = 0.0;
        this.saltiness = 0.0;
        this.bitterness = 0.0;
        this.umami = 0.0;
        this.spiciness = 0.0;
        this.additionalFlavors = new HashMap<>();
    }
    
    /**
     * Constructor with primary taste values
     * @param sweetness Sweetness level (0-10)
     * @param sourness Sourness level (0-10)
     * @param saltiness Saltiness level (0-10)
     * @param bitterness Bitterness level (0-10)
     * @param umami Umami level (0-10)
     * @param spiciness Spiciness level (0-10)
     */
    public FlavorProfile(double sweetness, double sourness, double saltiness, 
                        double bitterness, double umami, double spiciness) {
        this.sweetness = clamp(sweetness);
        this.sourness = clamp(sourness);
        this.saltiness = clamp(saltiness);
        this.bitterness = clamp(bitterness);
        this.umami = clamp(umami);
        this.spiciness = clamp(spiciness);
        this.additionalFlavors = new HashMap<>();
    }
    
    /**
     * Clamps a value between 0 and 10
     * @param value Value to clamp
     * @return Clamped value
     */
    private double clamp(double value) {
        return Math.max(0.0, Math.min(10.0, value));
    }
    
    // Getters and setters
    public double getSweetness() {
        return sweetness;
    }
    
    public void setSweetness(double sweetness) {
        this.sweetness = clamp(sweetness);
    }
    
    public double getSourness() {
        return sourness;
    }
    
    public void setSourness(double sourness) {
        this.sourness = clamp(sourness);
    }
    
    public double getSaltiness() {
        return saltiness;
    }
    
    public void setSaltiness(double saltiness) {
        this.saltiness = clamp(saltiness);
    }
    
    public double getBitterness() {
        return bitterness;
    }
    
    public void setBitterness(double bitterness) {
        this.bitterness = clamp(bitterness);
    }
    
    public double getUmami() {
        return umami;
    }
    
    public void setUmami(double umami) {
        this.umami = clamp(umami);
    }
    
    public double getSpiciness() {
        return spiciness;
    }
    
    public void setSpiciness(double spiciness) {
        this.spiciness = clamp(spiciness);
    }
    
    public Map<String, Double> getAdditionalFlavors() {
        return additionalFlavors;
    }
    
    public void addFlavor(String flavor, double intensity) {
        this.additionalFlavors.put(flavor, clamp(intensity));
    }
    
    /**
     * Calculates similarity between this profile and another
     * @param other Another flavor profile
     * @return Similarity score (0-1)
     */
    public double calculateSimilarity(FlavorProfile other) {
        double sum = 0.0;
        double weightSum = 0.0;
        
        // Compare primary taste components
        sum += Math.abs(sweetness - other.sweetness);
        sum += Math.abs(sourness - other.sourness);
        sum += Math.abs(saltiness - other.saltiness);
        sum += Math.abs(bitterness - other.bitterness);
        sum += Math.abs(umami - other.umami);
        sum += Math.abs(spiciness - other.spiciness);
        
        weightSum = 6.0;
        
        // Compare additional flavors
        Set<String> allFlavors = new HashSet<>();
        allFlavors.addAll(additionalFlavors.keySet());
        allFlavors.addAll(other.additionalFlavors.keySet());
        
        for (String flavor : allFlavors) {
            double thisValue = additionalFlavors.getOrDefault(flavor, 0.0);
            double otherValue = other.additionalFlavors.getOrDefault(flavor, 0.0);
            sum += Math.abs(thisValue - otherValue);
            weightSum += 1.0;
        }
        
        // Convert to similarity score (0-1)
        return 1.0 - (sum / (weightSum * 10.0));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sweet: ").append(String.format("%.1f", sweetness))
          .append(", Sour: ").append(String.format("%.1f", sourness))
          .append(", Salty: ").append(String.format("%.1f", saltiness))
          .append(", Bitter: ").append(String.format("%.1f", bitterness))
          .append(", Umami: ").append(String.format("%.1f", umami))
          .append(", Spicy: ").append(String.format("%.1f", spiciness));
        
        if (!additionalFlavors.isEmpty()) {
            sb.append(", Additional: ");
            boolean first = true;
            for (Map.Entry<String, Double> entry : additionalFlavors.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append("(").append(String.format("%.1f", entry.getValue())).append(")");
                first = false;
            }
        }
        
        return sb.toString();
    }
}

/**
 * Represents a user's taste entry
 */
class TasteEntry implements Serializable {
    private String id;
    private String userId;
    private String foodItemId;
    private String foodName;
    private int rating;
    private LocalDateTime timestamp;
    private Map<String, Integer> flavorRatings;
    private List<String> tags;
    private String notes;
    private String location;
    
    /**
     * Constructor for a taste entry
     * @param userId User ID
     * @param foodItemId Food item ID
     * @param foodName Food name
     * @param rating Rating (1-5)
     */
    public TasteEntry(String userId, String foodItemId, String foodName, int rating) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.foodItemId = foodItemId;
        this.foodName = foodName;
        this.rating = rating;
        this.timestamp = LocalDateTime.now();
        this.flavorRatings = new HashMap<>();
        this.tags = new ArrayList<>();
        this.notes = "";
        this.location = "";
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getFoodItemId() {
        return foodItemId;
    }
    
    public String getFoodName() {
        return foodName;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating));
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public Map<String, Integer> getFlavorRatings() {
        return flavorRatings;
    }
    
    public void addFlavorRating(String flavor, int rating) {
        this.flavorRatings.put(flavor, Math.max(1, Math.min(5, rating)));
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return String.format("%s: %s - Rating: %d/5 (%s) %s", 
                            timestamp.format(formatter), foodName, rating,
                            String.join(", ", tags), 
                            notes.isEmpty() ? "" : "Note: " + notes);
    }
}

/**
 * Represents a user's taste profile
 */
class TasteProfile implements Serializable {
    private String userId;
    private Map<String, Integer> categoryRatings;
    private Map<String, Integer> categoryCounts;
    private Map<FlavorProfile, Double> flavorRatings;
    private List<String> favoriteTags;
    private int totalEntries;
    private double averageRating;
    private LocalDateTime lastUpdated;
    
    /**
     * Constructor for a user profile
     * @param userId User ID
     */
    public TasteProfile(String userId) {
        this.userId = userId;
        this.categoryRatings = new HashMap<>();
        this.categoryCounts = new HashMap<>();
        this.flavorRatings = new HashMap<>();
        this.favoriteTags = new ArrayList<>();
        this.totalEntries = 0;
        this.averageRating = 0.0;
        this.lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Updates profile with a new taste entry
     * @param entry The new taste entry
     */
    public void updateWithEntry(TasteEntry entry) {
        if (!entry.getUserId().equals(userId)) {
            return;
        }
        
        // Extract food category from entry
        String category = "Unknown"; // Default category
        
        // Update category ratings
        int currentRating = categoryRatings.getOrDefault(category, 0);
        int currentCount = categoryCounts.getOrDefault(category, 0);
        
        categoryRatings.put(category, currentRating + entry.getRating());
        categoryCounts.put(category, currentCount + 1);
        
        // Update flavor ratings (simplified)
      
        // Update favorite tags
        for (String tag : entry.getTags()) {
            if (!favoriteTags.contains(tag)) {
                favoriteTags.add(tag);
            }
        }
        
        // Update general statistics
        totalEntries++;
        averageRating = ((averageRating * (totalEntries - 1)) + entry.getRating()) / totalEntries;
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Gets the user's favorite food category
     * @return The favorite category
     */
    public String getFavoriteCategory() {
        String favorite = "None";
        double highestAvg = 0.0;
        
        for (Map.Entry<String, Integer> entry : categoryRatings.entrySet()) {
            String category = entry.getKey();
            int totalRating = entry.getValue();
            int count = categoryCounts.get(category);
            double avgRating = (double) totalRating / count;
            
            if (avgRating > highestAvg) {
                highestAvg = avgRating;
                favorite = category;
            }
        }
        
        return favorite;
    }
    
    // Getters
    public String getUserId() {
        return userId;
    }
    
    public Map<String, Integer> getCategoryRatings() {
        return categoryRatings;
    }
    
    public Map<FlavorProfile, Double> getFlavorRatings() {
        return flavorRatings;
    }
    
    public List<String> getFavoriteTags() {
        return favoriteTags;
    }
    
    public int getTotalEntries() {
        return totalEntries;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
}

/**
 * Class responsible for database operations
 */
class DatabaseManager {
    private static final String FOOD_ITEMS_FILE = "taste_tracker_data/food_items.dat";
    private static final String TASTE_ENTRIES_FILE = "taste_tracker_data/taste_entries.dat";
    
    /**
     * Saves a food item to the database
     * @param item The food item to save
     */
    public void saveFoodItem(FoodItem item) {
        List<FoodItem> items = loadFoodItems();
        
        // Check if item already exists
        boolean exists = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            items.add(item);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FOOD_ITEMS_FILE))) {
            oos.writeObject(items);
        } catch (IOException e) {
            System.err.println("Error saving food item: " + e.getMessage());
        }
    }
    
    /**
     * Saves a taste entry to the database
     * @param entry The taste entry to save
     */
    public void saveTasteEntry(TasteEntry entry) {
        List<TasteEntry> entries = loadTasteEntries();
        
        // Check if entry already exists
        boolean exists = false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getId().equals(entry.getId())) {
                entries.set(i, entry);
                exists = true;
                break;
            }
        }
        
        if (!exists) {
            entries.add(entry);
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TASTE_ENTRIES_FILE))) {
            oos.writeObject(entries);
        } catch (IOException e) {
            System.err.println("Error saving taste entry: " + e.getMessage());
        }
    }
    
    /**
     * Loads food items from the database
     * @return List of food items
     */
    @SuppressWarnings("unchecked")
    public List<FoodItem> loadFoodItems() {
        List<FoodItem> items = new ArrayList<>();
        
        try {
            File file = new File(FOOD_ITEMS_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    items = (List<FoodItem>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading food items: " + e.getMessage());
        }
        
        return items;
    }
    
    /**
     * Loads taste entries from the database
     * @return List of taste entries
     */
    @SuppressWarnings("unchecked")
    public List<TasteEntry> loadTasteEntries() {
        List<TasteEntry> entries = new ArrayList<>();
        
        try {
            File file = new File(TASTE_ENTRIES_FILE);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    entries = (List<TasteEntry>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading taste entries: " + e.getMessage());
        }
        
        return entries;
    }
    
    /**
     * Deletes a food item from the database
     * @param itemId ID of the food item to delete
     * @return true if deleted successfully
     */
    public boolean deleteFoodItem(String itemId) {
        List<FoodItem> items = loadFoodItems();
        boolean removed = false;
        
        for (Iterator<FoodItem> iterator = items.iterator(); iterator.hasNext();) {
            FoodItem item = iterator.next();
            if (item.getId().equals(itemId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (removed) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FOOD_ITEMS_FILE))) {
                oos.writeObject(items);
            } catch (IOException e) {
                System.err.println("Error saving after deletion: " + e.getMessage());
                return false;
            }
        }
        
        return removed;
    }
    
    /**
     * Deletes a taste entry from the database
     * @param entryId ID of the taste entry to delete
     * @return true if deleted successfully
     */
    public boolean deleteTasteEntry(String entryId) {
        List<TasteEntry> entries = loadTasteEntries();
        boolean removed = false;
        
        for (Iterator<TasteEntry> iterator = entries.iterator(); iterator.hasNext();) {
            TasteEntry entry = iterator.next();
            if (entry.getId().equals(entryId)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (removed) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TASTE_ENTRIES_FILE))) {
                oos.writeObject(entries);
            } catch (IOException e) {
                System.err.println("Error saving after deletion: " + e.getMessage());
                return false;
            }
        }
        
        return removed;
    }
}

/**
 * Engine for analyzing taste data
 */
class AnalyticsEngine {
    private Map<String, Integer> categoryPopularity;
    private Map<String, Double> categoryAverageRatings;
    private Map<String, Double> flavorPopularity;
    
    /**
     * Constructor initializes analytics structures
     */
    public AnalyticsEngine() {
        this.categoryPopularity = new HashMap<>();
        this.categoryAverageRatings = new HashMap<>();
        this.flavorPopularity = new HashMap<>();
    }
    
    public void rebuildAnalytics(List<TasteEntry> tasteEntries) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rebuildAnalytics'");
    }

    /**
     * Process a new taste entry for analytics
     * @param entry The taste entry to process
     */
    public void processEntry(TasteEntry entry) {
        String category = getCategoryFromEntry(entry); // Replace with actual logic

        // Update category popularity
        categoryPopularity.put(category, categoryPopularity.getOrDefault(category, 0) + 1);

        // Update category ratings
        double currentTotal = categoryAverageRatings.getOrDefault(category, 0.0) * 
                             (categoryPopularity.get(category) - 1);
        categoryAverageRatings.put(category, 
                                  (currentTotal + entry.getRating()) / categoryPopularity.get(category));

        // Update flavor popularity
        for (Map.Entry<String, Integer> flavor : entry.getFlavorRatings().entrySet()) {
            flavorPopularity.put(flavor.getKey(), flavorPopularity.getOrDefault(flavor.getKey(), 0.0) + flavor.getValue());
        }
    }

    private String getCategoryFromEntry(TasteEntry entry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCategoryFromEntry'");
    }
}