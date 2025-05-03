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
 * This class provides comprehensive analytics capabilities for taste tracking data,
 * including trend analysis, flavor correlations, and personalized insights.
 */
class AnalyticsEngine {
    private Map<String, Integer> categoryPopularity;
    private Map<String, Double> categoryAverageRatings;
    private Map<String, Double> flavorPopularity;
    private Map<String, Map<String, Integer>> categoryToFlavorMap;
    private Map<String, Map<String, Double>> userCategoryPreferences;
    private Map<String, List<String>> userFavoriteFlavors;
    private Map<String, Double> seasonalTrends;
    private Map<String, Map<String, Integer>> locationPopularity;
    private Map<String, Integer> timeOfDayPreferences;
    private Map<String, List<Double>> ratingDistribution;
    private Map<String, Double> tasteCorrelations;
    private Map<String, Integer> tagPopularity;
    private Map<String, Map<String, Integer>> flavorComboPopularity;
    private Map<String, List<LocalDateTime>> categoryTrends;
    private final int TREND_INTERVAL_DAYS = 30;
    private final int MAX_FAVORITE_TAGS = 10;
    private final int MIN_ENTRIES_FOR_INSIGHT = 5;
    private static final String[] TIME_PERIODS = {
        "Morning (6AM-11AM)", "Noon (11AM-2PM)", "Afternoon (2PM-5PM)", 
        "Evening (5PM-8PM)", "Night (8PM-12AM)", "Late Night (12AM-6AM)"
    };
    
    // Statistical analysis constants
    private static final double CORRELATION_THRESHOLD = 0.7;
    private static final int MIN_DATA_POINTS = 3;
    private static final int PERCENTILE_INTERVALS = 5;
    
    /**
     * Constructor initializes analytics structures
     */
    public AnalyticsEngine() {
        this.categoryPopularity = new HashMap<>();
        this.categoryAverageRatings = new HashMap<>();
        this.flavorPopularity = new HashMap<>();
        this.categoryToFlavorMap = new HashMap<>();
        this.userCategoryPreferences = new HashMap<>();
        this.userFavoriteFlavors = new HashMap<>();
        this.seasonalTrends = new HashMap<>();
        this.locationPopularity = new HashMap<>();
        this.timeOfDayPreferences = new HashMap<>();
        this.ratingDistribution = new HashMap<>();
        this.tasteCorrelations = new HashMap<>();
        this.tagPopularity = new HashMap<>();
        this.flavorComboPopularity = new HashMap<>();
        this.categoryTrends = new HashMap<>();
        
        initializeAnalyticsStructures();
    }
    
    /**
     * Initialize data structures with default values where needed
     */
    private void initializeAnalyticsStructures() {
        // Initialize time periods
        for (String period : TIME_PERIODS) {
            timeOfDayPreferences.put(period, 0);
        }
        
        // Initialize standard flavor correlations
        String[] standardFlavors = {"sweet", "sour", "salty", "bitter", "umami", "spicy"};
        for (String flavor1 : standardFlavors) {
            for (String flavor2 : standardFlavors) {
                if (!flavor1.equals(flavor2)) {
                    tasteCorrelations.put(flavor1 + "-" + flavor2, 0.0);
                }
            }
        }
    }
    
    /**
     * Rebuilds all analytics from a list of taste entries
     * This method should be called when loading data from storage
     * @param tasteEntries List of all taste entries
     */
    public void rebuildAnalytics(List<TasteEntry> tasteEntries) {
        // Clear existing data
        categoryPopularity.clear();
        categoryAverageRatings.clear();
        flavorPopularity.clear();
        categoryToFlavorMap.clear();
        userCategoryPreferences.clear();
        userFavoriteFlavors.clear();
        seasonalTrends.clear();
        locationPopularity.clear();
        timeOfDayPreferences = new HashMap<>();
        ratingDistribution.clear();
        tagPopularity.clear();
        flavorComboPopularity.clear();
        categoryTrends.clear();
        
        // Reinitialize structures
        initializeAnalyticsStructures();
        
        // Process each entry
        for (TasteEntry entry : tasteEntries) {
            processEntry(entry);
        }
        
        // Calculate derived analytics that require the full dataset
        calculateFlavorCorrelations(tasteEntries);
        calculateCategoryTrends();
        calculateLocationInsights();
        calculateUserInsights();
    }

    /**
     * Process a new taste entry for analytics
     * Updates all analytics structures with data from the new entry
     * @param entry The taste entry to process
     */
    public void processEntry(TasteEntry entry) {
        if (entry == null) {
            return;
        }
        
        String category = getCategoryFromEntry(entry);
        String userId = entry.getUserId();
        
        // Update category popularity
        categoryPopularity.put(category, categoryPopularity.getOrDefault(category, 0) + 1);

        // Update category ratings
        updateCategoryRating(category, entry.getRating());

        // Update flavor popularity and mappings
        updateFlavorAnalytics(entry, category);
        
        // Update user preferences
        updateUserPreferences(userId, category, entry);
        
        // Update seasonal trends
        updateSeasonalTrends(entry);
        
        // Update location data
        updateLocationData(entry);
        
        // Update time of day preferences
        updateTimeOfDayPreferences(entry);
        
        // Update rating distribution
        updateRatingDistribution(category, entry.getRating());
        
        // Update tag popularity
        updateTagPopularity(entry);
        
        // Update category trends timeline
        updateCategoryTrends(category, entry.getTimestamp());
    }
    
    /**
     * Gets the category from a taste entry
     * @param entry The taste entry
     * @return The category name
     */
    private String getCategoryFromEntry(TasteEntry entry) {
        // In a real implementation, this would look up the category
        // from the food item database using the foodItemId
        // For now, we'll use a placeholder implementation
        
        // If we had a direct reference to the food items database:
        // return foodItemsDatabase.getById(entry.getFoodItemId()).getCategory();
        
        // Placeholder implementation:
        String foodName = entry.getFoodName().toLowerCase();
        
        if (foodName.contains("pizza") || foodName.contains("pasta") || foodName.contains("lasagna")) {
            return "Italian";
        } else if (foodName.contains("sushi") || foodName.contains("ramen") || foodName.contains("miso")) {
            return "Japanese";
        } else if (foodName.contains("taco") || foodName.contains("burrito") || foodName.contains("quesadilla")) {
            return "Mexican";
        } else if (foodName.contains("curry") || foodName.contains("tikka") || foodName.contains("naan")) {
            return "Indian";
        } else if (foodName.contains("burger") || foodName.contains("fries") || foodName.contains("sandwich")) {
            return "American";
        } else if (foodName.contains("stir fry") || foodName.contains("dumpling") || foodName.contains("spring roll")) {
            return "Chinese";
        } else if (foodName.contains("coffee") || foodName.contains("tea") || foodName.contains("latte")) {
            return "Beverage";
        } else if (foodName.contains("cake") || foodName.contains("cookie") || foodName.contains("pie")) {
            return "Dessert";
        } else if (foodName.contains("salad") || foodName.contains("vegetable") || foodName.contains("vegan")) {
            return "Vegetarian";
        } else {
            return "Other";
        }
    }
    
    /**
     * Updates the average rating for a category
     * @param category The food category
     * @param rating The new rating to incorporate
     */
    private void updateCategoryRating(String category, int rating) {
        int count = categoryPopularity.getOrDefault(category, 0);
        double currentTotal = categoryAverageRatings.getOrDefault(category, 0.0) * (count - 1);
        
        if (count > 0) {
            categoryAverageRatings.put(category, (currentTotal + rating) / count);
        } else {
            categoryAverageRatings.put(category, (double) rating);
        }
    }
    
    /**
     * Updates flavor-related analytics
     * @param entry The taste entry containing flavor ratings
     * @param category The food category
     */
    private void updateFlavorAnalytics(TasteEntry entry, String category) {
        Map<String, Integer> entryFlavorRatings = entry.getFlavorRatings();
        
        // Update overall flavor popularity
        for (Map.Entry<String, Integer> flavorEntry : entryFlavorRatings.entrySet()) {
            String flavor = flavorEntry.getKey();
            int rating = flavorEntry.getValue();
            
            flavorPopularity.put(flavor, flavorPopularity.getOrDefault(flavor, 0.0) + rating);
            
            // Update category to flavor mapping
            Map<String, Integer> flavorMap = categoryToFlavorMap.getOrDefault(category, new HashMap<>());
            flavorMap.put(flavor, flavorMap.getOrDefault(flavor, 0) + 1);
            categoryToFlavorMap.put(category, flavorMap);
            
            // Update flavor combinations (pairs of flavors that appear together)
            updateFlavorCombinations(entryFlavorRatings.keySet());
        }
    }
    
    /**
     * Updates the popularity count of flavor combinations
     * @param flavors Set of flavors that appear together
     */
    private void updateFlavorCombinations(Set<String> flavors) {
        if (flavors.size() < 2) return;
        
        // Convert to sorted list to ensure consistent combination keys
        List<String> flavorList = new ArrayList<>(flavors);
        Collections.sort(flavorList);
        
        // Update counts for each pair of flavors
        for (int i = 0; i < flavorList.size(); i++) {
            for (int j = i + 1; j < flavorList.size(); j++) {
                String flavor1 = flavorList.get(i);
                String flavor2 = flavorList.get(j);
                String comboKey = flavor1 + "+" + flavor2;
                
                Map<String, Integer> combos = flavorComboPopularity.getOrDefault(flavor1, new HashMap<>());
                combos.put(flavor2, combos.getOrDefault(flavor2, 0) + 1);
                flavorComboPopularity.put(flavor1, combos);
            }
        }
    }
    
    /**
     * Updates user preference data
     * @param userId User identifier
     * @param category Food category
     * @param entry The taste entry
     */
    private void updateUserPreferences(String userId, String category, TasteEntry entry) {
        // Update category preferences
        Map<String, Double> userCategories = userCategoryPreferences.getOrDefault(userId, new HashMap<>());
        userCategories.put(category, userCategories.getOrDefault(category, 0.0) + entry.getRating());
        userCategoryPreferences.put(userId, userCategories);
        
        // Update favorite flavors (keeping top rated flavors)
        if (!entry.getFlavorRatings().isEmpty()) {
            List<String> favorites = userFavoriteFlavors.getOrDefault(userId, new ArrayList<>());
            
            // Add new flavors rated highly (4-5)
            for (Map.Entry<String, Integer> flavorEntry : entry.getFlavorRatings().entrySet()) {
                if (flavorEntry.getValue() >= 4 && !favorites.contains(flavorEntry.getKey())) {
                    favorites.add(flavorEntry.getKey());
                }
            }
            
            // Keep list from growing too large
            if (favorites.size() > MAX_FAVORITE_TAGS) {
                favorites = favorites.subList(0, MAX_FAVORITE_TAGS);
            }
            
            userFavoriteFlavors.put(userId, favorites);
        }
    }
    
    /**
     * Updates seasonal trend data
     * @param entry The taste entry
     */
    private void updateSeasonalTrends(TasteEntry entry) {
        if (entry.getTimestamp() != null) {
            int month = entry.getTimestamp().getMonthValue();
            String season;
            
            // Determine season (Northern Hemisphere)
            if (month == 12 || month == 1 || month == 2) {
                season = "Winter";
            } else if (month >= 3 && month <= 5) {
                season = "Spring";
            } else if (month >= 6 && month <= 8) {
                season = "Summer";
            } else {
                season = "Fall";
            }
            
            // Update seasonal popularity
            String seasonCategory = season + "-" + getCategoryFromEntry(entry);
            seasonalTrends.put(seasonCategory, seasonalTrends.getOrDefault(seasonCategory, 0.0) + entry.getRating());
        }
    }
    
    /**
     * Updates location-based analytics
     * @param entry The taste entry
     */
    private void updateLocationData(TasteEntry entry) {
        if (entry.getLocation() != null && !entry.getLocation().isEmpty()) {
            String location = entry.getLocation();
            String category = getCategoryFromEntry(entry);
            
            // Update location to category mapping
            Map<String, Integer> categories = locationPopularity.getOrDefault(location, new HashMap<>());
            categories.put(category, categories.getOrDefault(category, 0) + 1);
            locationPopularity.put(location, categories);
        }
    }
    
    /**
     * Updates time of day preference analytics
     * @param entry The taste entry
     */
    private void updateTimeOfDayPreferences(TasteEntry entry) {
        if (entry.getTimestamp() != null) {
            int hour = entry.getTimestamp().getHour();
            String timePeriod;
            
            // Determine time period
            if (hour >= 6 && hour < 11) {
                timePeriod = "Morning (6AM-11AM)";
            } else if (hour >= 11 && hour < 14) {
                timePeriod = "Noon (11AM-2PM)";
            } else if (hour >= 14 && hour < 17) {
                timePeriod = "Afternoon (2PM-5PM)";
            } else if (hour >= 17 && hour < 20) {
                timePeriod = "Evening (5PM-8PM)";
            } else if (hour >= 20 && hour < 24) {
                timePeriod = "Night (8PM-12AM)";
            } else {
                timePeriod = "Late Night (12AM-6AM)";
            }
            
            // Update preference count
            timeOfDayPreferences.put(timePeriod, timeOfDayPreferences.getOrDefault(timePeriod, 0) + 1);
        }
    }
    
    /**
     * Updates rating distribution for categories
     * @param category The food category
     * @param rating The rating value
     */
    private void updateRatingDistribution(String category, int rating) {
        List<Double> ratings = ratingDistribution.getOrDefault(category, new ArrayList<>());
        ratings.add((double) rating);
        ratingDistribution.put(category, ratings);
    }
    
    /**
     * Updates tag popularity analytics
     * @param entry The taste entry with tags
     */
    private void updateTagPopularity(TasteEntry entry) {
        for (String tag : entry.getTags()) {
            tagPopularity.put(tag, tagPopularity.getOrDefault(tag, 0) + 1);
        }
    }
    
    /**
     * Updates category trend timeline data
     * @param category The food category
     * @param timestamp When the entry was created
     */
    private void updateCategoryTrends(String category, LocalDateTime timestamp) {
        if (timestamp != null) {
            List<LocalDateTime> timestamps = categoryTrends.getOrDefault(category, new ArrayList<>());
            timestamps.add(timestamp);
            categoryTrends.put(category, timestamps);
        }
    }
    
    /**
     * Calculates taste correlations based on all entries
     * @param entries List of taste entries
     */
    private void calculateFlavorCorrelations(List<TasteEntry> entries) {
        Map<String, List<Integer>> flavorRatings = new HashMap<>();
        
        // Collect all flavor ratings
        for (TasteEntry entry : entries) {
            for (Map.Entry<String, Integer> flavorEntry : entry.getFlavorRatings().entrySet()) {
                String flavor = flavorEntry.getKey();
                int rating = flavorEntry.getValue();
                
                List<Integer> ratings = flavorRatings.getOrDefault(flavor, new ArrayList<>());
                ratings.add(rating);
                flavorRatings.put(flavor, ratings);
            }
        }
        
        // Calculate correlations between flavors
        List<String> flavors = new ArrayList<>(flavorRatings.keySet());
        for (int i = 0; i < flavors.size(); i++) {
            for (int j = i + 1; j < flavors.size(); j++) {
                String flavor1 = flavors.get(i);
                String flavor2 = flavors.get(j);
                
                List<Integer> ratings1 = flavorRatings.get(flavor1);
                List<Integer> ratings2 = flavorRatings.get(flavor2);
                
                // Only calculate correlation if we have enough overlapping data points
                if (ratings1.size() >= MIN_DATA_POINTS && ratings2.size() >= MIN_DATA_POINTS) {
                    double correlation = calculateCorrelation(ratings1, ratings2);
                    
                    tasteCorrelations.put(flavor1 + "-" + flavor2, correlation);
                    tasteCorrelations.put(flavor2 + "-" + flavor1, correlation);
                }
            }
        }
    }
    
    /**
     * Calculates the Pearson correlation coefficient between two lists of ratings
     * @param list1 First list of ratings
     * @param list2 Second list of ratings
     * @return Correlation coefficient (-1 to 1)
     */
    private double calculateCorrelation(List<Integer> list1, List<Integer> list2) {
        // Ensure the lists are the same size by using the smaller size
        int size = Math.min(list1.size(), list2.size());
        if (size < 2) return 0; // Need at least 2 points for correlation
        
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;
        
        // Calculate sums
        for (int i = 0; i < size; i++) {
            double val1 = list1.get(i);
            double val2 = list2.get(i);
            
            sum1 += val1;
            sum2 += val2;
            sum1Sq += val1 * val1;
            sum2Sq += val2 * val2;
            pSum += val1 * val2;
        }
        
        // Calculate Pearson correlation coefficient
        double num = pSum - (sum1 * sum2 / size);
        double den = Math.sqrt((sum1Sq - sum1 * sum1 / size) * (sum2Sq - sum2 * sum2 / size));
        
        if (den == 0) return 0;
        return num / den;
    }
    
    /**
     * Calculates category trends over time
     */
    private void calculateCategoryTrends() {
        // This would analyze the frequency of categories over time intervals
        // to detect increasing or decreasing popularity
        
        // For each category, analyze its trend over time periods
        for (Map.Entry<String, List<LocalDateTime>> entry : categoryTrends.entrySet()) {
            String category = entry.getKey();
            List<LocalDateTime> timestamps = entry.getValue();
            
            // Sort timestamps chronologically
            Collections.sort(timestamps);
            
            // Analyze the trend (simplified version)
            if (timestamps.size() >= MIN_DATA_POINTS) {
                LocalDateTime firstTimestamp = timestamps.get(0);
                LocalDateTime lastTimestamp = timestamps.get(timestamps.size() - 1);
                
                // Calculate days between first and last entry
                long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(firstTimestamp, lastTimestamp);
                
                if (daysBetween > 0) {
                    // Calculate entries per day - higher value means increasing popularity
                    double entriesPerDay = timestamps.size() / (double) daysBetween;
                    
                    // Store trend information (could be expanded to more sophisticated analysis)
                    seasonalTrends.put(category + "-trend", entriesPerDay);
                }
            }
        }
    }
    
    /**
     * Calculates insights based on location data
     */
    private void calculateLocationInsights() {
        // This would find the most popular categories by location
        // and detect location-specific preferences
        
        // For demonstration, we'll just count the entries
        for (Map.Entry<String, Map<String, Integer>> entry : locationPopularity.entrySet()) {
            // Additional calculations could be performed here
        }
    }
    
    /**
     * Calculates insights for individual users
     */
    private void calculateUserInsights() {
        // This would analyze user preferences and generate personalized insights
        
        // For each user, calculate their preference profile
        for (Map.Entry<String, Map<String, Double>> entry : userCategoryPreferences.entrySet()) {
            String userId = entry.getKey();
            Map<String, Double> preferences = entry.getValue();
            
            // Sort categories by preference score
            List<Map.Entry<String, Double>> sortedPreferences = new ArrayList<>(preferences.entrySet());
            sortedPreferences.sort(Map.Entry.<String, Double>comparingByValue().reversed());
            
            // Store user's top categories for recommendation purposes
            if (!sortedPreferences.isEmpty()) {
                List<String> topCategories = new ArrayList<>();
                for (int i = 0; i < Math.min(3, sortedPreferences.size()); i++) {
                    topCategories.add(sortedPreferences.get(i).getKey());
                }
                
                // User insights could be stored here for later recommendation use
            }
        }
    }
    
    /**
     * Gets the most popular categories ranked by number of entries
     * @param limit Maximum number of categories to return
     * @return Map of categories to their popularity count
     */
    public Map<String, Integer> getTopCategories(int limit) {
        return sortMapByValueAndLimit(categoryPopularity, limit, true);
    }
    
    /**
     * Gets the highest rated categories
     * @param limit Maximum number of categories to return
     * @return Map of categories to their average rating
     */
    public Map<String, Double> getHighestRatedCategories(int limit) {
        // Filter to categories with minimum number of ratings
        Map<String, Double> filteredRatings = new HashMap<>();
        for (Map.Entry<String, Double> entry : categoryAverageRatings.entrySet()) {
            String category = entry.getKey();
            if (categoryPopularity.getOrDefault(category, 0) >= MIN_ENTRIES_FOR_INSIGHT) {
                filteredRatings.put(category, entry.getValue());
            }
        }
        
        return sortMapByValueAndLimit(filteredRatings, limit, true);
    }
    
    /**
     * Gets the most popular flavors based on user ratings
     * @param limit Maximum number of flavors to return
     * @return Map of flavors to their popularity score
     */
    public Map<String, Double> getTopFlavors(int limit) {
        return sortMapByValueAndLimit(flavorPopularity, limit, true);
    }
    
    /**
     * Gets the most popular flavor combinations
     * @param limit Maximum number of combinations to return
     * @return Map of flavor combinations to their popularity count
     */
    public Map<String, Integer> getPopularFlavorCombinations(int limit) {
        // Convert nested map to flat map of combination strings
        Map<String, Integer> combinations = new HashMap<>();
        
        for (Map.Entry<String, Map<String, Integer>> entry : flavorComboPopularity.entrySet()) {
            String flavor1 = entry.getKey();
            for (Map.Entry<String, Integer> subEntry : entry.getValue().entrySet()) {
                String flavor2 = subEntry.getKey();
                int count = subEntry.getValue();
                
                // Create a consistent key for the combination
                String[] flavors = {flavor1, flavor2};
                Arrays.sort(flavors);
                String comboKey = flavors[0] + " + " + flavors[1];
                
                combinations.put(comboKey, count);
            }
        }
        
        return sortMapByValueAndLimit(combinations, limit, true);
    }
    
    /**
     * Gets flavor correlations above the threshold value
     * @return Map of flavor pairs to their correlation coefficient
     */
    public Map<String, Double> getSignificantFlavorCorrelations() {
        Map<String, Double> significantCorrelations = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : tasteCorrelations.entrySet()) {
            if (Math.abs(entry.getValue()) >= CORRELATION_THRESHOLD) {
                significantCorrelations.put(entry.getKey(), entry.getValue());
            }
        }
        
        return sortMapByValueAndLimit(significantCorrelations, Integer.MAX_VALUE, true);
    }
    
    /**
     * Gets the most popular tags used in taste entries
     * @param limit Maximum number of tags to return
     * @return Map of tags to their popularity count
     */
    public Map<String, Integer> getPopularTags(int limit) {
        return sortMapByValueAndLimit(tagPopularity, limit, true);
    }
    
    /**
     * Gets the most popular categories by season
     * @return Map of season-category pairs to their popularity score
     */
    public Map<String, Double> getSeasonalPreferences() {
        return sortMapByValueAndLimit(seasonalTrends, 20, true);
    }
    
    /**
     * Gets the time of day preferences for food entries
     * @return Map of time periods to their entry count
     */
    public Map<String, Integer> getTimeOfDayPreferences() {
        // Sort by time order, not by popularity
        Map<String, Integer> sortedPreferences = new LinkedHashMap<>();
        for (String period : TIME_PERIODS) {
            sortedPreferences.put(period, timeOfDayPreferences.getOrDefault(period, 0));
        }
        return sortedPreferences;
    }
    
    /**
     * Gets the statistical distribution of ratings for a category
     * @param category The food category to analyze
     * @return Map of percentile values to rating scores
     */
    public Map<Integer, Double> getRatingDistribution(String category) {
        List<Double> ratings = ratingDistribution.getOrDefault(category, new ArrayList<>());
        Map<Integer, Double> distribution = new HashMap<>();
        
        if (ratings.size() >= MIN_DATA_POINTS) {
            // Sort ratings
            Collections.sort(ratings);
            
            // Calculate percentiles
            for (int i = 0; i <= 100; i += 100 / PERCENTILE_INTERVALS) {
                int index = (int) Math.ceil(ratings.size() * i / 100.0) - 1;
                if (index >= 0 && index < ratings.size()) {
                    distribution.put(i, ratings.get(index));
                }
            }
        }
        
        return distribution;
    }
    
    /**
     * Gets personalized flavor recommendations for a user
     * @param userId The user ID
     * @param limit Maximum number of recommendations
     * @return List of recommended flavors
     */
    public List<String> getFlavorRecommendations(String userId, int limit) {
        // Get user's favorite flavors
        List<String> userFlavors = userFavoriteFlavors.getOrDefault(userId, new ArrayList<>());
        Set<String> recommendations = new HashSet<>();
        
        // For each of the user's favorite flavors, find correlated flavors
        for (String flavor : userFlavors) {
            for (Map.Entry<String, Double> entry : tasteCorrelations.entrySet()) {
                if (entry.getKey().startsWith(flavor + "-") && entry.getValue() > CORRELATION_THRESHOLD) {
                    String otherFlavor = entry.getKey().substring(flavor.length() + 1);
                    if (!userFlavors.contains(otherFlavor)) {
                        recommendations.add(otherFlavor);
                    }
                }
            }
        }
        
        // Convert to list and limit results
        List<String> result = new ArrayList<>(recommendations);
        if (result.size() > limit) {
            result = result.subList(0, limit);
        }
        
        return result;
    }
    
    /**
     * Generates a complete trend report with insights
     * @return String containing a formatted trend report
     */
    public String generateTrendReport() {
        StringBuilder report = new StringBuilder();
        report.append("===== Taste Trends Analysis Report =====\n\n");
        
        // Top categories
        report.append("TOP CATEGORIES BY POPULARITY:\n");
        Map<String, Integer> topCategories = getTopCategories(5);
        for (Map.Entry<String, Integer> entry : topCategories.entrySet()) {
            report.append(String.format("- %s: %d entries\n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");
        
        // Highest rated categories
        report.append("HIGHEST RATED CATEGORIES:\n");
        Map<String, Double> topRated = getHighestRatedCategories(5);
        for (Map.Entry<String, Double> entry : topRated.entrySet()) {
            report.append(String.format("- %s: %.2f average rating\n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");
        
        // Popular flavors
        report.append("MOST POPULAR FLAVORS:\n");
        Map<String, Double> topFlavors = getTopFlavors(5);
        for (Map.Entry<String, Double> entry : topFlavors.entrySet()) {
            report.append(String.format("- %s: %.2f popularity score\n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");
        
        // Popular flavor combinations
        report.append("POPULAR FLAVOR COMBINATIONS:\n");
        Map<String, Integer> topCombos = getPopularFlavorCombinations(5);
        for (Map.Entry<String, Integer> entry : topCombos.entrySet()) {
            report.append(String.format("- %s: %d occurrences\n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");
        
        // Time of day preferences
        report.append("TIME OF DAY PREFERENCES:\n");
        Map<String, Integer> timePreferences = getTimeOfDayPreferences();
        for (Map.Entry<String, Integer> entry : timePreferences.entrySet()) {
            report.append(String.format("- %s: %d entries\n", entry.getKey(), entry.getValue()));
        }
        report.append("\n");
        
        // Seasonal preferences
        report.append("SEASONAL TRENDS:\n");
        Map<String, Double> seasonalPrefs = getSeasonalPreferences();
        for (Map.Entry<String, Double> entry : seasonalPrefs.entrySet()) {
            if (entry.getKey().contains("-trend")) {
                String category = entry.getKey().replace("-trend", "");
                String trend = entry.getValue() > 0.1 ? "Increasing" : "Stable";
                report.append(String.format("- %s: %s popularity trend\n", category, trend));
            }
        }
        
        report.append("\n=== End of Report ===\n");
        return report.toString();
    }
    
    /**
     * Utility method to sort a map by value and return top N entries
     * @param <K> Key type
     * @param <V> Value type that extends Comparable
     * @param map The map to sort
     * @param limit Maximum number of entries to return
     * @param descending Whether to sort in descending order
     * @return A new map with the top N entries
     */
    private <K, V extends Comparable<? super V>> Map<K, V> sortMapByValueAndLimit(
            Map<K, V> map, int limit, boolean descending) {
        List<Map.Entry<K, V>> entries = new ArrayList<>(map.entrySet());
        
        // Sort entries
        if (descending) {
            entries.sort(Map.Entry.<K, V>comparingByValue().reversed());
        } else {
            entries.sort(Map.Entry.comparingByValue());
        }
        
        // Return top N entries
        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(limit, entries.size()); i++) {
            Map.Entry<K, V> entry = entries.get(i);
            result.put(entry.getKey(), entry.getValue());
        }
        
        return result;
    }
}