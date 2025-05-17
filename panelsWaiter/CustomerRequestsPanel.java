/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelsWaiter;

import com.mysql.cj.Session;
import db.ConnectionProvider;
import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import panels.SessionData;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class CustomerRequestsPanel extends JPanel{
    
    private JTable ordersRequestsTable;
    private JScrollPane scrollPane;
    private Connection conn;

    public CustomerRequestsPanel() {
        this.conn = ConnectionProvider.getCon();  // Database connection
        setLayout(new BorderLayout());
        initializeTable();  // Initialize table and UI components
        loadActiveOrdersAndRequests();  // Load active orders and requests from the database
    }

    // Initialize the table for displaying order details and customer requests
    private void initializeTable() {
        ordersRequestsTable = new JTable();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Order Number");
        model.addColumn("Order Status");
        model.addColumn("Customer Request");
        model.addColumn("Comments");
        setPreferredSize(new Dimension(800, 800));
        ordersRequestsTable.setModel(model);
        ordersRequestsTable.setRowHeight(40);  // Set row height for better visibility

        scrollPane = new JScrollPane(ordersRequestsTable);
        add(scrollPane, BorderLayout.CENTER);  // Add scroll pane to panel
    }

    // Load active orders and customer requests from the database
    private void loadActiveOrdersAndRequests() {
        try {
            // SQL query to get active orders, customer requests, and comments
            String sql = "SELECT o.order_id, o.status AS order_status, r.customer_request, r.comments " +
                         "FROM orders o " +
                         "LEFT JOIN customer_requests r ON o.customer_id = r.customer_id " +
                         "WHERE o.status IN ('Pending', 'Preparing', 'Ready') " +  // Active orders
                         "ORDER BY o.order_id";

            // Execute the query
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) ordersRequestsTable.getModel();
            model.setRowCount(0);  // Clear the table before adding new data

            // Process the result set and add data to the table
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String orderStatus = rs.getString("order_status");
                String customerRequest = rs.getString("customer_request");
                String comments = rs.getString("comments");

                // Handle cases where no request or comments are made (NULL values)
                if (customerRequest == null) {
                    customerRequest = "No request";  // Default value when no request exists
                }
                if (comments == null) {
                    comments = "No comments";  // Default value when no comments exist
                }

                // Add row to the table model
                model.addRow(new Object[] {
                        orderId,
                        orderStatus,
                        customerRequest,
                        comments
                });
            }

            rs.close();
            ps.close();  // Close resources to prevent memory leaks
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Show error message if something goes wrong while fetching data
            JOptionPane.showMessageDialog(this, "Error loading active orders and requests:\n" + ex.getMessage());
        }
    }
    
}
