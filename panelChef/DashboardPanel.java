/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelChef;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

/**
 *
 * @author Admin
 */
public class DashboardPanel extends JPanel{
    
    private JList<String> incomingOrdersList;
    private JList<String> preparingOrdersList;
    private JList<String> readyOrdersList;

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(248, 249, 250));
        setPreferredSize(new Dimension(700, 500));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Chef Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(52, 58, 64));
        add(titleLabel, BorderLayout.NORTH);

        // Center panel with three columns
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        centerPanel.setBackground(new Color(248, 249, 250));

        incomingOrdersList = new JList<>();
        preparingOrdersList = new JList<>();
        readyOrdersList = new JList<>();

        setListRenderer(incomingOrdersList);
        setListRenderer(preparingOrdersList);
        setListRenderer(readyOrdersList);

        centerPanel.add(createOrderSection("Incoming", incomingOrdersList, new Color(173, 216, 230))); // Blue
        centerPanel.add(createOrderSection("Preparing", preparingOrdersList, new Color(144, 238, 144))); // Green
        centerPanel.add(createOrderSection("Ready", readyOrdersList, new Color(255, 165, 0))); // Orange

        add(centerPanel, BorderLayout.CENTER);

        // Refresh Button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(new Color(25, 135, 84));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(160, 36));
        refreshButton.addActionListener(e -> loadOrdersFromDatabase());

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(248, 249, 250));
        bottomPanel.add(refreshButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load orders initially
        loadOrdersFromDatabase();
    }

    private JPanel createOrderSection(String title, JList<String> orderList, Color backgroundColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 8, 8, 8)
        ));

        JLabel sectionLabel = new JLabel(title, SwingConstants.CENTER);
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionLabel.setForeground(new Color(33, 37, 41));
        sectionLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        orderList.setVisibleRowCount(6);
        orderList.setFixedCellHeight(28);
        orderList.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JScrollPane scrollPane = new JScrollPane(orderList);
        scrollPane.setPreferredSize(new Dimension(210, 170));

        panel.add(sectionLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void setListRenderer(JList<String> list) {
        list.setCellRenderer((JList<? extends String> l, String value, int index, boolean isSelected, boolean cellHasFocus) -> {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(4, 8, 4, 8));
            panel.setBackground(isSelected ? new Color(200, 230, 255) : Color.WHITE);

            JLabel label = new JLabel(value);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setForeground(new Color(33, 37, 41));

            panel.add(label, BorderLayout.CENTER);
            return panel;
        });
    }

    private void loadOrdersFromDatabase() {
        String dbUrl = "jdbc:mysql://localhost:3306/registration";
        String dbUser = "root";
        String dbPassword = "@Chante2004";

        DefaultListModel<String> incomingOrdersModel = new DefaultListModel<>();
        DefaultListModel<String> preparingOrdersModel = new DefaultListModel<>();
        DefaultListModel<String> readyOrdersModel = new DefaultListModel<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT order_id, order_type, status FROM orders");

            while (resultSet.next()) {
                String order = "Order #" + resultSet.getInt("order_id");
                String status = resultSet.getString("status");

                switch (status) {
                    case "Pending":
                        incomingOrdersModel.addElement(order);
                        break;
                    case "Preparing":
                        preparingOrdersModel.addElement(order);
                        break;
                    case "Ready":
                        readyOrdersModel.addElement(order);
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        incomingOrdersList.setModel(incomingOrdersModel);
        preparingOrdersList.setModel(preparingOrdersModel);
        readyOrdersList.setModel(readyOrdersModel);
    }

    

    
}
