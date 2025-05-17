/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelManager;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;
/**
 *
 * @author Admin
 */
public class ManageMenu extends JPanel{
    
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryComboBox;
    private JTextField txtItem, txtPrice;
    private JButton btnAdd, btnDelete, btnEdit;
    
    // === JDBC CONFIGURATION ===
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "@Chante2004";

    public ManageMenu() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800,600));

        // Title
        JLabel title = new JLabel("Edit Menu", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(
            new String[]{ "ID", "Category", "Item", "Price" }, 
            0
        ) {
            // make ID column non‐editable
            @Override public boolean isCellEditable(int row, int col) {
                return col != 0;
            }
        };
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(25);
        itemTable.getColumnModel().getColumn(0).setMinWidth(0);
        itemTable.getColumnModel().getColumn(0).setMaxWidth(0);
        add(new JScrollPane(itemTable), BorderLayout.CENTER);

        // Form inputs
        JPanel form = new JPanel(new GridLayout(3,2,10,10));
        form.setBackground(Color.WHITE);
        categoryComboBox = new JComboBox<>(new String[]{
            "Hot Beverages","Drinks","Main Meals","Starters","Dessert"
        });
        txtItem  = new JTextField();
        txtPrice = new JTextField();
        form.add(new JLabel("Category:"));
        form.add(categoryComboBox);
        form.add(new JLabel("Item:"));
        form.add(txtItem);
        form.add(new JLabel("Price:"));
        form.add(txtPrice);
        add(form, BorderLayout.WEST);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        buttons.setBackground(Color.WHITE);
        btnAdd    = createButton("Add", Color.decode("#4682B4"));
        btnDelete = createButton("Delete", Color.decode("#DC143C"));
        btnEdit   = createButton("Edit", Color.decode("#FFA500"));
        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnEdit);
        add(buttons, BorderLayout.SOUTH);

        // Event handlers
        btnAdd.addActionListener(e -> insertItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnEdit.addActionListener(e -> updateItem());

        // load from database
        loadItemsFromDb();
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadItemsFromDb() {
        tableModel.setRowCount(0);
        String sql = "SELECT item_id, category, item_name, price FROM menu_items";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("item_id"),
                    rs.getString("category"),
                    rs.getString("item_name"),
                    rs.getBigDecimal("price")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading items: "+ex.getMessage(),
                                          "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertItem() {
        String category = (String)categoryComboBox.getSelectedItem();
        String name     = txtItem.getText().trim();
        String priceStr = txtPrice.getText().trim();
        if (name.isEmpty()||priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }
        String sql = "INSERT INTO menu_items(category,item_name,price) VALUES(?,?,?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setString(2, name);
            ps.setBigDecimal(3, new java.math.BigDecimal(priceStr));
            ps.executeUpdate();
            loadItemsFromDb();
            txtItem.setText("");
            txtPrice.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Insert failed: "+ex.getMessage(),
                                          "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateItem() {
        int row = itemTable.getSelectedRow();
        if (row<0) {
            JOptionPane.showMessageDialog(this, "Select a row to edit.");
            return;
        }
        int id       = (int)tableModel.getValueAt(row, 0);
        String cat   = (String)categoryComboBox.getSelectedItem();
        String name  = txtItem.getText().trim();
        String price = txtPrice.getText().trim();
        if (name.isEmpty()||price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }
        String sql = "UPDATE menu_items SET category=?, item_name=?, price=? WHERE item_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cat);
            ps.setString(2, name);
            ps.setBigDecimal(3, new java.math.BigDecimal(price));
            ps.setInt(4, id);
            ps.executeUpdate();
            loadItemsFromDb();
            txtItem.setText("");
            txtPrice.setText("");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Update failed: "+ex.getMessage(),
                                          "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteItem() {
        int row = itemTable.getSelectedRow();
        if (row<0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        int id = (int)tableModel.getValueAt(row, 0);
        String sql = "DELETE FROM menu_items WHERE item_id=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadItemsFromDb();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Delete failed: "+ex.getMessage(),
                                          "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
