/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;
import db.ConnectionProvider;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Admin
 */
public class PasswordUpdater {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Chante2004";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get all users
            PreparedStatement selectStmt = conn.prepareStatement("SELECT user_id, password_hash FROM users");
            ResultSet rs = selectStmt.executeQuery();
            
            // Prepare update statement
            PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password_hash = ? WHERE user_id = ?");
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String plainPassword = rs.getString("password_hash");
                
                // Generate BCrypt hash (will handle null/empty passwords safely)
                String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
                
                // Update the user
                updateStmt.setString(1, hashedPassword);
                updateStmt.setInt(2, userId);
                updateStmt.executeUpdate();
                
                System.out.println("Updated password for user ID: " + userId);
            }
            
            System.out.println("Password update complete!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
