/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelManager;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
/**
 *
 * @author Admin
 */
public class DashboardPanel extends JPanel{
    
    private JTable upcomingLeavesTable;
    private JTable bigOrdersTable;
    private JTable customerFeedbackTable;
    private JLabel employeeOfTheMonthLabel;

    private DefaultTableModel upcomingLeavesModel;
    private DefaultTableModel bigOrdersModel;
    private DefaultTableModel customerFeedbackModel;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(20, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Main Panels
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        mainPanel.setBackground(new Color(245, 245, 245));
        add(mainPanel, BorderLayout.CENTER);

        // Add Panels
        mainPanel.add(createUpcomingLeavesPanel());
        mainPanel.add(createEmployeeOfTheMonthPanel());
        mainPanel.add(createBigOrdersPanel());
        mainPanel.add(createCustomerFeedbackPanel());

        // Load Mock Data
        loadUpcomingLeaves();
        loadEmployeeOfTheMonth();
        loadBigOrders();
        loadCustomerFeedback();
    }

    private JPanel createUpcomingLeavesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Upcoming Leaves"));
        panel.setBackground(Color.WHITE);

        upcomingLeavesModel = new DefaultTableModel(new String[]{"Employee Name", "Leave Date", "Reason"}, 0);
        upcomingLeavesTable = new JTable(upcomingLeavesModel);
        upcomingLeavesTable.setRowHeight(25);
        upcomingLeavesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(upcomingLeavesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEmployeeOfTheMonthPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee of the Month"));
        panel.setBackground(Color.WHITE);

        employeeOfTheMonthLabel = new JLabel("Loading...", SwingConstants.CENTER);
        employeeOfTheMonthLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(employeeOfTheMonthLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBigOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("This Week's Big Orders"));
        panel.setBackground(Color.WHITE);

        bigOrdersModel = new DefaultTableModel(new String[]{"Order ID", "Customer Name", "Total Amount"}, 0);
        bigOrdersTable = new JTable(bigOrdersModel);
        bigOrdersTable.setRowHeight(25);
        bigOrdersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(bigOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Customer Feedback"));
        panel.setBackground(Color.WHITE);

        customerFeedbackModel = new DefaultTableModel(new String[]{"Customer Name", "Feedback", "Date"}, 0);
        customerFeedbackTable = new JTable(customerFeedbackModel);
        customerFeedbackTable.setRowHeight(25);
        customerFeedbackTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(customerFeedbackTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadUpcomingLeaves() {
        upcomingLeavesModel.setRowCount(0);

        // Mock Data
        upcomingLeavesModel.addRow(new Object[]{"John Doe", "2025-05-15", "Personal Leave"});
        upcomingLeavesModel.addRow(new Object[]{"Jane Smith", "2025-05-18", "Medical Appointment"});
        upcomingLeavesModel.addRow(new Object[]{"Michael Johnson", "2025-05-20", "Vacation"});
    }

    private void loadEmployeeOfTheMonth() {
        // Mock Data
        employeeOfTheMonthLabel.setText("Emily Davis");
    }

    private void loadBigOrders() {
        bigOrdersModel.setRowCount(0);

        // Mock Data
        bigOrdersModel.addRow(new Object[]{"12345", "Robert Brown", "R1,200.00"});
        bigOrdersModel.addRow(new Object[]{"12346", "Sarah Wilson", "R950.00"});
        bigOrdersModel.addRow(new Object[]{"12347", "David Clark", "R870.00"});
        bigOrdersModel.addRow(new Object[]{"12348", "Sophia Martinez", "R730.00"});
        bigOrdersModel.addRow(new Object[]{"12349", "James Taylor", "R680.00"});
    }

    private void loadCustomerFeedback() {
        customerFeedbackModel.setRowCount(0);

        // Mock Data
        customerFeedbackModel.addRow(new Object[]{"Alice Johnson", "Great service and food!", "2025-05-10"});
        customerFeedbackModel.addRow(new Object[]{"Ethan Miller", "Order was delayed but staff was friendly.", "2025-05-09"});
        customerFeedbackModel.addRow(new Object[]{"Olivia Garcia", "Loved the ambiance and presentation!", "2025-05-08"});
        customerFeedbackModel.addRow(new Object[]{"William Martinez", "A bit pricey, but worth it.", "2025-05-07"});
        customerFeedbackModel.addRow(new Object[]{"Sophia Anderson", "Best experience ever!", "2025-05-06"});
    }
    
}
