package server; // You need to add package 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.Database;


class AuthenticationService {

    public static boolean authenticate(String email, String password) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT COUNT(*) FROM ADMIN WHERE email = ? AND password = ?";      
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, email);
                statement.setString(2, password);
                
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        String testEmail = "admin@example.com";
        String testPassword = "admin123";
        boolean isAuthenticated = authenticate(testEmail, testPassword);
        if (isAuthenticated) {
            System.out.println("Admin found with email: " + testEmail + " and password: " + testPassword);
        } else {
            System.out.println("Admin not found with email: " + testEmail + " and password: " + testPassword);
        }
    }
}