/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.Border;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 *
 * @author Admin
 */
public class MakeReservationsPanel extends JPanel {

    private JSpinner guestSpinner;
    private JComboBox<String> timeDropdown;
    private JDateChooser dateChooser;
    private JLabel confirmationLabel;
    private int customerId;

    public MakeReservationsPanel() {
        this.customerId = SessionData.getInstance().getUserId();

        // Set panel properties
        setPreferredSize(new Dimension(600, 400));
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Make a Reservation", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);

        // Confirm Panel
        JPanel confirmPanel = createConfirmPanel();
        add(confirmPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date Chooser
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Select Date:"), gbc);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);

        // Time Dropdown
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Time:"), gbc);

        timeDropdown = new JComboBox<>(new String[]{
            "10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"
        });
        gbc.gridx = 1;
        formPanel.add(timeDropdown, gbc);

        // Guests Spinner
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Number of Guests:"), gbc);

        guestSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        gbc.gridx = 1;
        formPanel.add(guestSpinner, gbc);

        return formPanel;
    }

    private JPanel createConfirmPanel() {
        JPanel confirmPanel = new JPanel(new BorderLayout());
        confirmPanel.setBackground(new Color(245, 245, 245));

        confirmationLabel = new JLabel("Please select your reservation details.", SwingConstants.CENTER);
        confirmationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmationLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        confirmPanel.add(confirmationLabel, BorderLayout.CENTER);

        JButton confirmButton = new JButton("Confirm Reservation");
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
        confirmButton.setBackground(new Color(0, 153, 76));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setPreferredSize(new Dimension(0, 40));

        confirmButton.addActionListener(this::handleConfirmAction);

        confirmPanel.add(confirmButton, BorderLayout.SOUTH);
        return confirmPanel;
    }

    private void handleConfirmAction(ActionEvent e) {
        Date date = dateChooser.getDate();
        String time = (String) timeDropdown.getSelectedItem();
        int guests = (int) guestSpinner.getValue();

        if (date == null) {
            JOptionPane.showMessageDialog(this, "Please select a reservation date.", "Missing Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (date.before(new java.util.Date())) {
            JOptionPane.showMessageDialog(this, "Please select a future date.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert to SQL Date
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        // Save reservation to the database
        String assignedTable = assignTableToReservation(sqlDate, time, guests);

        if (assignedTable != null) {
            confirmationLabel.setText(String.format(
                    "<html><center><b>Reservation Confirmed!</b><br>Date: %s<br>Time: %s<br>Guests: %d<br>Table: %s</center></html>",
                    sqlDate, time, guests, assignedTable
            ));
        } else {
            confirmationLabel.setText("<html><center><b>Error:</b> Could not assign a table or save reservation.</center></html>");
        }
    }

    private String assignTableToReservation(java.sql.Date date, String time, int guests) {
        String url = "jdbc:mysql://localhost:3306/registration";
        String user = "root";
        String password = "@Chante2004";

        String selectTableSql = "SELECT table_id, capacity FROM tables WHERE capacity >= ? AND table_id NOT IN (" +
                "SELECT table_id FROM reservations WHERE reservation_date = ? AND reservation_time = ?" +
                ") ORDER BY capacity ASC LIMIT 1";

        String insertReservationSql = "INSERT INTO reservations (customer_id, reservation_date, reservation_time, guest_count, table_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement selectStmt = conn.prepareStatement(selectTableSql)) {

            // Find a suitable table
            selectStmt.setInt(1, guests);
            selectStmt.setDate(2, date);
            selectStmt.setString(3, time);

            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int tableId = rs.getInt("table_id");
                int capacity = rs.getInt("capacity");

                // Save the reservation
                try (PreparedStatement insertStmt = conn.prepareStatement(insertReservationSql)) {
                    insertStmt.setInt(1, customerId);
                    insertStmt.setDate(2, date);
                    insertStmt.setString(3, time);
                    insertStmt.setInt(4, guests);
                    insertStmt.setInt(5, tableId);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        return "Table " + tableId + " (Capacity: " + capacity + ")";
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No suitable table available for the selected time.", "No Table Available", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        return null;
    }

}
