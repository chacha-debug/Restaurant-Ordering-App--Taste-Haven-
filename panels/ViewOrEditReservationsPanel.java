/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import com.toedter.calendar.JDateChooser;
import java.util.Properties;
import javax.swing.table.DefaultTableModel;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

/**
 *
 * @author Admin
 */
public class ViewOrEditReservationsPanel extends JPanel {

    private JTable reservationTable;
    private DefaultTableModel tableModel;
    private JSpinner guestSpinner;
    private JComboBox<String> timeDropdown;
    private JDatePickerImpl datePicker;
    private JLabel confirmationLabel;

    private final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private final String USER = "root";
    private final String PASS = "@Chante2004";
    
    private final int customerId; 

    public ViewOrEditReservationsPanel() {
        
        this.customerId = SessionData.getInstance().getUserId();
        setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Initialize components first
        initializeBottomPanel(); // This initializes confirmationLabel
        initializeTable();
        initializeEditPanel();

        // Load data from database
        loadReservationsFromDatabase();
    }

    private void initializeTable() {
        // Table setup
        tableModel = new DefaultTableModel(new Object[]{"Reservation ID", "Date", "Time", "Guests"}, 0);
        reservationTable = new JTable(tableModel);
        reservationTable.setFillsViewportHeight(true);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(reservationTable);
        tableScroll.setPreferredSize(new Dimension(780, 200));
        add(tableScroll, BorderLayout.NORTH);

        // Show reservation details on row click
        reservationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = reservationTable.getSelectedRow();
                if (selectedRow != -1) {
                    String id = (String) tableModel.getValueAt(selectedRow, 0);
                    String date = (String) tableModel.getValueAt(selectedRow, 1);
                    String time = (String) tableModel.getValueAt(selectedRow, 2);
                    String guests = tableModel.getValueAt(selectedRow, 3).toString();

                    JOptionPane.showMessageDialog(ViewOrEditReservationsPanel.this,
                            "Reservation Details:\n"
                            + "ID: " + id + "\n"
                            + "Date: " + date + "\n"
                            + "Time: " + time + "\n"
                            + "Guests: " + guests,
                            "Reservation Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    private void initializeEditPanel() {
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        editPanel.setBackground(new Color(245, 245, 245));

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        editPanel.add(new JLabel("Date:"));
        editPanel.add(datePicker);

        timeDropdown = new JComboBox<>(new String[]{"10:00 AM", "11:00 AM", "12:00 PM", "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM"});
        editPanel.add(new JLabel("Time:"));
        editPanel.add(timeDropdown);

        guestSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        editPanel.add(new JLabel("Guests:"));
        editPanel.add(guestSpinner);

        add(editPanel, BorderLayout.CENTER);
    }

    private void initializeBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        confirmationLabel = new JLabel("Select a reservation to edit.", SwingConstants.CENTER); // Initialize confirmationLabel
        confirmationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(confirmationLabel, BorderLayout.CENTER);

        JButton editButton = new JButton("Edit Reservation");
        editButton.setFont(new Font("Arial", Font.BOLD, 14));
        editButton.setBackground(new Color(0, 123, 255));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.setPreferredSize(new Dimension(180, 40));
        editButton.addActionListener(e -> handleEditAction());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.setBackground(new Color(0, 153, 76));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(120, 40));
        refreshButton.addActionListener(e -> {
            reloadReservationTable();
            confirmationLabel.setText("Reservations refreshed.");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void handleEditAction() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow != -1) {
            String reservationId = (String) tableModel.getValueAt(selectedRow, 0);
            java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
            String selectedTime = (String) timeDropdown.getSelectedItem();
            int guests = (int) guestSpinner.getValue();

            if (selectedDate != null && selectedTime != null) {
                String dateStr = new java.sql.Date(selectedDate.getTime()).toString();
                updateReservationInDatabase(reservationId, dateStr, selectedTime, guests);
                reloadReservationTable();

                confirmationLabel.setText("Reservation " + reservationId + " updated successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a reservation to edit.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void reloadReservationTable() {
        tableModel.setRowCount(0);
        loadReservationsFromDatabase();
    }

    private void loadReservationsFromDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT id, reservation_date, reservation_time, guest_count FROM reservations WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, customerId); // Filter by customerId
            ResultSet rs = stmt.executeQuery();

            boolean hasReservations = false; // Track if reservations exist
            while (rs.next()) {
                hasReservations = true;
                String id = rs.getString("id");
                String date = rs.getString("reservation_date");
                String time = rs.getString("reservation_time");
                int guests = rs.getInt("guest_count");
                tableModel.addRow(new Object[]{id, date, time, guests});
            }

            if (!hasReservations) {
                confirmationLabel.setText("No reservations found.");
            } else {
                confirmationLabel.setText("Select a reservation to edit.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load reservations from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateReservationInDatabase(String id, String date, String time, int guests) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String updateQuery = "UPDATE reservations SET reservation_date = ?, reservation_time = ?, guest_count = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, date);
            stmt.setString(2, time);
            stmt.setInt(3, guests);
            stmt.setString(4, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update reservation.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
