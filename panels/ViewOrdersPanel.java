package panels;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import db.ConnectionProvider;
/**
 *
 * @author Admin
 */
public class ViewOrdersPanel extends JPanel{
    
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public ViewOrdersPanel() {
        int customerId = SessionData.getInstance().getUserId();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel header = new JLabel("Your Past Orders", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        header.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Order ID", "Date", "Time", "Type", "Item", "Qty", "Price", "Total"}, 0);
        ordersTable = new JTable(tableModel);
        ordersTable.setFillsViewportHeight(true);
        add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        loadOrders(customerId);
    }

    private void loadOrders(int customerId) {
        String orderQuery = "SELECT o.order_id, o.order_date, o.scheduled_time AS order_time, o.order_type, " +
                "m.item_name, oi.quantity, m.price, (oi.quantity * m.price) AS total " +
                "FROM orders o " +
                "JOIN order_details oi ON o.order_id = oi.order_id " +
                "JOIN menu_items m ON oi.menu_item_id = m.item_id " +
                "WHERE o.customer_id = ? " +
                "ORDER BY o.order_date DESC, o.scheduled_time DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration", "root", "@Chante2004");
             PreparedStatement stmt = conn.prepareStatement(orderQuery)) {

            // Set the customer ID in the query
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            // Clear existing rows in the table
            tableModel.setRowCount(0);

            // Add rows to the table model
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getString("order_time"),
                        rs.getString("order_type"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        String.format("R%.2f", rs.getDouble("price")),
                        String.format("R%.2f", rs.getDouble("total"))
                });
            }


        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load orders.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
