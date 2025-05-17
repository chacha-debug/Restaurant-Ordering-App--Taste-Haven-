/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;
import db.ConnectionProvider;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Admin
 */
public class BrowseMenuPanel extends JPanel{
    
    private JPanel menuPanel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JSlider priceSlider;
    private Connection conn;
    private List<MenuItem> menuItems;

    public BrowseMenuPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(656, 721));
        setBackground(new Color(240, 240, 240)); // Light gray background

        // Initialize Database Connection
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/registration", "root", "@Chante2004");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // === Top Section: Filters ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(new Color(240, 240, 240));

        // Search Field
        searchField = new JTextField(20);
        searchField.setToolTipText("Search for dishes...");
        searchField.addActionListener(e -> filterMenu());
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);

        // Category Dropdown
        categoryFilter = new JComboBox<>(new String[]{"All", "Beverages", "Kids Meals", "Main Course - Non-Vegetarian","Main Course - Vegetarian" ,"Desserts"});
        categoryFilter.setToolTipText("Filter by category");
        categoryFilter.addActionListener(e -> filterMenu());
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilter);

        // Price Slider
        priceSlider = new JSlider(0, 200, 200);
        priceSlider.setMajorTickSpacing(50);
        priceSlider.setPaintTicks(true);
        priceSlider.setPaintLabels(true);
        priceSlider.setToolTipText("Filter by price");
        priceSlider.addChangeListener(e -> filterMenu());
        filterPanel.add(new JLabel("Max Price:"));
        filterPanel.add(priceSlider);

        add(filterPanel, BorderLayout.NORTH);

        // === Center Section: Menu Items ===
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(656, 600));
        add(scrollPane, BorderLayout.CENTER);

        // Load and display menu
        menuItems = new ArrayList<>();
        loadMenuItemsFromDatabase();
        displayMenuItems(menuItems);
    }

    private void loadMenuItemsFromDatabase() {
        String sql = "SELECT item_name, category, price, description, image_path FROM menu_items";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("item_name");
                String category = rs.getString("category");
                int price = rs.getInt("price");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");

                menuItems.add(new MenuItem(name, category, price, description, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayMenuItems(List<MenuItem> items) {
        menuPanel.removeAll();

        for (MenuItem item : items) {
            JPanel itemPanel = new JPanel(new BorderLayout());
            itemPanel.setPreferredSize(new Dimension(620, 140));
            itemPanel.setMaximumSize(new Dimension(620, 140));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            itemPanel.setBackground(Color.WHITE);

            // Resize Image
            ImageIcon rawIcon = new ImageIcon(item.getImageUrl());
            Image scaledImage = rawIcon.getImage().getScaledInstance(120, 100, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setPreferredSize(new Dimension(130, 100));
            imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            itemPanel.add(imageLabel, BorderLayout.WEST);

            // Details
            JPanel detailsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            detailsPanel.setBackground(new Color(245, 245, 245));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            detailsPanel.add(new JLabel("Name: " + item.getName()));
            detailsPanel.add(new JLabel("Description: " + item.getDescription()));
            detailsPanel.add(new JLabel("Price: R" + item.getPrice()));

            itemPanel.add(detailsPanel, BorderLayout.CENTER);

            menuPanel.add(Box.createVerticalStrut(10)); // spacing
            menuPanel.add(itemPanel);
        }

        menuPanel.revalidate();
        menuPanel.repaint();
    }

    private void filterMenu() {
        String search = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getSelectedItem().toString();
        int maxPrice = priceSlider.getValue();

        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : menuItems) {
            boolean matchesSearch = item.getName().toLowerCase().contains(search);
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equals(selectedCategory);
            boolean matchesPrice = item.getPrice() <= maxPrice;

            if (matchesSearch && matchesCategory && matchesPrice) {
                filtered.add(item);
            }
        }

        displayMenuItems(filtered);
    }

    // === MenuItem Class ===
    class MenuItem {
        private String name;
        private String category;
        private int price;
        private String description;
        private String imageUrl;

        public MenuItem(String name, String category, int price, String description, String imageUrl) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.description = description;
            this.imageUrl = imageUrl;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public int getPrice() { return price; }
        public String getDescription() { return description; }
        public String getImageUrl() { return imageUrl; }
    
    }
    
}
