/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelsWaiter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

import javax.swing.JPanel;

/**
 *
 * @author Admin
 */
public class MenuLookupPanel extends JPanel{
    
    private JTable menuTable;
    private JComboBox<String> categoryFilter;
    private DefaultTableModel tableModel;

    public MenuLookupPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 800));
        // Title
        JLabel title = new JLabel("Menu Lookup", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        // Table Model Setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Category", "Name", "Description", "Price", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable cell editing
            }
        };
        menuTable = new JTable(tableModel);
        menuTable.setRowHeight(24);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        add(scrollPane, BorderLayout.CENTER);

        // Filter Panel Setup
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter by Category:"));

        // Category Filter ComboBox
        categoryFilter = new JComboBox<>(new String[]{"All", "Beverages", "Kids Meals", "Main Course - Non-Vegetarian", "Main Course - Vegetarian", "Desserts"});
        categoryFilter.addActionListener(e -> loadMenuItems((String) categoryFilter.getSelectedItem()));
        filterPanel.add(categoryFilter);

        add(filterPanel, BorderLayout.SOUTH);

        // Initial Load of Menu Items
        loadMenuItems("All");
    }

    // Load menu items based on selected category
    private void loadMenuItems(String selectedCategory) {
        tableModel.setRowCount(0);  // Clear existing rows

        String url = "jdbc:mysql://localhost:3306/registration";
        String user = "root";
        String password = "@Chante2004";

        // SQL query to fetch menu items based on availability and selected category
        String sql = "SELECT * FROM menu_items WHERE availability = 1"; // Only available items
        if (!"All".equalsIgnoreCase(selectedCategory)) {
            sql += " AND category = ?";
        }

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (!"All".equalsIgnoreCase(selectedCategory)) {
                stmt.setString(1, selectedCategory);  // Set category filter if not "All"
            }

            ResultSet rs = stmt.executeQuery();  // Execute query

            // Process the result set and add rows to the table
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("item_id"));
                row.add(rs.getString("category"));
                row.add(rs.getString("item_name"));
                row.add(rs.getString("description"));
                row.add(String.format("R%.2f", rs.getDouble("price")));  // Format price
                row.add(rs.getBoolean("availability") ? "Yes" : "No");  // Availability status

                tableModel.addRow(row);  // Add row to the table model
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Display error message if the database query fails
            JOptionPane.showMessageDialog(this, "Failed to load menu items:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
