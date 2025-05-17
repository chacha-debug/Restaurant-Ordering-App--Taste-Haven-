/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class PaymentSlipDAO {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Chante2004";

    public void savePaymentSlip(int orderId, String paymentMethod, String receiptText) {
        String sql = "INSERT INTO payment_slips (order_id, payment_method, receipt_text, payment_date) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            
            preparedStatement.setInt(1, orderId);
            preparedStatement.setString(2, paymentMethod);
            preparedStatement.setString(3, receiptText);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();

            System.out.println("Payment slip saved to the database for Order ID: " + orderId);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database errors appropriately (e.g., logging, displaying an error message)
        }
    }
    
}
