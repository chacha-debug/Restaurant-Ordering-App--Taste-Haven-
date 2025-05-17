/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelChef;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Admin
 */
public class ViewPendingOrdersPanel extends JPanel{
    
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public ViewPendingOrdersPanel() {
        setPreferredSize(new Dimension(699, 769));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Pending Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80));
        add(titleLabel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                "Orders List",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(100, 100, 100)
        ));
        tablePanel.setPreferredSize(new Dimension(659, 619)); // panel - padding

        // Table Setup
        String[] columns = {
                "Order ID", "Customer ID", "Order Date",
                "Total", "Table No.", "Note", "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        ordersTable = new JTable(tableModel);
        ordersTable.setFillsViewportHeight(true);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ordersTable.setRowHeight(28);
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTable.setPreferredScrollableViewportSize(new Dimension(639, 599));

        // Custom cell renderer for row colors
        ordersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String status = (String) table.getValueAt(row, table.getColumnCount() - 1); // Last column is status
                if ("Pending".equals(status)) {
                    c.setBackground(new Color(255, 243, 205)); // Light Yellow
                } else if ("Preparing".equals(status)) {
                    c.setBackground(new Color(209, 231, 221)); // Light Green
                } else if ("Ready".equals(status)) {
                    c.setBackground(new Color(198, 246, 213)); // Light Orange
                } else {
                    c.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    c.setBackground(c.getBackground().darker());
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setPreferredSize(new Dimension(639, 599));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBackground(Color.WHITE);

        JButton prepareButton = new JButton("Mark as Preparing");
        prepareButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        prepareButton.setBackground(new Color(52, 152, 219)); // Light Blue
        prepareButton.setForeground(Color.WHITE);
        prepareButton.addActionListener(e -> updateOrderStatus("Preparing"));
        buttonsPanel.add(prepareButton);

        JButton readyButton = new JButton("Mark as Ready");
        readyButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        readyButton.setBackground(new Color(46, 204, 113)); // Light Green
        readyButton.setForeground(Color.WHITE);
        readyButton.addActionListener(e -> updateOrderStatus("Ready"));
        buttonsPanel.add(readyButton);

        add(buttonsPanel, BorderLayout.SOUTH);

        // Load data from database
        loadPendingOrders();
    }

    private void loadPendingOrders() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration", "root", "@Chante2004");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT order_id, customer_id, order_date, total_amount, table_number, note, status FROM orders")) {

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                int customerId = rs.getInt("customer_id");
                Timestamp orderDate = rs.getTimestamp("order_date");
                double totalAmount = rs.getDouble("total_amount");
                int tableNumber = rs.getInt("table_number");
                String note = rs.getString("note");
                String status = rs.getString("status");

                tableModel.addRow(new Object[]{
                        orderId, customerId, orderDate,
                        totalAmount, tableNumber, note, status
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading pending orders:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrderStatus(String newStatus) {
        int selectedRow = ordersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0); // Order ID is in the first column

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/registration", "root", "@Chante2004");
             PreparedStatement pstmt = conn.prepareStatement("UPDATE orders SET status = ? WHERE order_id = ?")) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();

            // Update the table model
            tableModel.setValueAt(newStatus, selectedRow, tableModel.getColumnCount() - 1);

            JOptionPane.showMessageDialog(this, "Order status updated to " + newStatus + ".", "Update Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating order status:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
