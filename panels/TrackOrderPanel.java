/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;

import javax.swing.*;
import java.sql.Date;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.Date; // Use java.sql.Date for SQL date handling
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import db.ConnectionProvider;

public class TrackOrderPanel extends JPanel {

    private JLabel statusLabel;
    private JLabel deliveryTimeLabel;
    private JPanel orderDetailsPanel;
    private JTextArea requestTextArea;
    private JButton sendRequestButton;
    private JButton refreshButton;
    private OrderDAO orderDAO;
    private final int customerId;

    public TrackOrderPanel() {
        this.customerId = SessionData.getInstance().getUserId();
        orderDAO = new OrderDAO();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Top Panel for Order Status
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        statusLabel = new JLabel("Order Status: ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusPanel.add(statusLabel, BorderLayout.NORTH);

        // Refresh Button
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> refreshOrderDetails());
        statusPanel.add(refreshButton, BorderLayout.SOUTH);

        add(statusPanel, BorderLayout.NORTH);

        // Center Panel for Order Details
        orderDetailsPanel = new JPanel();
        orderDetailsPanel.setLayout(new BoxLayout(orderDetailsPanel, BoxLayout.Y_AXIS));
        orderDetailsPanel.setBackground(new Color(245, 245, 245));
        orderDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        add(new JScrollPane(orderDetailsPanel), BorderLayout.CENTER);

        // Bottom Panel for Delivery Time and Request Section
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));

        JPanel deliveryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deliveryPanel.setBackground(new Color(245, 245, 245));
        deliveryTimeLabel = new JLabel("Estimated Delivery Time: 30mins");
        deliveryTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deliveryPanel.add(deliveryTimeLabel);
        bottomPanel.add(deliveryPanel, BorderLayout.NORTH);

        // Request Section
        JPanel requestPanel = new JPanel(new BorderLayout());
        requestPanel.setBackground(new Color(245, 245, 245));
        requestPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel requestLabel = new JLabel("Add a Request:");
        requestLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        requestPanel.add(requestLabel, BorderLayout.NORTH);

        requestTextArea = new JTextArea(3, 30);
        requestTextArea.setLineWrap(true);
        requestTextArea.setWrapStyleWord(true);
        requestTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        requestPanel.add(new JScrollPane(requestTextArea), BorderLayout.CENTER);

        sendRequestButton = new JButton("Send Request");
        sendRequestButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sendRequestButton.addActionListener(e -> sendRequest());
        requestPanel.add(sendRequestButton, BorderLayout.SOUTH);

        bottomPanel.add(requestPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);

        // Fetch the orderId dynamically from the database based on the current user's session or another method
        int orderId = orderDAO.getOrderIdForCustomer(SessionData.getInstance().getUserId());
        if (orderId != -1) {
            loadOrderDetails(orderId);
        } else {
            JOptionPane.showMessageDialog(this, "No order found for this customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshOrderDetails() {
        int orderId = orderDAO.getOrderIdForCustomer(customerId);
        if (orderId != -1) {
            loadOrderDetails(orderId);
        } else {
            JOptionPane.showMessageDialog(this, "No order found for this customer.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderDetails(int orderId) {
        Order order = orderDAO.getOrderById(orderId);
        if (order != null) {
            statusLabel.setText("Order Status: " + order.getPaymentStatus());

            orderDetailsPanel.removeAll();
            addOrderDetail("Order ID:", String.valueOf(order.getOrderId()));
            addOrderDetail("Order Date:", order.getOrderDate().toString());
            addOrderDetail("Total Price:", formatCurrency(order.getTotalPrice()));
            addOrderDetail("Items:", String.join(", ", orderDAO.getOrderItems(orderId)));

            orderDetailsPanel.revalidate();
            orderDetailsPanel.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Order not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrderDetail(String labelText, String valueText) {
        JPanel detailRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailRow.setBackground(new Color(245, 245, 245));
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setPreferredSize(new Dimension(120, label.getPreferredSize().height));
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        detailRow.add(label);
        detailRow.add(value);
        orderDetailsPanel.add(detailRow);
    }

    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));
        return currencyFormat.format(amount);
    }

    private void sendRequest() {
        String requestText = requestTextArea.getText().trim();
        if (requestText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a request before sending.", "Empty Request", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Save the request to the database
        boolean success = orderDAO.saveRequest(customerId, requestText);
        if (success) {
            JOptionPane.showMessageDialog(this, "Your request has been sent successfully.", "Request Sent", JOptionPane.INFORMATION_MESSAGE);
            requestTextArea.setText(""); // Clear the request text area
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send request. Please try again.", "Request Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class OrderDAO {

    public int getOrderIdForCustomer(int customerId) {
        String query = "SELECT order_id FROM orders WHERE customer_id = ? ORDER BY order_date DESC LIMIT 1";
        try (Connection conn = ConnectionProvider.getCon(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("order_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if no order is found
    }

    public Order getOrderById(int orderId) {
        String query = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection conn = ConnectionProvider.getCon(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setTotalPrice(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getOrderItems(int orderId) {
        List<String> items = new ArrayList<>();
        String query = "SELECT mi.item_name " +
                "FROM order_details od " +
                "JOIN menu_items mi ON od.menu_item_id = mi.item_id " +
                "WHERE od.order_id = ?";
        try (Connection conn = ConnectionProvider.getCon(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                items.add(rs.getString("item_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean saveRequest(int customerId, String request) {
        String query = "INSERT INTO customer_requests (customer_id, description, created_at) VALUES (?, ?, NOW())";
        try (Connection conn = ConnectionProvider.getCon(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, request);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

