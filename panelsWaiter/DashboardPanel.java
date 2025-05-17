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
import java.sql.*;


/**
 *
 * @author Admin
 */
public class DashboardPanel extends JPanel{
    
    public DashboardPanel() {
        setPreferredSize(new Dimension(699, 769));
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(800, 800));

        // Title
        JLabel titleLabel = new JLabel("Waiter Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(44, 62, 80));
        add(titleLabel, BorderLayout.NORTH);

        // Summary Cards Panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryPanel.setBackground(Color.WHITE);

        summaryPanel.add(createSummaryCard("Active Orders", getActiveOrdersCount(), new Color(41, 128, 185)));
        summaryPanel.add(createSummaryCard("Pending Requests", getPendingRequestsCount(), new Color(243, 156, 18)));
        summaryPanel.add(createSummaryCard("Tables Assigned", getTablesAssignedCount(), new Color(39, 174, 96)));

        // Button Grid Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        buttonPanel.add(createDashboardButton("Take New Order"));
        buttonPanel.add(createDashboardButton("View Active Orders"));
        buttonPanel.add(createDashboardButton("View Assigned Tables"));
        buttonPanel.add(createDashboardButton("Kitchen Messages"));

        // Arrange in a vertical box
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(summaryPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(buttonPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createSummaryCard(String title, String count, Color bgColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setPreferredSize(new Dimension(200, 100)); // Adjusted size to fit the panel properly
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        JLabel countLabel = new JLabel(count, SwingConstants.RIGHT);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        countLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.SOUTH);
        return card;
    }

    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50)); // Ensures buttons fit in the grid layout
        return button;
    }

    // Database methods to fetch counts
    private String getActiveOrdersCount() {
        return getCountFromDatabase("SELECT COUNT(*) FROM orders WHERE status NOT IN ('Pending')");
    }

    private String getPendingRequestsCount() {
        return getCountFromDatabase("SELECT COUNT(*) FROM requests WHERE status = 'Pending'");
    }

    private String getTablesAssignedCount() {
        return getCountFromDatabase("SELECT COUNT(DISTINCT table_number) FROM orders WHERE status IN ('Pending')");
    }

    private String getCountFromDatabase(String query) {
        String dbUrl = "jdbc:mysql://localhost:3306/registration";
        String dbUser = "root";
        String dbPassword = "@Chante2004";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error fetching data:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return "0"; // Default to 0 if there's an error
    }
    
}
