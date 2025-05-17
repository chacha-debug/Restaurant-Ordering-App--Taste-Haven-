/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class MakePaymentsPanel extends JPanel {

    private JLabel totalAmountLabel;
    private ButtonGroup paymentMethodGroup;
    private JButton payButton;
     private JButton refreshButton; 
    private JTextArea orderSummaryArea;
    private JPanel paymentDetailsPanel;

    private JTextField nameField, cardNumberField, expiryMonthField, expiryYearField, ccvField;

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("R#,##0.00");

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Chante2004";

    private int customerId; // Customer ID for fetching order details
    private int currentOrderId ; // To store the current order ID

    public MakePaymentsPanel() {
        this.customerId = SessionData.getInstance().getUserId();
        initComponents();
        fetchAndDisplayOrderDetails(); // Fetch and display the order for this customer
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Order Summary Section
        JPanel orderSummaryPanel = new JPanel(new BorderLayout(10, 10));
        orderSummaryPanel.setBackground(Color.WHITE);
        orderSummaryPanel.setBorder(BorderFactory.createTitledBorder("Order Summary"));

        orderSummaryArea = new JTextArea(10, 45);
        orderSummaryArea.setEditable(false);
        orderSummaryArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        orderSummaryArea.setLineWrap(true);
        orderSummaryArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(orderSummaryArea);
        orderSummaryPanel.add(scrollPane, BorderLayout.CENTER);

        add(orderSummaryPanel, BorderLayout.NORTH);

        // Payment Methods Section
        JPanel paymentMethodPanel = new JPanel(new BorderLayout());
        paymentMethodPanel.setBackground(Color.WHITE);
        paymentMethodPanel.setBorder(BorderFactory.createTitledBorder("Select Payment Method"));

        paymentMethodGroup = new ButtonGroup();
        JPanel paymentOptionsPanel = new JPanel();
        paymentOptionsPanel.setLayout(new BoxLayout(paymentOptionsPanel, BoxLayout.Y_AXIS));
        paymentOptionsPanel.setBackground(Color.WHITE);

        String[] paymentOptions = {"Credit/Debit Card", "Cash on Delivery"};
        for (String option : paymentOptions) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setBackground(Color.WHITE);
            radioButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            paymentMethodGroup.add(radioButton);
            paymentOptionsPanel.add(radioButton);

            if (option.equals("Credit/Debit Card")) {
                radioButton.setSelected(true);
                radioButton.addActionListener(e -> showCardDetails());
            } else {
                radioButton.addActionListener(e -> hideCardDetails());
            }
        }

        paymentDetailsPanel = new JPanel();
        paymentDetailsPanel.setLayout(new GridBagLayout());
        paymentDetailsPanel.setBackground(Color.WHITE);
        paymentDetailsPanel.setBorder(BorderFactory.createTitledBorder("Credit/Debit Card Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cardholder Name
        JLabel nameLabel = new JLabel("Cardholder Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        paymentDetailsPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        paymentDetailsPanel.add(nameField, gbc);

        // Card Number
        JLabel cardNumberLabel = new JLabel("Card Number:");
        cardNumberLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardNumberField = new JTextField(20);
        cardNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        paymentDetailsPanel.add(cardNumberLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        paymentDetailsPanel.add(cardNumberField, gbc);

        // Expiry Date
        JLabel expiryLabel = new JLabel("Expiry Date (MM/YY):");
        expiryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expiryMonthField = new JTextField(2);
        expiryMonthField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        expiryYearField = new JTextField(2);
        expiryYearField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expiryPanel.setBackground(Color.WHITE);
        expiryPanel.add(expiryMonthField);
        expiryPanel.add(new JLabel("/"));
        expiryPanel.add(expiryYearField);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        paymentDetailsPanel.add(expiryLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        paymentDetailsPanel.add(expiryPanel, gbc);

        // CCV
        JLabel ccvLabel = new JLabel("CCV:");
        ccvLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ccvField = new JTextField(3);
        ccvField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        paymentDetailsPanel.add(ccvLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        paymentDetailsPanel.add(ccvField, gbc);

        paymentMethodPanel.add(paymentOptionsPanel, BorderLayout.CENTER);
        paymentMethodPanel.add(paymentDetailsPanel, BorderLayout.SOUTH);

        add(paymentMethodPanel, BorderLayout.CENTER);

        // Actions Section
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);
        
        refreshButton = new JButton("Refresh"); // Added Refresh Button
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.addActionListener(e -> fetchAndDisplayOrderDetails());
        actionsPanel.add(refreshButton);

        totalAmountLabel = new JLabel("Total: " + CURRENCY_FORMAT.format(0.0));
        totalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionsPanel.add(totalAmountLabel);

        payButton = new JButton("Make Payment");
        payButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        payButton.setBackground(new Color(0, 123, 255));
        payButton.setForeground(Color.WHITE);
        payButton.addActionListener(e -> processPayment());
        actionsPanel.add(payButton);

        add(actionsPanel, BorderLayout.SOUTH);

        showCardDetails(); // Show card details by default
    }

    private void showCardDetails() {
        paymentDetailsPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void hideCardDetails() {
        paymentDetailsPanel.setVisible(false);
        revalidate();
        repaint();
    }

    private void fetchAndDisplayOrderDetails() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT o.order_id, mi.item_name, od.quantity, od.price_at_time_of_order, o.total_amount " +
                             "FROM orders o " +
                             "JOIN order_details od ON o.order_id = od.order_id " +
                             "JOIN menu_items mi ON od.menu_item_id = mi.item_id " +
                             "WHERE o.customer_id = ? AND o.status = 'Pending' " +
                             "ORDER BY o.order_date DESC LIMIT 1")) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder summary = new StringBuilder();
            double totalPrice = 0.0;

            boolean found = false;
summary.setLength(0); // Clear previous data
summary.append("----------------------------------------\n");
summary.append(String.format("%-20s %3s %10s\n", "Item", "Qty", "Price"));
summary.append("----------------------------------------\n");

while (resultSet.next()) {
    if (!found) {
        currentOrderId = resultSet.getInt("order_id");
        summary.insert(0, "Order ID: " + currentOrderId + "\n");
        found = true;
        totalPrice = resultSet.getDouble("total_amount"); // Get once
    }

    String itemName = resultSet.getString("item_name");
    int quantity = resultSet.getInt("quantity");
    double price = resultSet.getDouble("price_at_time_of_order");
    summary.append(String.format("%-20.20s %3d %10s\n",
            itemName, quantity, CURRENCY_FORMAT.format(price)));
}

if (found) {
    summary.append("----------------------------------------\n");
    summary.append(String.format("%-24s %10s\n", "Total", CURRENCY_FORMAT.format(totalPrice)));
} else {
    summary.append("No pending orders found.");
}

orderSummaryArea.setText(summary.toString());
totalAmountLabel.setText("Total: " + CURRENCY_FORMAT.format(totalPrice));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch order details: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void processPayment() {
        String selectedPaymentMethod = null;

        for (AbstractButton button : java.util.Collections.list(paymentMethodGroup.getElements())) {
            if (button.isSelected()) {
                selectedPaymentMethod = button.getText();
                break;
            }
        }

        if (selectedPaymentMethod == null) {
            JOptionPane.showMessageDialog(this, "Please select a payment method.", "Payment Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedPaymentMethod.equals("Credit/Debit Card")) {
            String name = nameField.getText().trim();
            String cardNumber = cardNumberField.getText().trim();
            String expiryMonth = expiryMonthField.getText().trim();
            String expiryYear = expiryYearField.getText().trim();
            String ccv = ccvField.getText().trim();

            // Validate card details
            if (name.isEmpty() || !name.matches("[a-zA-Z ]+")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid cardholder name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cardNumber.isEmpty() || !cardNumber.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (expiryMonth.isEmpty() || expiryYear.isEmpty() || Integer.parseInt(expiryMonth) < 1 || Integer.parseInt(expiryMonth) > 12) {
                JOptionPane.showMessageDialog(this, "Please enter a valid expiry date.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (ccv.isEmpty() || !ccv.matches("\\d{3}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 3-digit CCV.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (selectedPaymentMethod.equals("Cash on Delivery")) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to choose Cash on Delivery?",
                    "Confirm Cash on Delivery",
                    JOptionPane.YES_NO_OPTION);
            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Save payment slip
        String receiptText = generateReceiptText(selectedPaymentMethod);
        savePaymentSlip(currentOrderId, selectedPaymentMethod, receiptText);

        // Clear card details
        clearCardDetails();

        // Display receipt
        displayReceipt(receiptText);

        JOptionPane.showMessageDialog(this, "Payment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private String generateReceiptText(String paymentMethod) {
        return "Receipt\n" +
                "========================================\n" +
                orderSummaryArea.getText() +
                "\nPayment Method: " + paymentMethod +
                "\nDate: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                "\n========================================\n" +
                "Thank you for your purchase!";
    }

    private void clearCardDetails() {
        nameField.setText("");
        cardNumberField.setText("");
        expiryMonthField.setText("");
        expiryYearField.setText("");
        ccvField.setText("");
    }

    private void displayReceipt(String receiptText) {
        JTextArea receiptArea = new JTextArea(receiptText);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Consolas", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(receiptArea);

        JDialog dialog = new JDialog((Frame) null, "Receipt", true);
        dialog.add(scrollPane);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void savePaymentSlip(int orderId, String paymentMethod, String receiptText) {
        String sql = "INSERT INTO payment_slips (order_id, payment_method, receipt_text, payment_date) VALUES (?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, orderId);
            preparedStatement.setString(2, paymentMethod);
            preparedStatement.setString(3, receiptText);
            preparedStatement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            preparedStatement.executeUpdate();

            System.out.println("Payment slip saved to the database for Order ID: " + orderId);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save payment slip: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}