/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.time.LocalDate;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
/**
 *
 * @author Admin
 */
public class TrackSalesPanel extends JPanel{
    
    private JTable tblSales;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbCategory, cmbFilter;
    private JPanel chartPanelPlaceholder;

    // === JDBC CONFIG ===
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/your_database";
    private static final String DB_USER     = "your_username";
    private static final String DB_PASSWORD = "your_password";

    public TrackSalesPanel() {
        setLayout(new BorderLayout(15,15));
        setBorder(BorderFactory.createEmptyBorder(15,20,15,20));
        setBackground(Color.WHITE);

        // Title
        JLabel lblTitle = new JLabel("Track Sales", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(lblTitle, BorderLayout.NORTH);

        // Filters, Table, Chart
        JPanel content = new JPanel(new BorderLayout(10,10));
        content.setBackground(Color.WHITE);
        content.add(createFilterPanel(), BorderLayout.NORTH);
        content.add(createTablePanel(),  BorderLayout.CENTER);
        content.add(createChartPanel(),  BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        p.setBackground(Color.WHITE);

        cmbCategory = new JComboBox<>();
        cmbCategory.addItem("All Categories");
        // load real categories from menu_items
        try (Connection c = getConn();
             PreparedStatement ps = c.prepareStatement("SELECT DISTINCT category FROM menu_items");
             ResultSet rs = ps.executeQuery()) {
            while(rs.next()) cmbCategory.addItem(rs.getString(1));
        } catch(Exception e) {
            showError("Failed to load categories: "+e.getMessage());
        }

        cmbFilter = new JComboBox<>();
        populateDateRanges();

        JButton btnApply = new JButton("Apply Filter");
        btnApply.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnApply.setBackground(new Color(60,120,180));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        btnApply.addActionListener(e -> loadSalesData());

        p.add(new JLabel("Category:"));
        p.add(cmbCategory);
        p.add(new JLabel("Date Range:"));
        p.add(cmbFilter);
        p.add(btnApply);
        return p;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new String[]{
            "Order #","Date","Time","Item","Category","Qty","Unit Price","Total","Waiter"
        },0);
        tblSales = new JTable(tableModel);
        tblSales.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblSales.setRowHeight(28);
        tblSales.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        return new JScrollPane(tblSales);
    }

    private JPanel createChartPanel() {
        chartPanelPlaceholder = new JPanel(new BorderLayout());
        chartPanelPlaceholder.setPreferredSize(new Dimension(950,300));
        chartPanelPlaceholder.setBackground(Color.WHITE);
        chartPanelPlaceholder.add(
            new JLabel("Sales chart will appear here", SwingConstants.CENTER),
            BorderLayout.CENTER
        );
        return chartPanelPlaceholder;
    }

    private Connection getConn() throws SQLException {
        return DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
    }

    private void populateDateRanges() {
        cmbFilter.removeAllItems();
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        for(int i=0;i<4;i++){
            LocalDate start = now.minusWeeks(i).with(java.time.DayOfWeek.MONDAY);
            LocalDate end   = start.plusDays(6);
            cmbFilter.addItem(fmt.format(start)+" To "+fmt.format(end));
        }
    }

    private void loadSalesData() {
    tableModel.setRowCount(0);
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    String categoryFilter = cmbCategory.getSelectedItem().toString();
    String dateRange      = cmbFilter.getSelectedItem().toString();
    String[] parts        = dateRange.split(" To ");
    String start          = parts[0].replace('.', '-') + " 00:00:00";
    String end            = parts[1].replace('.', '-') + " 23:59:59";

    String sql = """
        SELECT o.order_id,
               DATE(o.order_date) AS od,
               TIME(o.order_date) AS ot,
               mi.item_name,
               mi.category,
               odet.quantity,
               odet.price_at_time_of_order,
               (odet.quantity*odet.price_at_time_of_order) AS total,
               u.username AS waiter
          FROM orders o
          JOIN order_details odet ON o.order_id = odet.order_id
          JOIN menu_items mi ON odet.menu_item_id = mi.item_id
          JOIN users u ON o.assigned_table_id = u.user_id
         WHERE o.order_date BETWEEN ? AND ?
           AND (? = 'All Categories' OR mi.category = ?)
         ORDER BY o.order_date
        """;

    try (Connection c = getConn();
         PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, start);
        ps.setString(2, end);
        ps.setString(3, categoryFilter);
        ps.setString(4, categoryFilter);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // 1) Pull values
                int    orderId   = rs.getInt("order_id");
                java.sql.Date sqlDate = rs.getDate("od");
                String time      = rs.getTime("ot").toString();
                String itemName  = rs.getString("item_name");
                String category  = rs.getString("category");
                int    qty       = rs.getInt("quantity");
                BigDecimal unitPrice = rs.getBigDecimal("price_at_time_of_order");
                BigDecimal total     = rs.getBigDecimal("total");
                String waiter    = rs.getString("waiter");

                // 2) Map to a day name via Calendar
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(new java.util.Date(sqlDate.getTime()));
                int dow = cal.get(java.util.Calendar.DAY_OF_WEEK); 
                // Calendar.SUNDAY=1 ... SATURDAY=7
                String[] dayNames = {
                    "Sunday","Monday","Tuesday",
                    "Wednesday","Thursday","Friday","Saturday"
                };
                String dayName = dayNames[dow - 1];

                // 3) Add row to table
                tableModel.addRow(new Object[]{
                    orderId,
                    sqlDate.toString(),
                    time,
                    itemName,
                    category,
                    qty,
                    unitPrice,
                    total,
                    waiter
                });

                // 4) Accumulate into dataset
                dataset.incrementValue(total.doubleValue(), "Sales", dayName);
            }
        }

        // 5) Render chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Sales " + dateRange,
            "Day",
            "ZAR",
            dataset,
            PlotOrientation.VERTICAL,
            false, true, false
        );
        ChartPanel cp = new ChartPanel(chart);
        chartPanelPlaceholder.removeAll();
        chartPanelPlaceholder.add(cp, BorderLayout.CENTER);
        chartPanelPlaceholder.revalidate();
        chartPanelPlaceholder.repaint();

    } catch (Exception ex) {
        showError("Failed to load sales: " + ex.getMessage());
    }
}


    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
}
