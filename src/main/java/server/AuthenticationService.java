package server; // You need to add package 

class AuthenticationService {
    private static final String VALID_EMAIL = "a@a.com"; // This data should come from data base
    private static final String VALID_PASSWORD = "password123"; // This data should come from data base

    public static boolean authenticate(String email, String password) {
        return VALID_EMAIL.equals(email) && VALID_PASSWORD.equals(password); // return a boolean value
    }
}
