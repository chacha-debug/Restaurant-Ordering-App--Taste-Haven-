/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelsWaiter;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
/**
 *
 * @author Admin
 */
public class ActiveOrdersPanel extends JPanel{
    
   private JTable ordersTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> statusFilter;
    private JButton refreshButton;
    private JButton setSelectedToReadyButton;

    public ActiveOrdersPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 800));

        // Title
        JLabel title = new JLabel("Active Orders", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(new String[]{
                "Order ID", "Table No", "Items", "Total Amount", "Payment Status", "Status", "Update Status"
        }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // Disable editing for all cells
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(40);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);

        statusFilter = new JComboBox<>(new String[]{"All", "Pending", "Preparing", "Ready", "Served"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setPreferredSize(new Dimension(150, 30));
        bottomPanel.add(new JLabel("Filter by Status: "));
        bottomPanel.add(statusFilter);

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.addActionListener(e -> loadActiveOrders());
        statusFilter.addActionListener(e -> loadActiveOrders());

        setSelectedToReadyButton = new JButton("Set Selected to Ready");
        setSelectedToReadyButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setSelectedToReadyButton.addActionListener(e -> setSelectedOrderToReady());

        bottomPanel.add(refreshButton);
        bottomPanel.add(setSelectedToReadyButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load orders initially
        loadActiveOrders();
    }

    private void loadActiveOrders() {
        // Clear the table before reloading
        tableModel.setRowCount(0);

        // Fetch selected status
        String selectedStatus = statusFilter.getSelectedItem().toString();

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/registration";
        String user = "root";
        String password = "@Chante2004";

        // SQL query to fetch orders with items
        String sql = "SELECT o.order_id, o.table_number, o.order_type, o.total_amount, " +
                "o.payment_status, o.status, o.timestamp, " +
                "COALESCE(GROUP_CONCAT(CONCAT(od.quantity, 'x ', mi.item_name) SEPARATOR ', '), 'No Items') AS items " +
                "FROM orders o " +
                "LEFT JOIN order_details od ON o.order_id = od.order_id " +
                "LEFT JOIN menu_items mi ON od.menu_item_id = mi.item_id ";

        if (!"All".equals(selectedStatus)) {
            sql += "WHERE o.status = ? ";
        }

        sql += "GROUP BY o.order_id, o.table_number, o.order_type, o.total_amount, " +
                "o.payment_status, o.status, o.timestamp " +
                "ORDER BY o.timestamp DESC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Add status parameter if filtering by status
            if (!"All".equals(selectedStatus)) {
                stmt.setString(1, selectedStatus);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int tableNum = rs.getInt("table_number");
                String items = rs.getString("items");
                String totalAmount = rs.getString("total_amount");
                String paymentStatus = rs.getString("payment_status");
                String status = rs.getString("status");

                Vector<Object> row = new Vector<>();
                row.add(orderId);
                row.add(tableNum);
                row.add(items != null ? items : "No Items");
                row.add(totalAmount != null ? totalAmount : "0.00");
                row.add(paymentStatus);
                row.add(status);
                row.add("N/A"); // Placeholder for "Update Status" column

                tableModel.addRow(row);
            }

            // Check if no orders were found
            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No active orders found.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setSelectedOrderToReady() {
        // Get the selected row
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the Order ID of the selected row
        int orderId = (int) tableModel.getValueAt(selectedRow, 0);

        // Update the status to "Ready"
        String url = "jdbc:mysql://localhost:3306/registration";
        String user = "root";
        String password = "@Chante2004";
        String sql = "UPDATE orders SET status = 'Ready' WHERE order_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Order ID " + orderId + " status updated to 'Ready'.");
            loadActiveOrders(); // Refresh the table after updating
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
    
