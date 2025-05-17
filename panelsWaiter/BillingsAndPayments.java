/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelsWaiter;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
/**
 *
 * @author Admin
 */
public class BillingsAndPayments extends JPanel{
    
    private JTable paymentsTable;
    private JTextArea receiptArea;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton clearButton;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/registration";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@Chante2004";
    private static final int DEBOUNCE_DELAY = 500;  // Delay in milliseconds

    public BillingsAndPayments() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));  // Light grey background for the panel
        setPreferredSize(new Dimension(800, 800));
        // Initialize the table model
        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Slip ID", "Order ID", "Payment Method", "Date"});
        paymentsTable = new JTable(tableModel);
        paymentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paymentsTable.setRowHeight(30);
        paymentsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add a row sorter for the table
        rowSorter = new TableRowSorter<>(tableModel);
        paymentsTable.setRowSorter(rowSorter);

        JScrollPane tableScroll = new JScrollPane(paymentsTable);

        // TextArea to display the receipt details
        receiptArea = new JTextArea(10, 30);
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Arial", Font.PLAIN, 12));
        receiptArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        JScrollPane receiptScroll = new JScrollPane(receiptArea);

        // Load payments data into the table
        loadPaymentsData();

        // Listen for table row selection to load receipt details
        paymentsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = paymentsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int orderId = (int) tableModel.getValueAt(paymentsTable.convertRowIndexToModel(selectedRow), 1);
                    loadPaymentSlip(orderId);
                }
            }
        });

        // Panel to organize the layout
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.setBackground(new Color(245, 245, 245));  // Match panel background
        panel.add(tableScroll);
        panel.add(receiptScroll);

        add(panel, BorderLayout.CENTER);

        // Search bar and clear button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 245, 245));  // Background color
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(new Color(60, 60, 60));  // Darker text color
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setToolTipText("Search by Payment ID, Order ID, or Method");
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.setBackground(new Color(255, 69, 0));  // Orange background
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setToolTipText("Clear the receipt display");
        clearButton.addActionListener(e -> receiptArea.setText(""));

        // Real-time search functionality with debounce
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer = new Timer(true);

            @Override
            public void insertUpdate(DocumentEvent e) {
                debounceSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                debounceSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                debounceSearch();
            }

            private void debounceSearch() {
                timer.cancel();
                timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(() -> filterTable());
                    }
                }, DEBOUNCE_DELAY);
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearButton);

        add(searchPanel, BorderLayout.NORTH);

        // Format the table for better presentation
        formatTable();
    }

    // Database connection method
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Load payment data into the table
    private void loadPaymentsData() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT slip_id, order_id, payment_method, payment_date FROM payment_slips");
             ResultSet resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                long slipId = resultSet.getLong("slip_id");
                int orderId = resultSet.getInt("order_id");
                String paymentMethod = resultSet.getString("payment_method");
                String paymentDate = resultSet.getString("payment_date");

                tableModel.addRow(new Object[]{slipId, orderId, paymentMethod, paymentDate});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading payment slips: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Load the receipt details for a specific order
    private void loadPaymentSlip(int orderId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT receipt_text FROM payment_slips WHERE order_id = ?")) {

            stmt.setInt(1, orderId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                String receiptText = resultSet.getString("receipt_text");
                receiptArea.setText(receiptText);
            } else {
                receiptArea.setText("No receipt found for Order ID: " + orderId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading receipt: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Format the table with proper fonts and alternating row colors
    private void formatTable() {
        paymentsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        paymentsTable.setRowHeight(30);

        // Apply alternating row colors
        paymentsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(240, 240, 240) : Color.WHITE);
                }
                return c;
            }
        });
    }

    // Filter table based on search input
    private void filterTable() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    
    
}
