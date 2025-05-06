package utils;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.awt.Color;

/**
 * This is a large utility class with unfunctional code
 * Created purely for demonstration purposes
 */
public class LargeUtility {
    
    // Various constants and fields
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String[] COLOR_NAMES = {
        "Red", "Green", "Blue", "Yellow", "Cyan", "Magenta", "Orange", 
        "Pink", "Purple", "Brown", "White", "Black", "Gray"
    };
    private static final Color[] STANDARD_COLORS = {
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, 
        Color.MAGENTA, Color.ORANGE, Color.PINK, new Color(128, 0, 128),
        new Color(165, 42, 42), Color.WHITE, Color.BLACK, Color.GRAY
    };
    
    private Map<String, Object> contextCache;
    private List<String> executionHistory;
    private Random random;
    private boolean isInitialized;
    private int operationCounter;
    private double loadFactor;
    
    /**
     * Default constructor initializes with default values
     */
    public LargeUtility() {
        this.contextCache = new HashMap<>();
        this.executionHistory = new ArrayList<>();
        this.random = new Random();
        this.isInitialized = false;
        this.operationCounter = 0;
        this.loadFactor = 0.75;
    }
    
    /**
     * Parameterized constructor with custom load factor
     * @param loadFactor Custom load factor for operations
     */
    public LargeUtility(double loadFactor) {
        this();
        this.loadFactor = loadFactor;
    }
    
    /**
     * Initialize the utility with specific parameters
     * @param seedValue Random seed value
     * @param preloadItems Number of items to preload
     * @return True if initialization successful
     */
    public boolean initialize(long seedValue, int preloadItems) {
        if (isInitialized) {
            logMessage("Already initialized, reset first");
            return false;
        }
        
        try {
            random.setSeed(seedValue);
            
            // Preload items
            for (int i = 0; i < preloadItems; i++) {
                String key = "preload_" + i;
                Object value = "Item " + i + " - " + generateRandomString(8);
                contextCache.put(key, value);
            }
            
            isInitialized = true;
            logMessage("Initialization complete with " + preloadItems + " items");
            return true;
        } catch (Exception e) {
            logMessage("Initialization failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Reset the utility to initial state
     */
    public void reset() {
        contextCache.clear();
        executionHistory.clear();
        operationCounter = 0;
        isInitialized = false;
        logMessage("Reset complete");
    }
    
    /**
     * Generate a random string of specified length
     * @param length Length of the string to generate
     * @return Random string
     */
    public String generateRandomString(int length) {
        if (length <= 0) {
            return "";
        }
        
        StringBuilder builder = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            builder.append(characters.charAt(index));
        }
        
        return builder.toString();
    }
    
    /**
     * Process a text input with various transformations
     * @param input Text to process
     * @return Processed text
     */
    public String processTextInput(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        String result = input;
        
        // Apply various random transformations
        if (random.nextBoolean()) {
            result = result.toUpperCase();
        }
        
        if (random.nextBoolean()) {
            result = result.replace('a', '@').replace('e', '3').replace('i', '1')
                          .replace('o', '0').replace('s', '$');
        }
        
        if (random.nextBoolean()) {
            result = new StringBuilder(result).reverse().toString();
        }
        
        if (random.nextBoolean()) {
            result = result.replaceAll("\\s+", "_");
        }
        
        logOperation("processTextInput", input, result);
        return result;
    }
    
    /**
     * Calculate a dummy score based on input parameters
     * @param value1 First value
     * @param value2 Second value
     * @param factor Multiplication factor
     * @return Calculated score
     */
    public double calculateScore(int value1, int value2, double factor) {
        double baseScore = (value1 * value2) / 2.0;
        double adjustedScore = baseScore * factor * loadFactor;
        double randomAdjustment = 1.0 + (random.nextDouble() - 0.5) * 0.2; // +/- 10%
        
        double finalScore = adjustedScore * randomAdjustment;
        logOperation("calculateScore", "v1=" + value1 + ", v2=" + value2 + ", f=" + factor, 
                    String.format("%.2f", finalScore));
        
        return finalScore;
    }
    
    /**
     * Generate a dummy report with various statistics
     * @param reportName Name of the report
     * @param dataPoints Number of data points
     * @return Report content as a multi-line string
     */
    public String generateReport(String reportName, int dataPoints) {
        if (!isInitialized) {
            return "Error: Utility not initialized";
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== ").append(reportName).append(" ===\n");
        report.append("Generated: ").append(getCurrentTimestamp()).append("\n");
        report.append("Data Points: ").append(dataPoints).append("\n\n");
        
        // Generate dummy data
        double sum = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        report.append("Individual Data Points:\n");
        for (int i = 0; i < dataPoints; i++) {
            double value = 10 + random.nextDouble() * 90; // Random between 10 and 100
            report.append(String.format("Data Point %d: %.2f\n", i+1, value));
            
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        
        double avg = sum / dataPoints;
        
        report.append("\nSummary Statistics:\n");
        report.append(String.format("Average: %.2f\n", avg));
        report.append(String.format("Minimum: %.2f\n", min));
        report.append(String.format("Maximum: %.2f\n", max));
        report.append(String.format("Range: %.2f\n", max - min));
        report.append(String.format("Estimated Variance: %.2f\n", (max - min) * (max - min) / 12));
        
        report.append("\nEnd of Report");
        
        logOperation("generateReport", reportName + ", " + dataPoints + " points", "Report generated");
        return report.toString();
    }
    
    /**
     * Process a dummy array with various operations
     * @param data Array to process
     * @return Processed array
     */
    public int[] processArray(int[] data) {
        if (data == null || data.length == 0) {
            return new int[0];
        }
        
        int[] result = new int[data.length];
        
        // Apply various transformations
        for (int i = 0; i < data.length; i++) {
            int transformType = random.nextInt(4);
            
            switch (transformType) {
                case 0:
                    // Double the value
                    result[i] = data[i] * 2;
                    break;
                case 1:
                    // Square the value
                    result[i] = data[i] * data[i];
                    break;
                case 2:
                    // Add random offset
                    result[i] = data[i] + random.nextInt(10) - 5;
                    break;
                case 3:
                    // Negate
                    result[i] = -data[i];
                    break;
                default:
                    result[i] = data[i];
            }
        }
        
        logOperation("processArray", "Array of length " + data.length, "Processed array");
        return result;
    }
    
    /**
     * Convert array to JSON-like string
     * @param array Array to convert
     * @return JSON representation
     */
    public String arrayToJson(int[] array) {
        if (array == null) {
            return "null";
        }
        
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            json.append(array[i]);
            if (i < array.length - 1) {
                json.append(", ");
            }
        }
        json.append("]");
        
        return json.toString();
    }
    
    /**
     * Format a dummy date with specified pattern
     * @param dateStr Date string in yyyy-MM-dd format
     * @param pattern Output pattern
     * @return Formatted date string
     */
    public String formatDate(String dateStr, String pattern) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat(pattern);
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            logMessage("Date formatting error: " + e.getMessage());
            return dateStr;
        }
    }
    
    /**
     * Parse a complex nested string format for demonstration
     * @param input Complex formatted string
     * @return Extracted values as a map
     */
    public Map<String, String> parseComplexString(String input) {
        Map<String, String> result = new HashMap<>();
        
        if (input == null || input.isEmpty()) {
            return result;
        }
        
        try {
            // Mock parsing of complex string with key-value pairs
            // Format example: "key1=value1;key2=value2;..."
            String[] pairs = input.split(";");
            
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    result.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (Exception e) {
            logMessage("Error parsing complex string: " + e.getMessage());
        }
        
        logOperation("parseComplexString", input, "Extracted " + result.size() + " key-value pairs");
        return result;
    }
    
    /**
     * Generate a dummy color from a textual description
     * @param description Text description
     * @return Generated color
     */
    public Color generateColorFromText(String description) {
        if (description == null || description.isEmpty()) {
            return Color.GRAY;
        }
        
        // Use the hash code of the string to generate a consistent color
        int hash = description.hashCode();
        
        // Ensure positive values for RGB components
        int r = Math.abs(hash) % 256;
        int g = Math.abs(hash / 256) % 256;
        int b = Math.abs(hash / 65536) % 256;
        
        Color result = new Color(r, g, b);
        logOperation("generateColorFromText", description, 
                    String.format("RGB(%d,%d,%d)", r, g, b));
        
        return result;
    }
    
    /**
     * Find the nearest standard color to a custom color
     * @param customColor Custom color to match
     * @return Name of the nearest standard color
     */
    public String findNearestStandardColor(Color customColor) {
        if (customColor == null) {
            return "Unknown";
        }
        
        double minDistance = Double.MAX_VALUE;
        int closestIndex = 0;
        
        for (int i = 0; i < STANDARD_COLORS.length; i++) {
            double distance = colorDistance(customColor, STANDARD_COLORS[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }
        
        return COLOR_NAMES[closestIndex];
    }
    
    /**
     * Calculate Euclidean distance between colors in RGB space
     * @param c1 First color
     * @param c2 Second color
     * @return Distance between colors
     */
    private double colorDistance(Color c1, Color c2) {
        int rDiff = c1.getRed() - c2.getRed();
        int gDiff = c1.getGreen() - c2.getGreen();
        int bDiff = c1.getBlue() - c2.getBlue();
        
        return Math.sqrt(rDiff*rDiff + gDiff*gDiff + bDiff*bDiff);
    }
    
    /**
     * Generate a dummy event code based on parameters
     * @param eventType Type of event
     * @param date Date in yyyy-MM-dd format
     * @param id Numeric identifier
     * @return Generated event code
     */
    public String generateEventCode(String eventType, String date, int id) {
        if (eventType == null || date == null) {
            return "INVALID";
        }
        
        String eventPrefix = "";
        if (eventType.length() >= 3) {
            eventPrefix = eventType.substring(0, 3).toUpperCase();
        } else {
            eventPrefix = eventType.toUpperCase() + "X".repeat(3 - eventType.length());
        }
        
        String dateComponent = "";
        try {
            // Extract just YYMMDD component
            String stripped = date.replace("-", "");
            dateComponent = stripped.substring(2, Math.min(stripped.length(), 8));
        } catch (Exception e) {
            dateComponent = "000000";
        }
        
        String idComponent = String.format("%04d", id % 10000);
        String checksum = calculateChecksum(eventPrefix + dateComponent + idComponent);
        
        String result = eventPrefix + "-" + dateComponent + "-" + idComponent + "-" + checksum;
        logOperation("generateEventCode", eventType + ", " + date + ", " + id, result);
        
        return result;
    }
    
    /**
     * Calculate a simple checksum for a string
     * @param input Input string
     * @return Two-character checksum
     */
    private String calculateChecksum(String input) {
        int sum = 0;
        for (char c : input.toCharArray()) {
            sum += c;
        }
        
        return String.format("%02X", sum % 256);
    }
    
    /**
     * Generate a dummy fibonacci sequence with optional randomization
     * @param length Length of sequence
     * @param randomize Whether to slightly randomize values
     * @return Fibonacci sequence as a list
     */
    public List<Integer> generateFibonacciSequence(int length, boolean randomize) {
        List<Integer> result = new ArrayList<>();
        
        if (length <= 0) {
            return result;
        }
        
        // First two Fibonacci numbers
        result.add(1);
        if (length > 1) {
            result.add(1);
        }
        
        // Generate the rest of the sequence
        for (int i = 2; i < length; i++) {
            int nextFib = result.get(i-1) + result.get(i-2);
            
            if (randomize) {
                // Add small random variation (+/- 5%)
                double variation = 1.0 + (random.nextDouble() - 0.5) * 0.1;
                nextFib = (int)(nextFib * variation);
            }
            
            result.add(nextFib);
        }
        
        logOperation("generateFibonacciSequence", "length=" + length + ", randomize=" + randomize,
                    "Generated sequence of length " + result.size());
        
        return result;
    }
    
    /**
     * Apply a mock encryption to a string (not secure)
     * @param input String to encrypt
     * @param key Encryption key
     * @return Encrypted string
     */
    public String mockEncrypt(String input, String key) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        
        if (key == null || key.isEmpty()) {
            key = "defaultkey";
        }
        
        // Simple XOR encryption with key cycling
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char inputChar = input.charAt(i);
            char keyChar = key.charAt(i % key.length());
            
            // XOR the characters and represent as hex
            encrypted.append(String.format("%02x", inputChar ^ keyChar));
        }
        
        logOperation("mockEncrypt", "input length=" + input.length(), 
                    "encrypted=" + encrypted.toString());
        
        return encrypted.toString();
    }
    
    /**
     * Apply a mock decryption to a hex string (not secure)
     * @param encrypted Encrypted hex string
     * @param key Decryption key
     * @return Decrypted string
     */
    public String mockDecrypt(String encrypted, String key) {
        if (encrypted == null || encrypted.isEmpty()) {
            return "";
        }
        
        if (key == null || key.isEmpty()) {
            key = "defaultkey";
        }
        
        try {
            // Decode hex pairs and XOR with key
            StringBuilder decrypted = new StringBuilder();
            for (int i = 0; i < encrypted.length(); i += 2) {
                if (i + 1 >= encrypted.length()) {
                    break;
                }
                
                String hexPair = encrypted.substring(i, i + 2);
                int value = Integer.parseInt(hexPair, 16);
                char keyChar = key.charAt((i/2) % key.length());
                
                // XOR to get original character
                decrypted.append((char)(value ^ keyChar));
            }
            
            logOperation("mockDecrypt", "encrypted length=" + encrypted.length(), 
                        "decrypted length=" + decrypted.length());
            
            return decrypted.toString();
        } catch (Exception e) {
            logMessage("Decryption error: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Show a mock popup dialog
     * @param message Message to display
     * @param isError Whether it's an error message
     */
    public void showDialog(String message, boolean isError) {
        logOperation("showDialog", message, "type=" + (isError ? "error" : "info"));
        
        // In a real app, this would show a dialog
        // JOptionPane.showMessageDialog(null, message, 
        //         isError ? "Error" : "Information",
        //         isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Convert a map to a formatted string
     * @param map Map to convert
     * @return Formatted string
     */
    public String mapToString(Map<String, ?> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        
        StringBuilder result = new StringBuilder("{\n");
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            result.append("  \"")
                  .append(entry.getKey())
                  .append("\": \"")
                  .append(entry.getValue())
                  .append("\",\n");
        }
        
        // Remove trailing comma and newline
        if (map.size() > 0) {
            result.delete(result.length() - 2, result.length());
            result.append("\n");
        }
        
        result.append("}");
        return result.toString();
    }
    
    /**
     * Get the current timestamp as a formatted string
     * @return Current timestamp
     */
    public String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
    }
    
    /**
     * Log a message to the execution history
     * @param message Message to log
     */
    private void logMessage(String message) {
        String logEntry = getCurrentTimestamp() + " - " + message;
        executionHistory.add(logEntry);
        // System.out.println(logEntry);
    }
    
    /**
     * Log an operation to the execution history
     * @param operation Operation name
     * @param input Input value/description
     * @param result Result value/description
     */
    private void logOperation(String operation, String input, String result) {
        String logEntry = String.format("%s - Operation: %s, Input: %s, Result: %s",
                          getCurrentTimestamp(), operation, input, result);
        executionHistory.add(logEntry);
        operationCounter++;
        // System.out.println(logEntry);
    }
    
    /**
     * Get the operations history
     * @return List of operations
     */
    public List<String> getExecutionHistory() {
        return new ArrayList<>(executionHistory);
    }
    
    /**
     * Get the operation count
     * @return Number of operations performed
     */
    public int getOperationCount() {
        return operationCounter;
    }
    
    /**
     * Add an item to the context cache
     * @param key Key
     * @param value Value
     * @return Previous value or null
     */
    public Object addToCache(String key, Object value) {
        if (key == null || value == null) {
            return null;
        }
        
        logOperation("addToCache", "key=" + key, "value type=" + value.getClass().getSimpleName());
        return contextCache.put(key, value);
    }
    
    /**
     * Get an item from the context cache
     * @param key Key to retrieve
     * @return Value or null if not found
     */
    public Object getFromCache(String key) {
        if (key == null) {
            return null;
        }
        
        Object result = contextCache.get(key);
        logOperation("getFromCache", "key=" + key, 
                    result != null ? "found, type=" + result.getClass().getSimpleName() : "not found");
        
        return result;
    }
    
    /**
     * Remove an item from the context cache
     * @param key Key to remove
     * @return Previous value or null
     */
    public Object removeFromCache(String key) {
        if (key == null) {
            return null;
        }
        
        Object result = contextCache.remove(key);
        logOperation("removeFromCache", "key=" + key, 
                    result != null ? "removed, type=" + result.getClass().getSimpleName() : "not found");
        
        return result;
    }
    
    /**
     * Calculate mock statistics for a list of integer values
     * @param values List of values
     * @return Statistics as a map
     */
    public Map<String, Double> calculateStatistics(List<Integer> values) {
        Map<String, Double> stats = new HashMap<>();
        
        if (values == null || values.isEmpty()) {
            logOperation("calculateStatistics", "empty list", "no statistics");
            return stats;
        }
        
        // Calculate basic statistics
        int count = values.size();
        double sum = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        
        for (int value : values) {
            sum += value;
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        
        double mean = sum / count;
        
        // Calculate variance and standard deviation
        double sumSquaredDiff = 0;
        for (int value : values) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        
        double variance = sumSquaredDiff / count;
        double stdDev = Math.sqrt(variance);
        
        // Store the statistics
        stats.put("count", (double) count);
        stats.put("sum", sum);
        stats.put("min", (double) min);
        stats.put("max", (double) max);
        stats.put("range", (double) (max - min));
        stats.put("mean", mean);
        stats.put("variance", variance);
        stats.put("stdDev", stdDev);
        
        logOperation("calculateStatistics", "count=" + count, "stats calculated");
        
        return stats;
    }
    
    /**
     * Perform a mock file operation (doesn't actually read/write)
     * @param filename Filename
     * @param operation Operation (read/write/delete)
     * @param content Content to write (for write operations)
     * @return Success message
     */
    public String mockFileOperation(String filename, String operation, String content) {
        if (filename == null || filename.isEmpty()) {
            return "Error: Invalid filename";
        }
        
        String result;
        switch (operation.toLowerCase()) {
            case "read":
                // Mock reading from a file
                result = "Successfully read " + filename + ": " + generateRandomString(20);
                break;
                
            case "write":
                // Mock writing to a file
                int contentLength = content != null ? content.length() : 0;
                result = "Successfully wrote " + contentLength + " characters to " + filename;
                break;
                
            case "delete":
                // Mock deleting a file
                result = "Successfully deleted " + filename;
                break;
                
            default:
                result = "Error: Unknown operation " + operation;
        }
        
        logOperation("mockFileOperation", operation + " on " + filename, result);
        return result;
    }
    
    /**
     * Generate a mock unique identifier
     * @param prefix Prefix for the identifier
     * @return Generated identifier
     */
    public String generateUniqueId(String prefix) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomPart = generateRandomString(8);
        
        String fullPrefix = prefix != null && !prefix.isEmpty() ? prefix : "ID";
        String result = fullPrefix + "-" + timestamp + "-" + randomPart;
        
        logOperation("generateUniqueId", "prefix=" + fullPrefix, result);
        return result;
    }
    
    /**
     * Generate a mock random data set
     * @param size Size of the data set
     * @param minValue Minimum value
     * @param maxValue Maximum value
     * @return List of random integers
     */
    public List<Integer> generateRandomDataSet(int size, int minValue, int maxValue) {
        if (size <= 0 || minValue >= maxValue) {
            return new ArrayList<>();
        }
        
        List<Integer> dataSet = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            dataSet.add(minValue + random.nextInt(maxValue - minValue + 1));
        }
        
        logOperation("generateRandomDataSet", "size=" + size + ", range=" + minValue + "-" + maxValue,
                    "generated " + dataSet.size() + " values");
        
        return dataSet;
    }
    
    /**
     * Apply a mock sorting algorithm
     * @param values List to sort
     * @param direction Sort direction ("asc" or "desc")
     * @return Sorted list
     */
    public List<Integer> mockSort(List<Integer> values, String direction) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Integer> result = new ArrayList<>(values);
        
        boolean ascending = !"desc".equalsIgnoreCase(direction);
        result.sort(ascending ? Integer::compare : (a, b) -> Integer.compare(b, a));
        
        logOperation("mockSort", "size=" + values.size() + ", direction=" + direction,
                    "sorted " + result.size() + " values");
        
        return result;
    }
    
    /**
     * Perform a mock search in a list
     * @param values List to search in
     * @param target Value to search for
     * @return Index of the value or -1 if not found
     */
    public int mockSearch(List<Integer> values, int target) {
        if (values == null || values.isEmpty()) {
            return -1;
        }
        
        int result = values.indexOf(target);
        
        logOperation("mockSearch", "size=" + values.size() + ", target=" + target,
                    result >= 0 ? "found at index " + result : "not found");
        
        return result;
    }
    
    /**
     * Perform a mock binary search (assumes sorted input)
     * @param values Sorted list to search in
     * @param target Value to search for
     * @return Index of the value or -1 if not found
     */
    public int mockBinarySearch(List<Integer> values, int target) {
        if (values == null || values.isEmpty()) {
            return -1;
        }
        
        int left = 0;
        int right = values.size() - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int midValue = values.get(mid);
            
            if (midValue == target) {
                logOperation("mockBinarySearch", "size=" + values.size() + ", target=" + target,
                            "found at index " + mid);
                return mid;
            }
            
            if (midValue < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        logOperation("mockBinarySearch", "size=" + values.size() + ", target=" + target, "not found");
        return -1;
    }
    
    /**
     * Perform a mock filtering operation
     * @param values List to filter
     * @param predicate Predicate type (e.g., "even", "odd", "positive", "negative")
     * @return Filtered list
     */
    public List<Integer> mockFilter(List<Integer> values, String predicate) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Integer> result = new ArrayList<>();
        
        for (int value : values) {
            boolean include = false;
            
            switch (predicate.toLowerCase()) {
                case "even":
                    include = value % 2 == 0;
                    break;
                case "odd":
                    include = value % 2 != 0;
                    break;
                case "positive":
                    include = value > 0;
                    break;
                case "negative":
                    include = value < 0;
                    break;
                case "prime":
                    include = isPrime(value);
                    break;
                default:
                    // Include all values if predicate is unknown
                    include = true;
            }
            
            if (include) {
                result.add(value);
            }
        }
        
        logOperation("mockFilter", "size=" + values.size() + ", predicate=" + predicate,
                    "filtered to " + result.size() + " values");
        
        return result;
    }
    
    /**
     * Check if a number is prime
     * @param n Number to check
     * @return True if prime
     */
    private boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        
        if (n <= 3) {
            return true;
        }
        
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Perform a mock transformation on each value
     * @param values List to transform
     * @param transformation Transformation type (e.g., "square", "double", "negate")
     * @return Transformed list
     */
    public List<Integer> mockTransform(List<Integer> values, String transformation) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Integer> result = new ArrayList<>(values.size());
        
        for (int value : values) {
            int transformed;
            
            switch (transformation.toLowerCase()) {
                case "square":
                    transformed = value * value;
                    break;
                case "double":
                    transformed = value * 2;
                    break;
                case "negate":
                    transformed = -value;
                    break;
                case "increment":
                    transformed = value + 1;
                    break;
                case "decrement":
                    transformed = value - 1;
                    break;
                default:
                    // Keep value unchanged if transformation is unknown
                    transformed = value;
            }
            
            result.add(transformed);
        }
        
        logOperation("mockTransform", "size=" + values.size() + ", transformation=" + transformation,
                    "transformed " + result.size() + " values");
        
        return result;
    }
    
    /**
     * Perform a mock reduction on a list
     * @param values List to reduce
     * @param reduction Reduction type (e.g., "sum", "product", "max", "min")
     * @return Result of the reduction
     */
    public int mockReduce(List<Integer> values, String reduction) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        
        int result;
        
        switch (reduction.toLowerCase()) {
            case "sum":
                result = values.stream().mapToInt(Integer::intValue).sum();
                break;
            case "product":
                result = values.stream().reduce(1, (a, b) -> a * b);
                break;
            case "max":
                result = values.stream().mapToInt(Integer::intValue).max().orElse(0);
                break;
            case "min":
                result = values.stream().mapToInt(Integer::intValue).min().orElse(0);
                break;
            case "average":
                result = (int) values.stream().mapToInt(Integer::intValue).average().orElse(0);
                break;
            default:
                // Return first element if reduction is unknown
                result = values.get(0);
        }
        
        logOperation("mockReduce", "size=" + values.size() + ", reduction=" + reduction,
                    "result=" + result);
        
        return result;
    }
    
    /**
     * Generate a mock random event
     * @return Event description
     */
    public String generateRandomEvent() {
        String[] eventTypes = {"Concert", "Match", "Conference", "Festival", "Exhibition", "Workshop"};
        String[] locations = {"New York", "London", "Tokyo", "Paris", "Sydney", "Berlin", "Cairo"};
        String[] adjectives = {"Annual", "International", "Regional", "Local", "Premier", "Exclusive"};
        
        String eventType = eventTypes[random.nextInt(eventTypes.length)];
        String location = locations[random.nextInt(locations.length)];
        String adjective = adjectives[random.nextInt(adjectives.length)];
        
        // Generate a random year between 2024 and 2030
        int year = 2024 + random.nextInt(7);
        
        // Generate a random month
        int month = 1 + random.nextInt(12);
        
        // Generate a random day
        int day = 1 + random.nextInt(28);
        
        String date = String.format("%04d-%02d-%02d", year, month, day);
        
        String event = String.format("%s %s %s in %s on %s", adjective, location, eventType, location, date);
        logOperation("generateRandomEvent", "", event);
        
        return event;
    }
    
    /**
     * Generate a mock random team name
     * @return Team name
     */
    public String generateRandomTeamName() {
        String[] prefixes = {"Royal", "United", "Elite", "Dynamic", "Mighty", "Alpha", "Phoenix"};
        String[] nouns = {"Eagles", "Tigers", "Lions", "Bears", "Hawks", "Wolves", "Dragons"};
        String[] suffixes = {"FC", "United", "City", "Athletic", "Stars", "Legends"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String noun = nouns[random.nextInt(nouns.length)];
        
        // Randomly include a suffix (50% chance)
        String suffix = random.nextBoolean() ? " " + suffixes[random.nextInt(suffixes.length)] : "";
        
        String teamName = prefix + " " + noun + suffix;
        logOperation("generateRandomTeamName", "", teamName);
        
        return teamName;
    }
    
    /**
     * Generate a mock random ticket price
     * @param type Ticket type
     * @return Ticket price
     */
    public double generateRandomTicketPrice(String type) {
        double basePrice;
        
        switch (type.toLowerCase()) {
            case "vip":
                basePrice = 150.0;
                break;
            case "premium":
                basePrice = 100.0;
                break;
            case "standard":
                basePrice = 50.0;
                break;
            case "economy":
                basePrice = 25.0;
                break;
            default:
                basePrice = 75.0;
        }
        
        // Add random variation (+/- 15%)
        double variation = 1.0 + (random.nextDouble() - 0.5) * 0.3;
        double price = basePrice * variation;
        
        // Round to nearest .99
        price = Math.floor(price) + 0.99;
        
        logOperation("generateRandomTicketPrice", "type=" + type, String.format("$%.2f", price));
        
        return price;
    }
    
    /**
     * Calculate a mock ticket revenue
     * @param vipQuantity Quantity of VIP tickets
     * @param standardQuantity Quantity of standard tickets
     * @param economyQuantity Quantity of economy tickets
     * @return Total revenue
     */
    public double calculateTicketRevenue(int vipQuantity, int standardQuantity, int economyQuantity) {
        double vipPrice = generateRandomTicketPrice("vip");
        double standardPrice = generateRandomTicketPrice("standard");
        double economyPrice = generateRandomTicketPrice("economy");
        
        double revenue = (vipQuantity * vipPrice) + (standardQuantity * standardPrice) + (economyQuantity * economyPrice);
        
        logOperation("calculateTicketRevenue", 
                    String.format("VIP=%d, Standard=%d, Economy=%d", vipQuantity, standardQuantity, economyQuantity),
                    String.format("$%.2f", revenue));
        
        return revenue;
    }
    
    /**
     * Generate a mock random event data map
     * @return Event data
     */
    public Map<String, Object> generateRandomEventData() {
        Map<String, Object> eventData = new HashMap<>();
        
        // Generate a random event ID
        String eventId = generateUniqueId("EVENT");
        
        // Generate event name and teams
        String teamA = generateRandomTeamName();
        String teamB = generateRandomTeamName();
        
        String[] eventTypes = {"Concert", "Match", "Conference", "Festival"};
        String eventType = eventTypes[random.nextInt(eventTypes.length)];
        
        String[] categories = {"Regular", "VIP", "Premium"};
        String category = categories[random.nextInt(categories.length)];
        
        // Generate a random date
        int year = 2024 + random.nextInt(2);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        String eventDate = String.format("%04d-%02d-%02d", year, month, day);
        
        // Generate random ticket quantities
        int vipTickets = 50 + random.nextInt(200);
        int standardTickets = 200 + random.nextInt(500);
        int economyTickets = 300 + random.nextInt(700);
        int totalTickets = vipTickets + standardTickets + economyTickets;
        
        // Calculate revenue
        double revenue = calculateTicketRevenue(vipTickets, standardTickets, economyTickets);
        
        // Populate the map
        eventData.put("event_id", eventId);
        eventData.put("event_name", teamA + " vs " + teamB);
        eventData.put("event_type", eventType);
        eventData.put("category", category);
        eventData.put("event_date", eventDate);
        eventData.put("team_a", teamA);
        eventData.put("team_b", teamB);
        eventData.put("vip_tickets", vipTickets);
        eventData.put("standard_tickets", standardTickets);
        eventData.put("economy_tickets", economyTickets);
        eventData.put("total_ticket_sold", totalTickets);
        eventData.put("total_revenue", revenue);
        
        logOperation("generateRandomEventData", "", "Generated event data: " + eventId);
        
        return eventData;
    }
    
    /**
     * Generate a mock batch of random events
     * @param count Number of events to generate
     * @return List of event data maps
     */
    public List<Map<String, Object>> generateRandomEvents(int count) {
        if (count <= 0) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> events = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            events.add(generateRandomEventData());
        }
        
        logOperation("generateRandomEvents", "count=" + count, "Generated " + events.size() + " events");
        
        return events;
    }
    
    /**
     * Generate mock random sales data
     * @param eventId Event ID
     * @param days Number of days
     * @return List of daily sales data
     */
    public List<Map<String, Object>> generateRandomSalesData(String eventId, int days) {
        if (days <= 0) {
            return new ArrayList<>();
        }
        
        List<Map<String, Object>> salesData = new ArrayList<>(days);
        
        // Generate a base event
        Map<String, Object> eventData = generateRandomEventData();
        String eventName = (String) eventData.get("event_name");
        String eventType = (String) eventData.get("event_type");
        String eventDate = (String) eventData.get("event_date");
        
        // Total tickets to be sold
        int totalTargetTickets = 1000 + random.nextInt(2000);
        int remainingTickets = totalTargetTickets;
        
        // Sales acceleration parameters
        double peakDayPercentage = 0.6 + (random.nextDouble() * 0.3); // 60-90% of tickets sold on peak day
        int peakDay = days / 3 + random.nextInt(days / 3); // Peak day in first or second third
        
        for (int day = 0; day < days; day++) {
            Map<String, Object> daySales = new HashMap<>();
            
            // Calculate date for this day
            int daysBeforeEvent = days - day;
            String saleDate = calculateDateBefore(eventDate, daysBeforeEvent);
            
            // Calculate tickets sold this day
            int ticketsSold;
            if (day == peakDay) {
                // Peak day
                ticketsSold = (int) (totalTargetTickets * peakDayPercentage);
            } else {
                // Calculate based on proximity to event and peak day
                double dayFactor = 1.0 - (Math.abs(day - peakDay) / (double) days);
                ticketsSold = (int) (remainingTickets * dayFactor * 0.2);
            }
            
            // Ensure we don't exceed remaining tickets
            ticketsSold = Math.min(ticketsSold, remainingTickets);
            remainingTickets -= ticketsSold;
            
            // Skip days with no sales (but ensure at least some days have sales)
            if (ticketsSold == 0 && salesData.size() > days / 3) {
                continue;
            }
            
            // Calculate revenue (average ticket price between $50-$150)
            double avgTicketPrice = 50.0 + random.nextDouble() * 100.0;
            double revenue = ticketsSold * avgTicketPrice;
            
            // Populate the day's sales data
            daySales.put("event_id", eventId != null ? eventId : "EVENT" + random.nextInt(1000));
            daySales.put("event_name", eventName);
            daySales.put("event_type", eventType);
            daySales.put("sale_date", saleDate);
            daySales.put("tickets_sold", ticketsSold);
            daySales.put("revenue", revenue);
            
            salesData.add(daySales);
        }
        
        logOperation("generateRandomSalesData", "eventId=" + eventId + ", days=" + days,
                    "Generated " + salesData.size() + " daily sales records");
        
        return salesData;
    }
    
    /**
     * Calculate a date before a given date
     * @param dateStr Date string (yyyy-MM-dd)
     * @param daysBefore Number of days before
     * @return Calculated date string
     */
    private String calculateDateBefore(String dateStr, int daysBefore) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(dateStr);
            
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -daysBefore);
            
            return format.format(calendar.getTime());
        } catch (Exception e) {
            return dateStr; // Return original on error
        }
    }
    
    /**
     * Main demonstration method for testing utility functions
     */
    public static void demonstrateUtility() {
        LargeUtility utility = new LargeUtility();
        utility.initialize(12345L, 10);
        
        // Test various functions
        System.out.println("Random String: " + utility.generateRandomString(10));
        
        System.out.println("Processed Text: " + utility.processTextInput("Hello World"));
        
        double score = utility.calculateScore(10, 20, 1.5);
        System.out.println("Score: " + score);
        
        System.out.println("\nReport Example:");
        System.out.println(utility.generateReport("Test Report", 3));
        
        // Test array processing
        int[] testArray = {1, 2, 3, 4, 5};
        int[] processedArray = utility.processArray(testArray);
        System.out.println("\nProcessed Array: " + utility.arrayToJson(processedArray));
        
        // Test formatting
        System.out.println("Formatted Date: " + utility.formatDate("2023-05-15", "MM/dd/yyyy"));
        
        // Test parsing
        Map<String, String> parsed = utility.parseComplexString("name=John;age=30;city=NewYork");
        System.out.println("\nParsed Complex String:");
        for (Map.Entry<String, String> entry : parsed.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // Test color generation
        Color color = utility.generateColorFromText("Blue Sky");
        System.out.println("\nGenerated Color: RGB(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")");
        System.out.println("Nearest Standard Color: " + utility.findNearestStandardColor(color));
        
        // Test event code generation
        System.out.println("\nEvent Code: " + utility.generateEventCode("Concert", "2023-12-25", 12345));
        
        // Test Fibonacci sequence
        List<Integer> fibSequence = utility.generateFibonacciSequence(10, true);
        System.out.println("\nFibonacci Sequence: " + fibSequence);
        
        // Test encryption/decryption
        String encrypted = utility.mockEncrypt("Secret Message", "key123");
        System.out.println("\nEncrypted: " + encrypted);
        String decrypted = utility.mockDecrypt(encrypted, "key123");
        System.out.println("Decrypted: " + decrypted);
        
        // Test file operations
        System.out.println("\nFile Operation: " + utility.mockFileOperation("test.txt", "write", "Hello World"));
        
        // Test unique ID generation
        System.out.println("\nUnique ID: " + utility.generateUniqueId("USER"));
        
        // Test random data set
        List<Integer> dataSet = utility.generateRandomDataSet(5, 1, 100);
        System.out.println("\nRandom Data Set: " + dataSet);
        
        // Test sorting
        List<Integer> sorted = utility.mockSort(dataSet, "asc");
        System.out.println("Sorted (Asc): " + sorted);
        
        // Test filtering
        List<Integer> filtered = utility.mockFilter(dataSet, "even");
        System.out.println("Filtered (Even): " + filtered);
        
        // Test transformation
        List<Integer> transformed = utility.mockTransform(dataSet, "square");
        System.out.println("Transformed (Square): " + transformed);
        
        // Test reduction
        int sum = utility.mockReduce(dataSet, "sum");
        System.out.println("Reduced (Sum): " + sum);
        
        // Test event generation
        System.out.println("\nRandom Event: " + utility.generateRandomEvent());
        System.out.println("Random Team: " + utility.generateRandomTeamName());
        
        // Test ticket pricing
        System.out.println("\nVIP Ticket Price: $" + utility.generateRandomTicketPrice("vip"));
        
        // Test revenue calculation
        double revenue = utility.calculateTicketRevenue(10, 50, 100);
        System.out.println("Ticket Revenue: $" + String.format("%.2f", revenue));
        
        // Test random event data
        Map<String, Object> eventData = utility.generateRandomEventData();
        System.out.println("\nRandom Event Data:");
        System.out.println(utility.mapToString(eventData));
        
        // Test execution history
        System.out.println("\nOperation Count: " + utility.getOperationCount());
    }
}