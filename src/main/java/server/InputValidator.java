package server;

import java.util.regex.Pattern;

/**
 * Class responsible for validating user inputs before processing them in
 * services.
 * Provides methods to validate different types of inputs like emails,
 * passwords,
 * and strings.
 */
public class InputValidator {

  // Email validation pattern
  private static final Pattern EMAIL_PATTERN = Pattern.compile(
      "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

  // Password pattern requiring at least 8 chars, with at least one digit, one
  // lower, one upper case
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$");

  /**
   * Validates if a string is not null or empty.
   * 
   * @param input the string to validate
   * @return true if input is valid, false otherwise
   */
  public static boolean isValidString(String input) {
    return input != null && !input.trim().isEmpty();
  }

  /**
   * Validates if a string is a valid email address.
   * 
   * @param email the email to validate
   * @return true if email is valid, false otherwise
   */
  public static boolean isValidEmail(String email) {
    if (!isValidString(email)) {
      return false;
    }
    return EMAIL_PATTERN.matcher(email).matches();
  }

  /**
   * Validates password strength.
   * 
   * @param password the password to validate
   * @return true if password meets strength requirements, false otherwise
   */
  public static boolean isStrongPassword(String password) {
    if (!isValidString(password)) {
      return false;
    }
    return PASSWORD_PATTERN.matcher(password).matches();
  }

  /**
   * Validates if input is a positive integer.
   * 
   * @param value the integer value to validate
   * @return true if value is positive, false otherwise
   */
  public static boolean isPositiveInteger(int value) {
    return value > 0;
  }

  /**
   * Validates a price category selection.
   * 
   * @param priceCategory the selected price category
   * @return true if the category is valid, false otherwise
   */
  public static boolean isValidPriceCategory(String priceCategory) {
    if (!isValidString(priceCategory)) {
      return false;
    }

    return priceCategory.contains("VIP") ||
        priceCategory.contains("Premium") ||
        priceCategory.contains("Standard");
  }

  /**
   * Validates ticket type.
   * 
   * @param ticketType the ticket type to validate
   * @return true if the ticket type is valid, false otherwise
   */
  public static boolean isValidTicketType(String ticketType) {
    if (!isValidString(ticketType)) {
      return false;
    }

    // Add your specific ticket type validation logic here
    return true;
  }
}
