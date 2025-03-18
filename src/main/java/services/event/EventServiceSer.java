package services.event;

import java.util.Arrays;
import java.util.List;

public class EventServiceSer {
  public List<String> getEventTypes(String category) {
    // TODO: Replace with database query
    return Arrays.asList("Math", "Event");
  }

  public List<String> getEventCategories() {
    // TODO: Replace with database query
    return Arrays.asList("Regular", "VIP");
  }
}