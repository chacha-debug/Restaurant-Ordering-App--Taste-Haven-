/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Properties;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author Admin
 */
public class PreOrderPanel extends JPanel{
    
    private JPanel itemsPanel;
    private JLabel totalLabel;
    private JTextField searchField;

    private int customerId;
    // --- Data ---
    private Map<Integer, OrderItem> orderMap = new LinkedHashMap<>();
    private List<MenuItem> menuItems = new ArrayList<>();

    // --- Database Connection Details (Consider moving to a config file) ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Chante2004";

    // --- Colors ---
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_PANEL_BACKGROUND = new Color(245, 248, 250); // Light blue-gray
    private static final Color COLOR_PRIMARY_ACTION = new Color(25, 118, 210); // Material Blue
    private static final Color COLOR_SECONDARY_ACTION = new Color(255, 160, 0); // Orange accent
    private static final Color COLOR_VIEW_PLACED_ORDERS = new Color(120, 144, 156); // Blue Grey
    private static final Color COLOR_TEXT_ON_PRIMARY = Color.WHITE;
    private static final Color COLOR_CARD_BORDER = new Color(224, 224, 224);
    private static final Color COLOR_BUTTON_ADD = new Color(76, 175, 80); // Green

    public PreOrderPanel() {
        setLayout(new BorderLayout(0, 0)); // Reduced gap
        setBackground(COLOR_BACKGROUND);
        this.customerId = SessionData.getInstance().getUserId();

        // Top Panel: Search + View Order
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        topPanel.setBackground(COLOR_PANEL_BACKGROUND);

        searchField = new JTextField(20);
        searchField.setToolTipText("Search by item name or category...");
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterMenuItems(searchField.getText());
            }
        });

        JButton clearSearchBtn = new JButton("X");
        clearSearchBtn.setToolTipText("Clear search");
        clearSearchBtn.setMargin(new Insets(2, 5, 2, 5));
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            filterMenuItems("");
        });

        JPanel searchContainer = new JPanel(new BorderLayout(5,0));
        searchContainer.setBackground(COLOR_PANEL_BACKGROUND);
        searchContainer.add(searchField, BorderLayout.CENTER);
        searchContainer.add(clearSearchBtn, BorderLayout.EAST);

        JButton viewOrderBtn = new JButton("View Current Order");
        viewOrderBtn.setBackground(COLOR_SECONDARY_ACTION);
        viewOrderBtn.setForeground(COLOR_TEXT_ON_PRIMARY);
        viewOrderBtn.setFocusPainted(false);
        viewOrderBtn.addActionListener(e -> showOrderDialog());

        topPanel.add(new JLabel("Search: "), BorderLayout.WEST);
        topPanel.add(searchContainer, BorderLayout.CENTER);
        topPanel.add(viewOrderBtn, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Menu Items
        itemsPanel = new JPanel(new GridLayout(0, 3, 15, 15)); // Changed to 3 columns
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        itemsPanel.setBackground(COLOR_BACKGROUND);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scrollpane border
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(COLOR_BACKGROUND);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Total + Action Buttons
        totalLabel = new JLabel("Total: R0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(COLOR_PRIMARY_ACTION);
        placeOrderBtn.setForeground(COLOR_TEXT_ON_PRIMARY);
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setFont(new Font("Arial", Font.BOLD, 14));
        placeOrderBtn.addActionListener(e -> placeOrder());

        JButton viewPlacedOrdersBtn = new JButton("View Placed Orders");
        viewPlacedOrdersBtn.setBackground(COLOR_VIEW_PLACED_ORDERS);
        viewPlacedOrdersBtn.setForeground(COLOR_TEXT_ON_PRIMARY);
        viewPlacedOrdersBtn.setFocusPainted(false);
        viewPlacedOrdersBtn.setFont(new Font("Arial", Font.BOLD, 14));
        viewPlacedOrdersBtn.addActionListener(e -> showPlacedOrdersDialog());

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Panel for buttons
        buttonsPanel.setBackground(COLOR_PANEL_BACKGROUND);
        buttonsPanel.add(viewPlacedOrdersBtn);
        buttonsPanel.add(placeOrderBtn);


        JPanel bottomPanel = new JPanel(new BorderLayout(10,10));
        bottomPanel.setBackground(COLOR_PANEL_BACKGROUND);
        bottomPanel.add(totalLabel, BorderLayout.CENTER);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST); // Add buttons panel to EAST
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        add(bottomPanel, BorderLayout.SOUTH);

        loadMenuItems();
        setPreferredSize(new Dimension(950, 700)); // Adjusted preferred size
    }

    private void loadMenuItems() {
        // IMPORTANT: In a production app, run database operations on a background thread (e.g., using SwingWorker)
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             // Assuming 'image_path' column exists, otherwise remove it from query
             ResultSet rs = stmt.executeQuery("SELECT item_id, item_name, description, price, category, image_path FROM menu_items WHERE availability = 1")) {

            menuItems.clear();
            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getString("category"),
                        rs.getString("image_path") // Add this if you have the column
                );
                menuItems.add(item);
            }
            displayMenuItems(menuItems);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayMenuItems(List<MenuItem> itemsToDisplay) {
        itemsPanel.removeAll();
        if (itemsToDisplay.isEmpty()) {
            JLabel noItemsLabel = new JLabel("No menu items match your search.");
            noItemsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noItemsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            itemsPanel.setLayout(new BorderLayout()); // Change layout to center label
            itemsPanel.add(noItemsLabel, BorderLayout.CENTER);
        } else {
            itemsPanel.setLayout(new GridLayout(0, 3, 15, 15)); // Reset to grid
            for (MenuItem item : itemsToDisplay) {
                itemsPanel.add(createMenuItemCard(item));
            }
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1, true), // Rounded border
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE); // Card background

        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(100, 80)); // Slightly smaller image
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            String imagePath = (item.imagePath == null || item.imagePath.trim().isEmpty()) ? "images/default_food.jpg" : item.imagePath;
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaledImg = img.getScaledInstance(100, 80, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaledImg));
        } catch (IOException ex) {
            // Fallback if image loading fails
            imgLabel.setText("No Image");
            imgLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            imgLabel.setForeground(Color.GRAY);
             try { // Try loading a generic default again explicitly
                BufferedImage img = ImageIO.read(new File("images/default_food.jpg"));
                Image scaledImg = img.getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaledImg));
            } catch (IOException ex2) { /* give up on image */ }
        }
        card.add(imgLabel, BorderLayout.WEST);


        JPanel contentPanel = new JPanel(new BorderLayout(5,3));
        contentPanel.setOpaque(false);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel("<html><body style='width: 120px'><b>" + item.name + "</b></body></html>"); // Constrain width
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(nameLabel);

        JLabel categoryLabel = new JLabel("Category: " + item.category);
        categoryLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(categoryLabel);

        JTextArea descArea = new JTextArea(item.description);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Arial", Font.PLAIN, 10));
        descArea.setForeground(Color.DARK_GRAY);
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.getViewport().setOpaque(false);
        descScrollPane.setOpaque(false);
        descScrollPane.setBorder(null);
        descScrollPane.setPreferredSize(new Dimension(120, 30)); // Limit height
        infoPanel.add(descScrollPane);


        JLabel priceLabel = new JLabel("Price: R" + String.format("%.2f", item.price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(priceLabel);

        contentPanel.add(infoPanel, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        controls.setOpaque(false);
        JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        qtySpinner.setPreferredSize(new Dimension(50, 25));

        JButton addBtn = new JButton("Add");
        addBtn.setBackground(COLOR_BUTTON_ADD);
        addBtn.setForeground(COLOR_TEXT_ON_PRIMARY);
        addBtn.setMargin(new Insets(2, 8, 2, 8));
        addBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addBtn.setFocusPainted(false);

        addBtn.addActionListener(e -> {
            int quantity = (Integer) qtySpinner.getValue();
            orderMap.merge(item.id, new OrderItem(item, quantity),
                    (existing, added) -> {
                        existing.quantity += added.quantity;
                        return existing;
                    });
            updateTotal();
            qtySpinner.setValue(1); // Reset spinner
            JOptionPane.showMessageDialog(this, item.name + " (x" + quantity + ") added to order.", "Item Added", JOptionPane.INFORMATION_MESSAGE);
        });

        controls.add(new JLabel("Qty:"));
        controls.add(qtySpinner);
        controls.add(addBtn);
        contentPanel.add(controls, BorderLayout.SOUTH);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private void filterMenuItems(String query) {
        String lower = query.toLowerCase().trim();
        List<MenuItem> filtered = menuItems.stream()
                .filter(item -> item.name.toLowerCase().contains(lower) || item.category.toLowerCase().contains(lower))
                .collect(Collectors.toList());
        displayMenuItems(filtered);
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderMap.values()) {
            total = total.add(item.menuItem.price.multiply(BigDecimal.valueOf(item.quantity)));
        }
        totalLabel.setText("Total: R" + String.format("%.2f", total));
    }

    private void showOrderDialog() {
        if (orderMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your current order is empty.", "Order Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // For a more interactive dialog, you might create a custom JDialog
        // with a JTable and +/- buttons to modify quantities or remove items.
        // This is a simplified summary:
        StringBuilder orderSummary = new StringBuilder("<html><h3>Your Current Order:</h3><table border='0' style='width:300px'>");
        orderSummary.append("<tr><th>Item</th><th>Qty</th><th>Price</th><th>Total</th></tr>");
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : orderMap.values()) {
            BigDecimal lineTotal = item.menuItem.price.multiply(BigDecimal.valueOf(item.quantity));
            total = total.add(lineTotal);
            orderSummary.append("<tr>")
                    .append("<td>").append(item.menuItem.name).append("</td>")
                    .append("<td align='center'>").append(item.quantity).append("</td>")
                    .append("<td align='right'>R").append(String.format("%.2f", item.menuItem.price)).append("</td>")
                    .append("<td align='right'>R").append(String.format("%.2f", lineTotal)).append("</td>")
                    .append("</tr>");
        }
        orderSummary.append("</table><hr><b>Total: R").append(String.format("%.2f", total)).append("</b></html>");

        JOptionPane.showMessageDialog(this, new JLabel(orderSummary.toString()), "Current Order Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void placeOrder() {
        if (orderMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add items to your order first.", "Empty Order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : orderMap.values()) {
            totalAmount = totalAmount.add(item.menuItem.price.multiply(BigDecimal.valueOf(item.quantity)));
        }

        // Database operation
        // IMPORTANT: In a production app, run database operations on a background thread (e.g., using SwingWorker)
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into 'orders' table
            String insertOrderSQL = "INSERT INTO orders (customer_id, total_amount) VALUES (?, ?)";
            long orderId = -1;
            try (PreparedStatement psOrder = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, customerId);
                psOrder.setBigDecimal(2, totalAmount);
                psOrder.executeUpdate();
                try (ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getLong(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }
            
            //  Replace this with your actual way of getting the customer ID
//  This method should return the logged-in user's ID

            // 2. Insert into 'order_details' table
            String insertDetailsSQL = "INSERT INTO order_details (order_id, menu_item_id, quantity, price_at_time_of_order, customer_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psDetails = conn.prepareStatement(insertDetailsSQL)) {
                for (OrderItem orderItem : orderMap.values()) {
                    psDetails.setLong(1, orderId);
                    psDetails.setInt(2, orderItem.menuItem.id);
                    psDetails.setInt(3, orderItem.quantity);
                    psDetails.setBigDecimal(4, orderItem.menuItem.price);
                    psDetails.setInt(5, customerId);
                    psDetails.addBatch();
                }
                psDetails.executeBatch();
            }

            conn.commit(); // Commit transaction
            JOptionPane.showMessageDialog(this, "Order placed successfully! Total: " + totalLabel.getText(), "Order Placed", JOptionPane.INFORMATION_MESSAGE);
            orderMap.clear();
            updateTotal();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error placing order: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showPlacedOrdersDialog() {
        // IMPORTANT: In a production app, run database operations on a background thread (e.g., using SwingWorker)
        JDialog placedOrdersDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Placed Orders History", true);
        placedOrdersDialog.setLayout(new BorderLayout(10,10));
        placedOrdersDialog.setSize(700, 500);
        placedOrdersDialog.setLocationRelativeTo(this);

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Order ID", "Date", "Total Amount", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        JTable ordersTable = new JTable(tableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setReorderingAllowed(false);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement("SELECT order_id, order_date, total_amount, status FROM orders WHERE customer_id = ? ORDER BY order_date DESC")) {

        pstmt.setInt(1, customerId); 
        ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getTimestamp("order_date"),
                        String.format("%.2f", rs.getBigDecimal("total_amount")),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading placed orders: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane tableScrollPane = new JScrollPane(ordersTable);
        placedOrdersDialog.add(tableScrollPane, BorderLayout.CENTER);

        JButton viewDetailsBtn = new JButton("View Details");
        viewDetailsBtn.setBackground(COLOR_SECONDARY_ACTION);
        viewDetailsBtn.setForeground(COLOR_TEXT_ON_PRIMARY);
        viewDetailsBtn.addActionListener(e -> {
            int selectedRow = ordersTable.getSelectedRow();
            if (selectedRow >= 0) {
                int orderId = (Integer) ordersTable.getValueAt(selectedRow, 0);
                showPlacedOrderDetailsDialog(orderId);
            } else {
                JOptionPane.showMessageDialog(placedOrdersDialog, "Please select an order to view its details.", "No Order Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        JPanel bottomDialogPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomDialogPanel.add(viewDetailsBtn);
        placedOrdersDialog.add(bottomDialogPanel, BorderLayout.SOUTH);

        placedOrdersDialog.setVisible(true);
    }

    private void showPlacedOrderDetailsDialog(int order_Id) {
        // IMPORTANT: In a production app, run database operations on a background thread (e.g., using SwingWorker)
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Order Details - ID: " + order_Id, true);
        detailsDialog.setLayout(new BorderLayout(10,10));
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);

        DefaultTableModel detailsTableModel = new DefaultTableModel(new String[]{"Item Name", "Quantity", "Price at Order", "Line Total"}, 0){
             @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        JTable detailsTable = new JTable(detailsTableModel);

        BigDecimal overallTotal = BigDecimal.ZERO;

        String query = "SELECT mi.item_name, od.quantity, od.price_at_time_of_order " +
                       "FROM order_details od " +
                       "JOIN menu_items mi ON od.menu_item_id = mi.item_id " +
                       "WHERE od.order_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, order_Id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String itemName = rs.getString("item_name");
                    int quantity = rs.getInt("quantity");
                    BigDecimal priceAtOrder = rs.getBigDecimal("price_at_time_of_order");
                    BigDecimal lineTotal = priceAtOrder.multiply(BigDecimal.valueOf(quantity));
                    overallTotal = overallTotal.add(lineTotal);
                    detailsTableModel.addRow(new Object[]{
                            itemName,
                            quantity,
                            String.format("%.2f", priceAtOrder),
                            String.format("%.2f", lineTotal)
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading order details: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);
        detailsDialog.add(detailsScrollPane, BorderLayout.CENTER);

        JLabel totalDetailsLabel = new JLabel("Total: R" + String.format("%.2f", overallTotal));
        totalDetailsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalDetailsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalDetailsLabel.setBorder(BorderFactory.createEmptyBorder(5,0,5,10));
        detailsDialog.add(totalDetailsLabel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }


    // --- Helper Classes (Static inner classes for simplicity here) ---
    static class MenuItem {
        int id;
        String name;
        String description;
        BigDecimal price;
        String category;
        String imagePath; // Added for dynamic images

        MenuItem(int id, String name, String description, BigDecimal price, String category, String imagePath) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.category = category;
            this.imagePath = imagePath; // Initialize
        }
    }
    
}
