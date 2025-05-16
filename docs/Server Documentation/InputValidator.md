# InputValidator

## Overview
The `InputValidator` class is responsible for validating user inputs before processing them in services. It provides methods to validate different types of inputs like emails, passwords, strings, and more specific application-related values.

## Class Structure

### Main Class: InputValidator
This is a utility class with static methods for validating various types of input data.

#### Constants
- `EMAIL_PATTERN`: A compiled regex Pattern for validating email addresses
- `PASSWORD_PATTERN`: A compiled regex Pattern for validating password strength (requires at least 8 characters, with at least one digit, one lowercase, and one uppercase letter)

#### Methods

##### isValidString
```java
public static boolean isValidString(String input)
```
Validates if a string is not null or empty.

- **Parameters**:
  - `input`: The string to validate
- **Returns**: `true` if input is not null and not empty, `false` otherwise
- **Implementation Details**:
  - Checks for null
  - Uses trim() to remove whitespace and checks if the result is empty

##### isValidEmail
```java
public static boolean isValidEmail(String email)
```
Validates if a string is a valid email address.

- **Parameters**:
  - `email`: The email to validate
- **Returns**: `true` if email is valid, `false` otherwise
- **Implementation Details**:
  - First checks if the string is valid using isValidString()
  - Uses EMAIL_PATTERN regex to match against common email patterns

##### isStrongPassword
```java
public static boolean isStrongPassword(String password)
```
Validates password strength against predefined criteria.

- **Parameters**:
  - `password`: The password to validate
- **Returns**: `true` if password meets strength requirements, `false` otherwise
- **Implementation Details**:
  - First checks if the string is valid using isValidString()
  - Uses PASSWORD_PATTERN regex to ensure the password contains at least 8 characters, with at least one digit, one lowercase, and one uppercase letter

##### isPositiveInteger
```java
public static boolean isPositiveInteger(int value)
```
Validates if a value is a positive integer.

- **Parameters**:
  - `value`: The integer value to validate
- **Returns**: `true` if value is positive, `false` otherwise
- **Implementation Details**:
  - Simply checks if the value is greater than 0

##### isValidPriceCategory
```java
public static boolean isValidPriceCategory(String priceCategory)
```
Validates a price category selection.

- **Parameters**:
  - `priceCategory`: The selected price category
- **Returns**: `true` if the category is valid, `false` otherwise
- **Implementation Details**:
  - First checks if the string is valid using isValidString()
  - Checks if the string contains "VIP", "Premium", or "Standard"

##### isValidTicketType
```java
public static boolean isValidTicketType(String ticketType)
```
Validates ticket type.

- **Parameters**:
  - `ticketType`: The ticket type to validate
- **Returns**: `true` if the ticket type is valid, `false` otherwise
- **Implementation Details**:
  - First checks if the string is valid using isValidString()
  - Currently returns true for any non-empty string (placeholder for more specific validation)

## Usage Example
```java
// Validate a user's email
String email = "user@example.com";
if (InputValidator.isValidEmail(email)) {
    System.out.println("Email is valid");
} else {
    System.out.println("Invalid email format");
}

// Validate a password
String password = "Password123";
if (InputValidator.isStrongPassword(password)) {
    System.out.println("Password meets strength requirements");
} else {
    System.out.println("Password is too weak");
}

// Validate a price category
String priceCategory = "VIP Section A";
if (InputValidator.isValidPriceCategory(priceCategory)) {
    System.out.println("Valid price category");
} else {
    System.out.println("Invalid price category");
}
```

## Dependencies
- `java.util.regex.Pattern`: Used for regex pattern matching
