/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;
import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;


/**
 *
 * @author Admin
 */
public class DashboardPanel extends JPanel {
    private static final int WIDTH = 656;
    private static final int HEIGHT = 721;
    private JLabel clockLabel;

    public DashboardPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createMiddlePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        initClock();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 153, 255));
        topPanel.setPreferredSize(new Dimension(WIDTH, 100));

        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon("sample_logo.png"));
        logoLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to Our Restaurant!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        welcomeLabel.setForeground(Color.WHITE);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        return topPanel;
    }

    private JPanel createMiddlePanel() {
        JPanel middlePanel = new JPanel(new GridLayout(2, 2, 15, 15));
        middlePanel.setBackground(new Color(240, 240, 240));
        middlePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        middlePanel.add(createDishPanel("C:\\Users\\Admin\\OneDrive\\Documents\\NetBeansProjects\\Interface2\\src\\panels\\pictures\\pic1.jpeg", "Spaghetti Bolognese", "Only R119.99!"));
        middlePanel.add(createDishPanel("C:\\Users\\Admin\\OneDrive\\Documents\\NetBeansProjects\\Interface2\\src\\panels\\pictures\\pic2.jpeg", "Grilled Salmon", "Special: R145.00!"));
middlePanel.add(createDishPanel("C:\\Users\\Admin\\OneDrive\\Documents\\NetBeansProjects\\Interface2\\src\\panels\\pictures\\pic3.jpeg", "Caesar Salad", "Healthy Choice: R89.00"));
middlePanel.add(createDishPanel("C:\\Users\\Admin\\OneDrive\\Documents\\NetBeansProjects\\Interface2\\src\\panels\\pictures\\pic4.jpeg", "Chocolate Cake", "Dessert Deal: R65.00"));
        return middlePanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Book a Table Button
        JButton bookTableButton = new JButton("Book a Table");
        bookTableButton.setFont(new Font("Arial", Font.BOLD, 16));
        bookTableButton.setForeground(Color.WHITE);
        bookTableButton.setBackground(new Color(0, 153, 76));
        bookTableButton.setFocusPainted(false);
        bookTableButton.setPreferredSize(new Dimension(160, 40));

        bookTableButton.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Go To Make Reservation", "Book a Table", JOptionPane.INFORMATION_MESSAGE)
        );

        // Promotions Area
        JPanel promoPanel = new JPanel();
        promoPanel.setLayout(new BoxLayout(promoPanel, BoxLayout.Y_AXIS));
        promoPanel.setBackground(Color.WHITE);

        JLabel promoHeader = new JLabel("Today's Promotions:");
        promoHeader.setFont(new Font("SansSerif", Font.BOLD, 18));
        promoHeader.setForeground(new Color(255, 87, 51));
        promoHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        promoPanel.add(promoHeader);

        // Load promotions from database
        ArrayList<String> promotions = fetchPromotionsFromDB();
        if (promotions.isEmpty()) {
            promotions.add("Buy 1 Get 1 Free on all appetizers!");
            promotions.add("Happy Hour: 5 PM - 7 PM (50% off drinks)");
            promotions.add("Free dessert with any entrée!");
        }

        for (String promo : promotions) {
            JLabel label = new JLabel("• " + promo);
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            promoPanel.add(label);
        }

        bottomPanel.add(promoPanel, BorderLayout.CENTER);
        bottomPanel.add(bookTableButton, BorderLayout.EAST);
        return bottomPanel;
    }

    private JPanel createDishPanel(String imagePath, String dishName, String promotion) {
        JPanel dishPanel = new JPanel(new BorderLayout());
        dishPanel.setBackground(Color.WHITE);
        dishPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Load and scale image
        ImageIcon rawIcon = new ImageIcon(imagePath);
        Image scaledImg = rawIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
        JLabel dishImageLabel = new JLabel(new ImageIcon(scaledImg));
        dishImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel dishNameLabel = new JLabel(dishName, SwingConstants.CENTER);
        dishNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dishNameLabel.setForeground(new Color(51, 51, 51));

        JLabel promotionLabel = new JLabel(promotion, SwingConstants.CENTER);
        promotionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        promotionLabel.setForeground(new Color(102, 102, 102));

        dishPanel.add(dishNameLabel, BorderLayout.NORTH);
        dishPanel.add(dishImageLabel, BorderLayout.CENTER);
        dishPanel.add(promotionLabel, BorderLayout.SOUTH);
        return dishPanel;
    }

    private void initClock() {
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        clockLabel.setForeground(Color.DARK_GRAY);
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Timer to update the clock every second
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm:ss");
            clockLabel.setText(sdf.format(new Date()));
        });
        timer.start();

        // Add the clock label to top panel after the panel has been fully created
        JPanel topPanel = (JPanel) getComponent(0); // Get the first component (topPanel)
        topPanel.add(clockLabel, BorderLayout.EAST);
    }

    // Fetch promotions from MySQL database
    private ArrayList<String> fetchPromotionsFromDB() {
        ArrayList<String> promos = new ArrayList<>();
        try {
            // Update connection parameters accordingly
            String url = "jdbc:mysql://localhost:3306/registration";
            String user = "root";
            String password = "@Chante2004";

            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT details FROM promotions");

            while (rs.next()) {
                promos.add(rs.getString("details"));
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("Failed to fetch promotions: " + e.getMessage());
        }
        return promos;
    }
    
}
