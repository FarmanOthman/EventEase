package server; // You need to add package 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.Database;

public class AuthenticationService {

    public static boolean authenticate(String username, String password) {
        try (Connection connection = Database.getConnection()) {
            String query = "SELECT COUNT(*) FROM ADMIN WHERE username = ? AND password = ?";      
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
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

}