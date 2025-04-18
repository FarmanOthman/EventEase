package ui;

import java.util.HashMap;
import java.util.Map;

public class Router {
  private static MainFrame mainFrame;
  private static Map<String, Object> sharedData = new HashMap<>();

  public static void setMainFrame(MainFrame frame) {
    mainFrame = frame;
  }

  public static void showPage(String pageName) {
    if (mainFrame != null) {
      mainFrame.showPage(pageName);
    }
  }

  // Method to store data for passing between pages
  public static void putData(String key, Object value) {
    sharedData.put(key, value);
  }

  // Method to retrieve data passed between pages
  public static Object getData(String key) {
    return sharedData.get(key);
  }

  // Method to clear specific data
  public static void clearData(String key) {
    sharedData.remove(key);
  }

  // Method to clear all shared data
  public static void clearAllData() {
    sharedData.clear();
  }
}