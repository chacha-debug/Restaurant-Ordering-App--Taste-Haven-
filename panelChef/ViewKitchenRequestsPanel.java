/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelChef;

import javax.swing.JPanel;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class ViewKitchenRequestsPanel extends JPanel{
    
    private DefaultListModel<String> listModel;
    private JList<String> messageList;
    private JButton refreshButton, markAsReadButton, sendMessageButton;
    private JTextField messageInputField;
    private ArrayList<Integer> messageIds;
    private boolean showOnlyUnread = false;

    public ViewKitchenRequestsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 800));

        JLabel titleLabel = new JLabel("Messages Between Chef and Waiter", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        messageList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(messageList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMessages());

        markAsReadButton = new JButton("Mark as Read");
        markAsReadButton.addActionListener(e -> markSelectedAsRead());

        sendMessageButton = new JButton("Send Message");
        sendMessageButton.addActionListener(e -> sendMessageToWaiter());

        buttonPanel.add(refreshButton);
        buttonPanel.add(markAsReadButton);
        buttonPanel.add(sendMessageButton);

        add(buttonPanel, BorderLayout.SOUTH);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBackground(Color.WHITE);
        JLabel inputLabel = new JLabel("Message to Waiter:");
        messageInputField = new JTextField();
        inputPanel.add(inputLabel, BorderLayout.WEST);
        inputPanel.add(messageInputField, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);

        messageIds = new ArrayList<>();
        loadMessages();
    }

    private void loadMessages() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                listModel.clear();
                messageIds.clear();

                String url = "jdbc:mysql://localhost:3306/registration";
                String user = "root";
                String password = "@Chante2004";

                String sql = "SELECT * FROM chef_waiter_messages " +
                             "WHERE sender_role = 'waiter' " +
                             (showOnlyUnread ? "AND status = 'new' " : "") +
                             "ORDER BY timestamp DESC";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    while (rs.next()) {
                        int id = rs.getInt("message_id");
                        String messageText = rs.getString("message_text");
                        Timestamp timestamp = rs.getTimestamp("timestamp");
                        String status = rs.getString("status");

                        String display = String.format(
                                "%s | %s\n→ %s",
                                sdf.format(timestamp),
                                status.toUpperCase(),
                                messageText
                        );

                        listModel.addElement(display);
                        messageIds.add(id);
                    }

                    if (listModel.isEmpty()) {
                        listModel.addElement("No messages found.");
                    }

                } catch (SQLException ex) {
                    listModel.addElement("Error loading messages: " + ex.getMessage());
                    ex.printStackTrace();
                }

                return null;
            }
        };
        worker.execute();
    }

    private void markSelectedAsRead() {
        int index = messageList.getSelectedIndex();
        if (index == -1 || index >= messageIds.size()) {
            JOptionPane.showMessageDialog(this, "Please select a valid message to mark as read.");
            return;
        }

        int messageId = messageIds.get(index);

        String url = "jdbc:mysql://localhost:3306/registration";
        String user = "root";
        String password = "@Chante2004";

        String updateSql = "UPDATE chef_waiter_messages SET status = 'read' WHERE message_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setInt(1, messageId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Message marked as read.");
            loadMessages();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update message:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendMessageToWaiter() {
        String message = messageInputField.getText().trim();

        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message to send.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/registration";
        String user = "root";
        String password = "@Chante2004";

        String insertSql = "INSERT INTO chef_waiter_messages (sender_role, message_text, status) VALUES ('chef', ?, 'new')";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setString(1, message);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Message sent to the waiter.");
            messageInputField.setText("");
            loadMessages();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to send message:\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
}
