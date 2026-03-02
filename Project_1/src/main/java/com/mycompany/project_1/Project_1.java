package com.mycompany.project_1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

class Donor implements Serializable {
    private static final long serialVersionUID = 1L;
    String name, bloodGroup, contact;

    Donor(String name, String bloodGroup, String contact) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.contact = contact;
    }
}

public class Project_1 {
    private ArrayList<Donor> donorList = new ArrayList<>();
    private JFrame frame;
    private JTextField tfName, tfContact, tfSearch;
    private JComboBox<String> cbBloodGroup;
    private JTable donorTable;
    private DefaultTableModel tableModel;
    private final String DATA_FILE = "donors.dat";

    public Project_1() {
        loadData();
        
        frame = new JFrame("Blood Bank Management System");
        frame.setSize(750, 500);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top Panel Title
        JLabel title = new JLabel("Blood Bank Management System", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.RED);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(title, BorderLayout.NORTH);

        // Form Panel (Left)
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(8, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setBackground(new Color(255, 240, 240));

        tfName = new JTextField();
        tfContact = new JTextField();
        cbBloodGroup = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        tfSearch = new JTextField();

        JButton btnAdd = new JButton("Add Donor");
        JButton btnSearch = new JButton("Search by Group");
        JButton btnDelete = new JButton("Delete Selected");
        JButton btnShowAll = new JButton("Show All");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(tfName);
        formPanel.add(new JLabel("Blood Group:"));
        formPanel.add(cbBloodGroup);
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(tfContact);
        formPanel.add(btnAdd);
        formPanel.add(btnSearch);

        frame.add(formPanel, BorderLayout.WEST);

        // Table Panel (Center)
        tableModel = new DefaultTableModel(new String[]{"Name", "Blood Group", "Contact"}, 0);
        donorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(donorTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(255, 240, 240));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.add(new JLabel("Search Blood Group:"));
        bottomPanel.add(tfSearch);
        bottomPanel.add(btnShowAll);
        bottomPanel.add(btnDelete);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Event Listeners
        btnAdd.addActionListener(e -> addDonor());
        btnSearch.addActionListener(e -> searchByBloodGroup());
        btnDelete.addActionListener(e -> deleteSelected());
        btnShowAll.addActionListener(e -> loadTable());

        // Sample data if no data loaded
        if (donorList.isEmpty()) {
            donorList.add(new Donor("John Doe", "A+", "9876543210"));
            donorList.add(new Donor("Jane Smith", "O-", "9123456789"));
        }
        loadTable();
        
        frame.setVisible(true);
    }

    private void addDonor() {
        String name = tfName.getText().trim();
        String contact = tfContact.getText().trim();
        String blood = cbBloodGroup.getSelectedItem().toString();

        if (name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter all details.");
            return;
        }

        donorList.add(new Donor(name, blood, contact));
        saveData();
        loadTable();
        tfName.setText("");
        tfContact.setText("");
    }

    private void searchByBloodGroup() {
        String bg = tfSearch.getText().trim().toUpperCase();
        if (bg.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter blood group to search.");
            return;
        }

        tableModel.setRowCount(0);
        for (Donor d : donorList) {
            if (d.bloodGroup.equalsIgnoreCase(bg)) {
                tableModel.addRow(new Object[]{d.name, d.bloodGroup, d.contact});
            }
        }
        
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(frame, "No donors found with blood group: " + bg);
        }
    }

    private void deleteSelected() {
        int row = donorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(frame, "Select a row to delete.");
            return;
        }
        String name = tableModel.getValueAt(row, 0).toString();
        donorList.removeIf(d -> d.name.equals(name));
        saveData();
        loadTable();
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        for (Donor d : donorList) {
            tableModel.addRow(new Object[]{d.name, d.bloodGroup, d.contact});
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                donorList = (ArrayList<Donor>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(donorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Project_1();
        });
    }
}