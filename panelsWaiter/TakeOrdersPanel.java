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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.border.TitledBorder;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class TakeOrdersPanel extends JPanel{
    
    private JComboBox<String> categoryComboBox;
    private JTable menuTable;
    private JTable orderSummaryTable;
    private DefaultTableModel menuTableModel;
    private DefaultTableModel orderSummaryTableModel;
    private JLabel totalAmountLabel;
    private JButton finalizeOrderButton;
    private JButton resetButton;
    private JButton cancelButton;
    private JButton makePaymentButton;
    private JButton deleteItemButton;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Chante2004";

    private final List<OrderItem> orderItems;

    public TakeOrdersPanel() {
        orderItems = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 800));

        // Title
        JLabel titleLabel = new JLabel("Take Orders", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        add(mainPanel, BorderLayout.CENTER);

        // Menu Panel
        JPanel menuPanel = new JPanel(new BorderLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
        mainPanel.add(menuPanel);

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.addActionListener(e -> loadMenuItems());
        menuPanel.add(categoryComboBox, BorderLayout.NORTH);

        menuTableModel = new DefaultTableModel(new String[]{"Item ID", "Item Name", "Price"}, 0);
        menuTable = new JTable(menuTableModel);
        menuTable.setRowHeight(30);
        menuTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane menuScrollPane = new JScrollPane(menuTable);
        menuPanel.add(menuScrollPane, BorderLayout.CENTER);

        JButton addItemButton = new JButton("Add Item");
        addItemButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addItemButton.addActionListener(e -> addItemToOrder());
        menuPanel.add(addItemButton, BorderLayout.SOUTH);

        // Order Summary Panel
        JPanel orderSummaryPanel = new JPanel(new BorderLayout());
        orderSummaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));
        mainPanel.add(orderSummaryPanel);

        orderSummaryTableModel = new DefaultTableModel(new String[]{"Item Name", "Price", "Qty", "Total"}, 0);
        orderSummaryTable = new JTable(orderSummaryTableModel);
        orderSummaryTable.setRowHeight(30);
        orderSummaryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane orderSummaryScrollPane = new JScrollPane(orderSummaryTable);
        orderSummaryPanel.add(orderSummaryScrollPane, BorderLayout.CENTER);

        JPanel summaryBottomPanel = new JPanel(new BorderLayout());
        totalAmountLabel = new JLabel("Total: R0.00", SwingConstants.RIGHT);
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalAmountLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        summaryBottomPanel.add(totalAmountLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        finalizeOrderButton = new JButton("Finalize Order");
        finalizeOrderButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        finalizeOrderButton.addActionListener(e -> finalizeOrder());
        buttonPanel.add(finalizeOrderButton);

        resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.addActionListener(e -> resetOrder());
        buttonPanel.add(resetButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.addActionListener(e -> cancelOrder());
        buttonPanel.add(cancelButton);

        makePaymentButton = new JButton("Make Payment");
        makePaymentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        makePaymentButton.addActionListener(e -> handlePayment());
        buttonPanel.add(makePaymentButton);

        // New Delete Item Button
        deleteItemButton = new JButton("Delete Item");
        deleteItemButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteItemButton.addActionListener(e -> deleteSelectedItem());
        buttonPanel.add(deleteItemButton);

        summaryBottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        orderSummaryPanel.add(summaryBottomPanel, BorderLayout.SOUTH);

        // Load categories and menu items
        loadCategories();
    }

    private void loadCategories() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT category FROM menu_items")) {

            while (rs.next()) {
                categoryComboBox.addItem(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMenuItems() {
        menuTableModel.setRowCount(0);
        String selectedCategory = (String) categoryComboBox.getSelectedItem();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT item_id, item_name, price FROM menu_items WHERE category = ?")) {

            stmt.setString(1, selectedCategory);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                menuTableModel.addRow(new Object[]{rs.getInt("item_id"), rs.getString("item_name"), rs.getDouble("price")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading menu items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addItemToOrder() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to add.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemName = (String) menuTableModel.getValueAt(selectedRow, 1);
        double price = (double) menuTableModel.getValueAt(selectedRow, 2);
        boolean itemExists = false;

        for (OrderItem orderItem : orderItems) {
            if (orderItem.getItemName().equals(itemName)) {
                orderItem.incrementQuantity();
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            orderItems.add(new OrderItem(itemName, price, 1));
        }

        updateOrderSummary();
    }

    private void updateOrderSummary() {
        orderSummaryTableModel.setRowCount(0);

        double total = 0;
        for (OrderItem orderItem : orderItems) {
            orderSummaryTableModel.addRow(new Object[]{orderItem.getItemName(), orderItem.getPrice(), orderItem.getQuantity(), orderItem.getTotal()});
            total += orderItem.getTotal();
        }

        totalAmountLabel.setText(String.format("Total: R%.2f", total));
    }

    private void finalizeOrder() {
        JOptionPane.showMessageDialog(this, "Order finalized successfully!", "Order Finalized", JOptionPane.INFORMATION_MESSAGE);
        resetOrder();
    }

    private void resetOrder() {
        orderItems.clear();
        updateOrderSummary();
    }

    private void cancelOrder() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel the order?", "Cancel Order", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            resetOrder();
        }
    }

    private void handlePayment() {
        double totalAmount = getTotalAmount();

        if (totalAmount <= 0) {
            JOptionPane.showMessageDialog(this, "Please add items to the order before making payment.", "No Items in Order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] options = {"Cash", "Card"};
        int choice = JOptionPane.showOptionDialog(this, "Choose Payment Method", "Payment", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        String paymentMethod = options[choice];
        JOptionPane.showMessageDialog(this, "Payment of R" + totalAmount + " made using " + paymentMethod + ".", "Payment Successful", JOptionPane.INFORMATION_MESSAGE);

        generateBill();
    }

    private double getTotalAmount() {
        double total = 0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getTotal();
        }
        return total;
    }

    private void generateBill() {
        StringBuilder bill = new StringBuilder();
        bill.append("----- BILL -----\n");

        for (OrderItem orderItem : orderItems) {
            bill.append(orderItem.getItemName())
                .append(" - Qty: ").append(orderItem.getQuantity())
                .append(" - R").append(orderItem.getTotal()).append("\n");
        }

        bill.append("-----------------\n");
        bill.append("Total: R").append(getTotalAmount()).append("\n");
        bill.append("Thank you for your order!\n");

        System.out.println(bill.toString());
        JOptionPane.showMessageDialog(this, bill.toString(), "Generated Bill", JOptionPane.INFORMATION_MESSAGE);

        resetOrder();
    }

    // New Method to Delete Selected Item
    private void deleteSelectedItem() {
        int selectedRow = orderSummaryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemName = (String) orderSummaryTableModel.getValueAt(selectedRow, 0);
        orderItems.removeIf(orderItem -> orderItem.getItemName().equals(itemName));

        updateOrderSummary();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // OrderItem Class
    private static class OrderItem {
        private String itemName;
        private double price;
        private int quantity;

        public OrderItem(String itemName, double price, int quantity) {
            this.itemName = itemName;
            this.price = price;
            this.quantity = quantity;
        }

        public String getItemName() {
            return itemName;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getTotal() {
            return price * quantity;
        }

        public void incrementQuantity() {
            this.quantity++;
        }
    }
}
    

