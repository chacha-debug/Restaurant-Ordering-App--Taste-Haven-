/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelManager;
import panels.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
/**
 *
 * @author Admin
 */
public class SettingsPanel extends JPanel{
    private JCheckBox notificationsCheckBox;
    private JCheckBox darkModeCheckBox;
    private JComboBox<String> languageComboBox;
    private JCheckBox soundCheckBox;
    private JTextArea issueTextArea; // Text area for reporting issues
    
    // Database credentials (update with your own credentials)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/your_database";  // Update with your database URL
    private static final String DB_USERNAME = "root";  // Update with your database username
    private static final String DB_PASSWORD = "password";  // Update with your database password

    public SettingsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 30, 20, 30));
        setPreferredSize(new Dimension(800, 800));
        // Title
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(33, 37, 41));
        add(titleLabel, BorderLayout.NORTH);

        // Center Panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBackground(Color.WHITE);
        settingsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Application Preferences",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(100, 100, 100)
        ));

        // Notifications
        notificationsCheckBox = new JCheckBox("Enable Notifications");
        styleCheckBox(notificationsCheckBox, "Get alerts for updates and activity.");

        // Dark Mode
        darkModeCheckBox = new JCheckBox("Enable Dark Mode");
        styleCheckBox(darkModeCheckBox, "Switch to a dark-themed interface.");

        // Sound
        soundCheckBox = new JCheckBox("Enable Sound Effects");
        styleCheckBox(soundCheckBox, "Turn on sound cues for app actions.");

        // Language Selection
        JPanel languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        languagePanel.setBackground(Color.WHITE);
        JLabel languageLabel = new JLabel("Language: ");
        languageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        languageComboBox = new JComboBox<>(new String[]{"English", "Spanish", "French", "German"});
        languageComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        languageComboBox.setPreferredSize(new Dimension(150, 30));
        languageComboBox.setToolTipText("Choose your preferred language");
        languagePanel.add(languageLabel);
        languagePanel.add(languageComboBox);

        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(notificationsCheckBox);
        settingsPanel.add(darkModeCheckBox);
        settingsPanel.add(soundCheckBox);
        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(languagePanel);

        // Issue Reporting Section
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BoxLayout(reportPanel, BoxLayout.Y_AXIS));
        reportPanel.setBackground(Color.WHITE);
        reportPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "Report an Issue",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(100, 100, 100)
        ));

        JLabel issueLabel = new JLabel("Describe the issue:");
        issueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        issueTextArea = new JTextArea(5, 20);
        issueTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        issueTextArea.setLineWrap(true);
        issueTextArea.setWrapStyleWord(true);
        issueTextArea.setToolTipText("Describe the issue you're facing");
        JScrollPane scrollPane = new JScrollPane(issueTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));

        JButton reportButton = new JButton("Report Issue");
        reportButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        reportButton.setBackground(new Color(220, 53, 69)); // Red color for report
        reportButton.setForeground(Color.WHITE);
        reportButton.setPreferredSize(new Dimension(180, 40));
        reportButton.setFocusPainted(false);
        reportButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        reportButton.setToolTipText("Click to report the issue");
        reportButton.addActionListener(this::reportIssue);

        reportPanel.add(issueLabel);
        reportPanel.add(scrollPane);
        reportPanel.add(reportButton);

        settingsPanel.add(Box.createVerticalStrut(10));
        settingsPanel.add(reportPanel);

        add(settingsPanel, BorderLayout.CENTER);

        // Save Button
        JButton saveButton = new JButton("Save Settings");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveButton.setBackground(new Color(25, 135, 84));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(180, 40));
        saveButton.setFocusPainted(false);
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveButton.setToolTipText("Click to apply your settings");
        saveButton.addActionListener(e -> saveSettings());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleCheckBox(JCheckBox checkBox, String tooltip) {
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        checkBox.setBackground(Color.WHITE);
        checkBox.setToolTipText(tooltip);
        checkBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void saveSettings() {
        // Logic for saving settings (e.g., database or config file)
        JOptionPane.showMessageDialog(this,
                "Settings saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void reportIssue(ActionEvent e) {
        String issueDescription = issueTextArea.getText().trim();
        if (issueDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please describe the issue before submitting.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            // Connect to the database and insert the reported issue
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String sql = "INSERT INTO reported_issues (description) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, issueDescription);
                    stmt.executeUpdate();
                }

                // Notify the user that the issue was successfully reported
                JOptionPane.showMessageDialog(this,
                        "Thank you for reporting the issue. Our team will look into it.",
                        "Issue Reported",
                        JOptionPane.INFORMATION_MESSAGE);

                // Clear the text area after submission
                issueTextArea.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "An error occurred while reporting the issue. Please try again later.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
    
}
