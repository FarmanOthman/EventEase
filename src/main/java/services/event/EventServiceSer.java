package services.event;

import java.util.Arrays;
import java.util.List;

public class EventServiceSer {
  public List<String> getEventTypes(String category) {
    // TODO: Replace with database query
    return Arrays.asList("Regular", "VIP", "Special");
  }

  public List<String> getEventCategories() {
    // TODO: Replace with database query
    return Arrays.asList("Sports", "Music", "Theater", "Conference");
  }
}