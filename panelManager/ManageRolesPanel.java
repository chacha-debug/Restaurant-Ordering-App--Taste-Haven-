/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package panelManager;

import javax.swing.JPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
/**
 *
 * @author Admin
 */
public class ManageRolesPanel extends JPanel{
    
    private JTable tblUsers;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JComboBox<String> comboDepartments;
    private JComboBox<String> comboRoles;
    private JTextField txtName;
    private JTextField txtSurname;
    private Map<String, String[]> departmentRolesMap;

    public ManageRolesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 600));

        // Title
        JLabel titleLabel = new JLabel("Manage Staff Roles", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(40, 40, 40));
        add(titleLabel, BorderLayout.NORTH);

        // Center layout
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);

        // Table
        tableModel = new DefaultTableModel(new String[]{"Staff ID", "Staff Code", "Department", "Name", "Surname", "Role"}, 0);
        tblUsers = new JTable(tableModel);
        tblUsers.setRowHeight(25);
        tblUsers.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        rowSorter = new TableRowSorter<>(tableModel);
        tblUsers.setRowSorter(rowSorter);

        JScrollPane tableScrollPane = new JScrollPane(tblUsers);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);

        txtName = new JTextField();
        txtSurname = new JTextField();

        comboDepartments = new JComboBox<>(new String[]{"Choose A Department", "Kitchen Department", "Service Department", "Bar Department",
                "Cashier & Reception Department", "Administrative Department", "Human Resources Department",
                "Marketing & Promotions Department", "Purchasing & Inventory Department",
                "Cleaning and Maintenance Department", "Security Department"});

        comboRoles = new JComboBox<>(new String[]{"Choose a Role"});

        formPanel.add(new JLabel("First Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(txtSurname);
        formPanel.add(new JLabel("Department:"));
        formPanel.add(comboDepartments);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(comboRoles);

        centerPanel.add(formPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAssign = createButton("Assign Role", new Color(70, 130, 180));
        JButton btnDelete = createButton("Delete Role", new Color(220, 20, 60));
        JButton btnSearch = createButton("Search", new Color(60, 179, 113));

        buttonPanel.add(btnAssign);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize department-roles mapping
        initializeDepartmentRoles();

        // Populate roles on department selection
        comboDepartments.addActionListener(e -> populateRoles());

        // Button Actions
        btnAssign.addActionListener(e -> assignRole());
        btnDelete.addActionListener(e -> deleteSelectedRole());
        btnSearch.addActionListener(e -> searchByName());
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void populateRoles() {
        String selectedDepartment = (String) comboDepartments.getSelectedItem();
        comboRoles.removeAllItems();

        if (departmentRolesMap.containsKey(selectedDepartment)) {
            for (String role : departmentRolesMap.get(selectedDepartment)) {
                comboRoles.addItem(role);
            }
        } else {
            comboRoles.addItem("No roles available");
        }
    }

    private void initializeDepartmentRoles() {
        departmentRolesMap = new HashMap<>();
        departmentRolesMap.put("Kitchen Department", new String[]{"Executive Chef", "Sous Chef", "Line Cook", "Pastry Chef", "Kitchen Porter", "Prep Cook", "Dishwasher"});
        departmentRolesMap.put("Service Department", new String[]{"Waiter", "Waitress", "Food Runner", "Host", "Hostess"});
        departmentRolesMap.put("Bar Department", new String[]{"Bartender", "Barback", "Mixologist"});
        departmentRolesMap.put("Cashier & Reception Department", new String[]{"Cashier", "Reservation Desk Attendant", "Receptionist"});
        departmentRolesMap.put("Administrative Department", new String[]{"Restaurant Manager", "Assistant Manager", "Shift Supervisor", "Operations Manager", "General Manager"});
        departmentRolesMap.put("Human Resources Department", new String[]{"HR Manager", "Recruiter", "Training Coordinator", "Payroll Officer"});
        departmentRolesMap.put("Marketing & Promotions Department", new String[]{"Marketing Manager", "Social Media Manager", "Content Creator", "Graphic Designer", "Promoter"});
        departmentRolesMap.put("Purchasing & Inventory Department", new String[]{"Inventory Manager", "Stock Controller", "Purchasing Officer", "Supplier Liaison"});
        departmentRolesMap.put("Cleaning and Maintenance Department", new String[]{"Janitor", "Cleaner", "Maintenance Technician", "Sanitation Worker"});
        departmentRolesMap.put("Security Department", new String[]{"Security Guard", "Surveillance Operator", "Door Supervisor"});
    }

    // ==== Button Action Methods ====

    private void assignRole() {
        String name = txtName.getText().trim();
        String surname = txtSurname.getText().trim();
        String department = (String) comboDepartments.getSelectedItem();
        String role = (String) comboRoles.getSelectedItem();

        if (name.isEmpty() || surname.isEmpty() || department.equals("Choose A Department") || role == null || role.equals("Choose a Role")) {
            JOptionPane.showMessageDialog(this, "Please fill all fields correctly.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // You can generate Staff ID or Code here
        String staffID = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String staffCode = "RB" + new Random().nextInt(9999);

        tableModel.addRow(new Object[]{staffID, staffCode, department, name, surname, role});
        clearForm();
    }

    private void deleteSelectedRole() {
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this role?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeRow(tblUsers.convertRowIndexToModel(selectedRow));
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void searchByName() {
        String name = txtName.getText().trim();
        if (name.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + name, 3)); // Column 3 = Name
        }
    }

    private void clearForm() {
        txtName.setText("");
        txtSurname.setText("");
        comboDepartments.setSelectedIndex(0);
        comboRoles.removeAllItems();
        comboRoles.addItem("Choose a Role");
    }
}
