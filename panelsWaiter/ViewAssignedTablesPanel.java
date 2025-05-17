/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelsWaiter;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 *
 * @author Admin
 */
public class ViewAssignedTablesPanel extends JPanel {

    private JLabel assignedTableLabel;
    private JLabel summaryLabel;
    private JButton refreshButton;

    public ViewAssignedTablesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 240));  // Light grey background for a modern feel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(800, 800));
        // Header Panel with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(39, 174, 96));  // Green header background
        JLabel headerLabel = new JLabel("Assigned Tables", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Assigned Tables Label (for table list display)
        assignedTableLabel = new JLabel("Loading assigned tables...", SwingConstants.CENTER);
        assignedTableLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        assignedTableLabel.setForeground(Color.BLACK);
        assignedTableLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        assignedTableLabel.setVerticalAlignment(SwingConstants.TOP);  // Align text to the top for better readability
        assignedTableLabel.setPreferredSize(new Dimension(400, 200));
        JScrollPane tableScrollPane = new JScrollPane(assignedTableLabel);  // Make the label scrollable if needed
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));  // Border around table content
        tableScrollPane.setPreferredSize(new Dimension(400, 200));

        add(tableScrollPane, BorderLayout.CENTER);

        // Summary Label
        summaryLabel = new JLabel("Summary: Loading...", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        summaryLabel.setForeground(new Color(100, 100, 100));  // Lighter color for summary
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(summaryLabel, BorderLayout.SOUTH);

        // Refresh Button (modern style with hover effect)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 240));  // Same background as the panel
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(39, 174, 96));  // Green button color
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect to the button
        refreshButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(34, 139, 34));  // Darker green when hovered
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                refreshButton.setBackground(new Color(39, 174, 96));  // Original green when not hovered
            }
        });

        // Add action listener for refresh button
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAssignedTables(); // Reload tables on click
            }
        });

        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Initial load of assigned tables
        loadAssignedTables();
    }

    private void loadAssignedTables() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                int currentWaiterId = panels.SessionData.getInstance().getUserId(); // Ensure userId is valid

                if (currentWaiterId <= 0) {
                    assignedTableLabel.setText("<html><b>Error:</b> Invalid waiter ID. Please log in again.</html>");
                    return null;
                }

                String url = "jdbc:mysql://localhost:3306/registration";
                String user = "root";
                String password = "@Chante2004";

                String selectSql = "SELECT table_number, capacity FROM tables WHERE waiter_id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password)) {
                    assignRandomWaitersToUnassignedTables(conn); // Assign tables to waiters if needed

                    try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                        stmt.setInt(1, currentWaiterId);
                        ResultSet rs = stmt.executeQuery();

                        StringBuilder tableList = new StringBuilder("<html><b>Your Assigned Tables:</b><br><br>");
                        int totalTables = 0;
                        int totalCapacity = 0;

                        while (rs.next()) {
                            totalTables++;
                            int tableNum = rs.getInt("table_number");
                            int capacity = rs.getInt("capacity");
                            totalCapacity += capacity;

                            tableList.append("Table ").append(tableNum)
                                    .append(" (Capacity: ").append(capacity).append(")<br>");
                        }

                        if (totalTables == 0) {
                            tableList.append("No tables assigned yet.");
                        }

                        tableList.append("</html>");
                        assignedTableLabel.setText(tableList.toString());

                        summaryLabel.setText(String.format("Summary: %d Tables Assigned | Total Capacity: %d", totalTables, totalCapacity));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    assignedTableLabel.setText("<html><b>Error:</b> Could not fetch assigned tables.<br>"
                            + ex.getMessage() + "</html>");
                    summaryLabel.setText("Summary: Error fetching data.");
                }

                return null;
            }
        }.execute();
    }

    private void assignRandomWaitersToUnassignedTables(Connection conn) throws SQLException {
        String getWaitersSql = "SELECT user_id FROM users WHERE role = 'waiter'";
        String getUnassignedTablesSql = "SELECT table_id FROM tables WHERE waiter_id IS NULL";
        String updateTableSql = "UPDATE tables SET waiter_id = ? WHERE table_id = ?";

        List<Integer> waiterIds = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(getWaitersSql)) {
            while (rs.next()) {
                waiterIds.add(rs.getInt("user_id"));
            }
        }

        if (waiterIds.isEmpty()) {
            System.err.println("No waiters found in the database.");
            return;
        }

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(getUnassignedTablesSql)) {
            while (rs.next()) {
                int tableId = rs.getInt("table_id");
                int randomWaiterId = waiterIds.get(new Random().nextInt(waiterIds.size()));

                try (PreparedStatement updateStmt = conn.prepareStatement(updateTableSql)) {
                    updateStmt.setInt(1, randomWaiterId);
                    updateStmt.setInt(2, tableId);
                    updateStmt.executeUpdate();
                }
            }
        }
    }

}
