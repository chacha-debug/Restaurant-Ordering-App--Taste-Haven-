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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Admin
 */
public class ManageReservationPanel extends JPanel {

    private JTable tblReservations;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbFilter;
    private JButton btnFilter, btnReset, btnAllocate, btnDelete;
    private JLabel lblStatus;

    // JDBC settings – adjust to your environment
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "@Chante2004";

    public ManageReservationPanel() {
        setLayout(new BorderLayout(15,15));
        setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        setBackground(Color.WHITE);

        // Title
        JLabel lblTitle = new JLabel("Manage Reservations", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        // Table model: include hidden reservation_id & customer_id for operations
        tableModel = new DefaultTableModel(new String[]{
            "ResID","CustID","Name","Contact","Date","Time","Guests","Requests","Table#"
        }, 0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tblReservations = new JTable(tableModel);
        // hide internal IDs
        tblReservations.getColumnModel().getColumn(0).setMinWidth(0);
        tblReservations.getColumnModel().getColumn(0).setMaxWidth(0);
        tblReservations.getColumnModel().getColumn(1).setMinWidth(0);
        tblReservations.getColumnModel().getColumn(1).setMaxWidth(0);
        tblReservations.setRowHeight(25);
        add(new JScrollPane(tblReservations), BorderLayout.CENTER);

        // Filter & action buttons
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT,10,10));
        cmbFilter = new JComboBox<>();
        populateComboBoxWithWeeks();
        btnFilter   = createButton("Filter",   new Color(70,130,180));
        btnReset    = createButton("Reset",    new Color(255,140,0));
        btnAllocate = createButton("Allocate", new Color(46,204,113));
        btnDelete   = createButton("Delete",   new Color(220,20,60));
        top.add(new JLabel("Filter by Week:"));
        top.add(cmbFilter);
        top.add(btnFilter);
        top.add(btnReset);
        top.add(btnAllocate);
        top.add(btnDelete);
        add(top, BorderLayout.SOUTH);

        // Status label
        lblStatus = new JLabel(" ");
        add(lblStatus, BorderLayout.SOUTH);

        // Listeners
        btnFilter.addActionListener(e -> filterReservations());
        btnReset.addActionListener(e -> loadAllReservations());
        btnAllocate.addActionListener(e -> allocateTable());
        btnDelete.addActionListener(e -> deleteReservation());

        // initial load
        loadAllReservations();
    }

    private JButton createButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(140,30));
        return b;
    }

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    }

    private void loadAllReservations() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT r.id AS res_id,
                   u.user_id AS cust_id,
                   u.username AS name,
                   u.phone_number AS contact,
                   r.reservation_date,
                   r.reservation_time,
                   r.guest_count,
                   cr.request_detail AS requests,
                   t.table_number
              FROM reservations r
              JOIN users u ON r.customer_id = u.user_id
         LEFT JOIN customer_requests cr ON cr.customer_id = u.user_id
         LEFT JOIN tables t ON r.table_id = t.table_id
             ORDER BY r.reservation_date, r.reservation_time
            """;
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(sql);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("res_id"),
                    rs.getInt("cust_id"),
                    rs.getString("name"),
                    rs.getString("contact"),
                    rs.getDate("reservation_date").toString(),
                    rs.getString("reservation_time"),
                    rs.getInt("guest_count"),
                    rs.getString("requests"),
                    rs.getString("table_number")
                });
            }
            lblStatus.setText("Loaded all reservations.");
        } catch (SQLException ex) {
            showError("Load failed: "+ex.getMessage());
        }
    }

    private void filterReservations() {
        String range = (String)cmbFilter.getSelectedItem();
        if (range==null||!range.contains("To")) return;
        String[] parts = range.split("To");
        String start = parts[0].trim().replace('.', '-') ;
        String end   = parts[1].trim().replace('.', '-');
        tableModel.setRowCount(0);

        String sql = """
            SELECT r.id AS res_id,
                   u.user_id AS cust_id,
                   u.username AS name,
                   u.phone_number AS contact,
                   r.reservation_date,
                   r.reservation_time,
                   r.guest_count,
                   cr.request_detail AS requests,
                   t.table_number
              FROM reservations r
              JOIN users u ON r.customer_id = u.user_id
         LEFT JOIN customer_requests cr ON cr.customer_id = u.user_id
         LEFT JOIN tables t ON r.table_id = t.table_id
             WHERE r.reservation_date BETWEEN ? AND ?
             ORDER BY r.reservation_date, r.reservation_time
            """;
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setDate(1, Date.valueOf(start));
            p.setDate(2, Date.valueOf(end));
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("res_id"),
                        rs.getInt("cust_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getDate("reservation_date").toString(),
                        rs.getString("reservation_time"),
                        rs.getInt("guest_count"),
                        rs.getString("requests"),
                        rs.getString("table_number")
                    });
                }
            }
            lblStatus.setText("Filtered: "+range);
        } catch (SQLException ex) {
            showError("Filter failed: "+ex.getMessage());
        }
    }

    private void allocateTable() {
        int r = tblReservations.getSelectedRow();
        if (r<0) { JOptionPane.showMessageDialog(this,"Select a reservation."); return; }
        int resId = (int)tableModel.getValueAt(r,0);
        String newTable = JOptionPane.showInputDialog(this,"Enter table number:");
        if (newTable==null||newTable.isBlank()) return;

        String sql = "UPDATE reservations r JOIN tables t ON t.table_number=? SET r.table_id=t.table_id WHERE r.id=?";
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1,newTable);
            p.setInt(2,resId);
            int updated = p.executeUpdate();
            if (updated>0) {
                loadAllReservations();
                JOptionPane.showMessageDialog(this,"Table allocated.");
            } else {
                showError("Allocation failed—check table number.");
            }
        } catch (SQLException ex) {
            showError("Allocate failed: "+ex.getMessage());
        }
    }

    private void deleteReservation() {
        int r = tblReservations.getSelectedRow();
        if (r<0) { JOptionPane.showMessageDialog(this,"Select a reservation."); return; }
        int resId = (int)tableModel.getValueAt(r,0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete reservation?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm!=JOptionPane.YES_OPTION) return;

        String sql = "DELETE FROM reservations WHERE id=?";
        try (Connection c = getConn();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1,resId);
            p.executeUpdate();
            loadAllReservations();
        } catch (SQLException ex) {
            showError("Delete failed: "+ex.getMessage());
        }
    }

    private void populateComboBoxWithWeeks() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        LocalDate end   = start.plusMonths(1).minusDays(1);
        cmbFilter.removeAllItems();
        while (!start.isAfter(end)) {
            LocalDate wEnd = start.plusDays(6);
            if (wEnd.isAfter(end)) wEnd = end;
            cmbFilter.addItem(fmt.format(start)+" To "+fmt.format(wEnd));
            start = start.plusWeeks(1);
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
