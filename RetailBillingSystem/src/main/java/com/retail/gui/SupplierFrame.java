package com.retail.gui;

import com.retail.model.Supplier;
import com.retail.service.SupplierService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class SupplierFrame extends JFrame {
    private final SupplierService service = new SupplierService();
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Address"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    public SupplierFrame() {
        super("Supplier Master");
        setSize(760, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) fillForm();
        });
        refreshTable();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Phone:")); form.add(phoneField);
        form.add(new JLabel("Address:")); form.add(addressField);
        return form;
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel actions = new JPanel(new FlowLayout());
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(event -> addSupplier());
        update.addActionListener(event -> updateSupplier());
        delete.addActionListener(event -> deleteSupplier());
        clear.addActionListener(event -> clearForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);

        JPanel search = new JPanel(new BorderLayout(8, 8));
        search.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        search.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(event -> search());
        search.add(searchButton, BorderLayout.EAST);
        panel.add(actions, BorderLayout.NORTH);
        panel.add(search, BorderLayout.SOUTH);
        return panel;
    }

    private Supplier readForm() {
        Supplier supplier = new Supplier();
        supplier.setName(nameField.getText().trim());
        supplier.setPhone(phoneField.getText().trim());
        supplier.setAddress(addressField.getText().trim());
        return supplier;
    }

    private void addSupplier() {
        try {
            service.add(readForm());
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Supplier added.");
        } catch (IllegalArgumentException | SQLException exception) { showError(exception); }
    }

    private void updateSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a supplier to update."); return; }
        try {
            Supplier supplier = readForm();
            supplier.setSupplierId((Integer) tableModel.getValueAt(row, 0));
            if (service.update(supplier)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Supplier updated.");
            }
        } catch (IllegalArgumentException | SQLException exception) { showError(exception); }
    }

    private void deleteSupplier() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a supplier to delete."); return; }
        int choice = JOptionPane.showConfirmDialog(this, "Delete this supplier?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            if (service.delete(id)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Supplier deleted.");
            }
        } catch (SQLException exception) { showError(exception); }
    }

    private void search() {
        try {
            fillTable(service.search(searchField.getText()));
        } catch (IllegalArgumentException | SQLException exception) { showError(exception); }
    }

    private void refreshTable() {
        try {
            fillTable(service.getAll());
        } catch (SQLException exception) { showError(exception); }
    }

    private void fillTable(java.util.List<Supplier> suppliers) {
        tableModel.setRowCount(0);
        for (Supplier supplier : suppliers) {
            tableModel.addRow(new Object[]{supplier.getSupplierId(), supplier.getName(),
                    supplier.getPhone(), supplier.getAddress()});
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        addressField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
    }

    private void clearForm() {
        nameField.setText("");
        phoneField.setText("");
        addressField.setText("");
        table.clearSelection();
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
