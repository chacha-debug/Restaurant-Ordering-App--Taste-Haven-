/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class ProfileHistoryPanel extends JPanel{
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Chante2004";
    private final int customerId; 

    public ProfileHistoryPanel() {
        this.customerId = SessionData.getInstance().getUserId();
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Title Label
        JLabel titleLabel = new JLabel("Profile and Order History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(50, 50, 50));
        add(titleLabel, BorderLayout.NORTH);

        // Table Columns and Empty Data
        String[] columns = {"Order ID", "Date", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        historyTable.setRowHeight(25);
        historyTable.setGridColor(new Color(220, 220, 220));

        // Center-align table content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Style Table Header
        JTableHeader tableHeader = historyTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setBackground(new Color(200, 200, 200));
        tableHeader.setForeground(Color.BLACK);

        // Add Table to ScrollPane
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(scrollPane, BorderLayout.CENTER);

        // Load Data from Database
        loadOrderHistory();

        // Add Row Selection Listener
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = historyTable.getSelectedRow();
                if (selectedRow != -1) {
                    String orderId = historyTable.getValueAt(selectedRow, 0).toString();
                    showOrderDetails(orderId);
                }
            }
        });
    }

    private void loadOrderHistory() {
        // Fetch order history from the database
        String query = "SELECT order_id, order_date, total_amount FROM orders WHERE customer_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, customerId); // Filter orders by customer_id
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0); // Clear existing rows
            while (resultSet.next()) {
                String orderId = resultSet.getString("order_id");
                String orderDate = resultSet.getString("order_date");
                String totalAmount = "R" + resultSet.getBigDecimal("total_amount").toString();

                tableModel.addRow(new Object[]{orderId, orderDate, totalAmount});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order history: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOrderDetails(String orderId) {
        // Create a dialog to show order details
        JDialog orderDetailsDialog = new JDialog((Frame) null, "Order Details", true);
        orderDetailsDialog.setSize(800, 500);
        orderDetailsDialog.setLocationRelativeTo(this);
        orderDetailsDialog.setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Items Bought for Order ID: " + orderId);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        orderDetailsDialog.add(titleLabel, BorderLayout.NORTH);

        // Order Items Table
        String[] columns = {"Item Name", "Description", "Quantity", "Price", "Total"};
        Object[][] data = fetchOrderDetails(orderId);

        JTable orderDetailsTable = new JTable(data, columns);
        orderDetailsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        orderDetailsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(orderDetailsTable);
        orderDetailsDialog.add(scrollPane, BorderLayout.CENTER);

        // Close Button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> orderDetailsDialog.dispose());
        orderDetailsDialog.add(closeButton, BorderLayout.SOUTH);

        orderDetailsDialog.setVisible(true);
    }

    private Object[][] fetchOrderDetails(String orderId) {
        // Fetch items bought for the selected order by joining orders, order_details, and menu_items tables
        String query = "SELECT m.item_name, m.description, od.quantity, m.price, " +
                       "(od.quantity * m.price) AS total_price " +
                       "FROM order_details od " +
                       "JOIN menu_items m ON od.menu_item_id = m.item_id " +
                       "WHERE od.order_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            // Process result set and build data array
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            while (resultSet.next()) {
                rows.add(new Object[]{
                    resultSet.getString("item_name"),
                    resultSet.getString("description"),
                    resultSet.getInt("quantity"),
                    "R" + resultSet.getBigDecimal("price").toString(),
                    "R" + resultSet.getBigDecimal("total_price").toString()
                });
            }

            return rows.toArray(new Object[0][]);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching order details: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
            return new Object[0][0];
        }
    }
    
}
