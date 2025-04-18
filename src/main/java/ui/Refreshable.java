package ui;

/**
 * Interface for UI components that need to refresh their content
 * when being displayed or when data changes.
 */
public interface Refreshable {
  /**
   * Refreshes the component data and UI.
   * This method should be called when the component is shown
   * or when underlying data has changed.
   */
  void refresh();
}