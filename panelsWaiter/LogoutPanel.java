/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelsWaiter;
import panels.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
/**
 *
 * @author Admin
 */
public class LogoutPanel extends JPanel{
    
    public LogoutPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setPreferredSize(new Dimension(800, 800));
        // Title Label
        JLabel titleLabel = new JLabel("Logout Confirmation", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));
        add(titleLabel, BorderLayout.NORTH);

        // Logout message
        JLabel logoutLabel = new JLabel("Are you sure you want to logout?", SwingConstants.CENTER);
        logoutLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutLabel.setForeground(new Color(80, 80, 80));
        add(logoutLabel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 53, 69)); // Bootstrap red color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(120, 40));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> confirmLogout());

        // Cancel Button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(new Color(108, 117, 125)); // Bootstrap gray color
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> cancelLogout());

        buttonPanel.add(logoutButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void confirmLogout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            // Perform logout action
            JOptionPane.showMessageDialog(this,
                    "You have been logged out successfully.",
                    "Logout Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Redirect to the login page or exit application
            System.exit(0); // Example: Exit the application
        }
    }

    private void cancelLogout() {
        // Logic to handle the cancellation of logout
        JOptionPane.showMessageDialog(this,
                "Logout cancelled.",
                "Cancellation",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Testing the LogoutPanel
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Logout Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.add(new LogoutPanel());
            frame.setVisible(true);
        });
    }
    
}
