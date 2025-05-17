/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panels;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class SubmitFeedbackPanel extends JPanel {

    private JTextArea feedbackTextArea;
    private JComboBox<String> ratingComboBox;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/registration";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@Chante2004";
    private int customerId;

    public SubmitFeedbackPanel() {
        this.customerId = SessionData.getInstance().getUserId();
   
        setPreferredSize(new Dimension(656, 721));
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // Title Label
        JLabel titleLabel = new JLabel("Submit Feedback");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Feedback Input Panel
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.setOpaque(false);
        add(feedbackPanel, BorderLayout.CENTER);

        // Feedback Text Area
        feedbackTextArea = new JTextArea(8, 30);
        feedbackTextArea.setBorder(BorderFactory.createTitledBorder("Your Feedback"));
        feedbackTextArea.setLineWrap(true);
        feedbackTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackTextArea);
        feedbackPanel.add(scrollPane);
        feedbackPanel.add(Box.createVerticalStrut(15));

        // Rating Section
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ratingPanel.setOpaque(false);
        JLabel ratingLabel = new JLabel("Rate Us:");
        ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ratingComboBox = new JComboBox<>(new String[]{"⭐️ 1", "⭐️⭐️ 2", "⭐️⭐️⭐️ 3", "⭐️⭐️⭐️⭐️ 4", "⭐️⭐️⭐️⭐️⭐️ 5"});
        ratingPanel.add(ratingLabel);
        ratingPanel.add(ratingComboBox);
        feedbackPanel.add(ratingPanel);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        add(buttonsPanel, BorderLayout.SOUTH);

        JButton submitButton = new JButton("Submit Feedback");
       submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(new Color(0, 123, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(180, 40));
        submitButton.addActionListener(e -> submitFeedback());
        buttonsPanel.add(submitButton);

        JButton viewButton = new JButton("View Past Feedback");
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewButton.setBackground(new Color(108, 117, 125));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setPreferredSize(new Dimension(180, 40));
        viewButton.addActionListener(e -> viewPastFeedback());
        buttonsPanel.add(viewButton);
    }

    private void submitFeedback() {
          // Replace with actual session retrieval
        String feedbackText = feedbackTextArea.getText().trim();
        int rating = ratingComboBox.getSelectedIndex() + 1;

        if (feedbackText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your feedback before submitting.",
                    "Empty Feedback", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO feedback (customer_id, feedback_text, rating) VALUES (?, ?, ?)")) {

            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, feedbackText);
            preparedStatement.setInt(3, rating);
            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Feedback submitted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            feedbackTextArea.setText("");
            ratingComboBox.setSelectedIndex(2); // Default to 3 stars

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving feedback: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewPastFeedback() {
         // Replace with actual session retrieval
        List<String[]> feedbackList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT feedback_text, rating, submitted_at FROM feedback WHERE customer_id = ? ORDER BY submitted_at DESC")) {

            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String feedbackText = resultSet.getString("feedback_text");
                int rating = resultSet.getInt("rating");
                String submittedAt = resultSet.getTimestamp("submitted_at").toString();
                feedbackList.add(new String[]{feedbackText, rating + " Stars", submittedAt});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching feedback: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Display feedback in a dialog
        JDialog feedbackDialog = new JDialog((Frame) null, "Past Feedback", true);
        feedbackDialog.setSize(800, 500);
        feedbackDialog.setLocationRelativeTo(this);
        feedbackDialog.setLayout(new BorderLayout(10, 10));

        JLabel dialogTitle = new JLabel("Past Feedback");
        dialogTitle.setFont(new Font("Arial", Font.BOLD, 16));
        dialogTitle.setHorizontalAlignment(SwingConstants.CENTER);
        feedbackDialog.add(dialogTitle, BorderLayout.NORTH);

        String[] columns = {"Feedback", "Rating", "Submitted At"};
        String[][] data = feedbackList.toArray(new String[0][0]);
        JTable feedbackTable = new JTable(data, columns);
        feedbackTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        feedbackTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        feedbackDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> feedbackDialog.dispose());
        feedbackDialog.add(closeButton, BorderLayout.SOUTH);

        feedbackDialog.setVisible(true);
    }
}
