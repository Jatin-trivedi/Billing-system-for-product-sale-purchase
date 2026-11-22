package com.retail.gui;

import com.retail.model.Product;
import com.retail.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.io.*;
import java.util.Arrays;
import java.util.Comparator;

public class ProductFrame extends JFrame {
    private final ProductService service = new ProductService();
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{
            "Stationery", "Accessories", "Groceries", "Electronics", "Other"});
    private final JTextField purchaseField = new JTextField();
    private final JTextField sellingField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField searchField = new JTextField();
    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Name", "Category", "Purchase", "Selling", "Stock"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    public ProductFrame() {
        super("Product Master");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(buildForm(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) fillFormFromSelectedRow();
        });
        refreshTable();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Product Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addField(form, gbc, 0, 0, "Name:", nameField);
        addField(form, gbc, 1, 0, "Category:", categoryBox);
        addField(form, gbc, 0, 1, "Purchase Price:", purchaseField);
        addField(form, gbc, 1, 1, "Selling Price:", sellingField);
        addField(form, gbc, 0, 2, "Stock:", stockField);
        return form;
    }

    private void addField(JPanel form, GridBagConstraints gbc, int column, int row,
                          String label, Component field) {
        gbc.gridx = column * 2; gbc.gridy = row; gbc.weightx = 0;
        form.add(new JLabel(label), gbc);
        gbc.gridx = column * 2 + 1; gbc.weightx = 1;
        form.add(field, gbc);
    }

    private JPanel buildButtons() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton add = new JButton("Add");
        JButton update = new JButton("Update");
        JButton delete = new JButton("Delete");
        JButton clear = new JButton("Clear");
        add.addActionListener(event -> addProduct());
        update.addActionListener(event -> updateProduct());
        delete.addActionListener(event -> deleteProduct());
        clear.addActionListener(event -> clearForm());
        actions.add(add); actions.add(update); actions.add(delete); actions.add(clear);
        JButton backup = new JButton("Backup Products");
        JButton viewBackup = new JButton("View Backup");
        JButton lowStockDemo = new JButton("Top 3 Low Stock");
        backup.addActionListener(event -> backupProducts());
        viewBackup.addActionListener(event -> viewBackup());
        lowStockDemo.addActionListener(event -> showTopLowStock());
        actions.add(backup); actions.add(viewBackup); actions.add(lowStockDemo);

        JPanel search = new JPanel(new BorderLayout(8, 8));
        search.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        search.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(event -> search());
        search.add(searchButton, BorderLayout.EAST);
        panel.add(actions);
        panel.add(search);
        return panel;
    }

    private Product readForm() {
        try {
            Product product = new Product();
            product.setName(nameField.getText().trim());
            product.setCategory((String) categoryBox.getSelectedItem());
            product.setPurchasePrice(new BigDecimal(purchaseField.getText().trim()));
            product.setSellingPrice(new BigDecimal(sellingField.getText().trim()));
            product.setStock(Integer.parseInt(stockField.getText().trim()));
            return product;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter valid prices and a whole-number stock.");
        }
    }

    private void addProduct() {
        try {
            service.add(readForm());
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Product added.");
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }
        try {
            Product product = readForm();
            product.setProductId((Integer) tableModel.getValueAt(row, 0));
            if (service.update(product)) {
                refreshTable();
                JOptionPane.showMessageDialog(this, "Product updated.");
            }
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }
        int choice = JOptionPane.showConfirmDialog(this, "Delete this product?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) return;
        try {
            int id = (Integer) tableModel.getValueAt(row, 0);
            if (service.delete(id)) {
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Product deleted.");
            }
        } catch (SQLException exception) {
            showError(exception);
        }
    }

    private void search() {
        try {
            fillTable(service.search(searchField.getText()));
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        }
    }

    private void refreshTable() {
        try {
            fillTable(service.getAll());
        } catch (SQLException exception) {
            showError(exception);
        }
    }

    private void fillTable(java.util.List<Product> products) {
        tableModel.setRowCount(0);
        for (Product product : products) {
            tableModel.addRow(new Object[]{
                    product.getProductId(), product.getName(), product.getCategory(),
                    product.getPurchasePrice(), product.getSellingPrice(), product.getStock()});
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        categoryBox.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 2)));
        purchaseField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        sellingField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        stockField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        nameField.setText("");
        categoryBox.setSelectedIndex(0);
        purchaseField.setText("");
        sellingField.setText("");
        stockField.setText("");
        table.clearSelection();
    }

    private void backupProducts() {
        try {
            java.util.List<Product> products = service.getAll();
            File folder = new File("backup");
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("Could not create backup folder.");
            }
            try (ObjectOutputStream output = new ObjectOutputStream(
                    new FileOutputStream(new File(folder, "products.ser")))) {
                output.writeObject(products);
            }
            JOptionPane.showMessageDialog(this,
                    "Product backup saved to backup/products.ser");
        } catch (SQLException | IOException exception) {
            // Exception handling demo: checked IOException/SQLException caught clearly.
            showError(exception);
        }
    }

    @SuppressWarnings("unchecked")
    private void viewBackup() {
        File file = new File("backup/products.ser");
        try (ObjectInputStream input =
                     new ObjectInputStream(new FileInputStream(file))) {
            java.util.List<Product> products =
                    (java.util.List<Product>) input.readObject();
            JTextArea area = new JTextArea();
            area.setEditable(false);
            for (Product product : products) {
                area.append(product.toString() + "\n");
            }
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Product Backup", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException exception) {
            // try/catch/finally is represented by try-with-resources in this method.
            showError(exception);
        }
    }

    private void showTopLowStock() {
        try {
            java.util.List<Product> productList = service.getAll();
            // Unit III: a Product array demonstrates arrays and sorting.
            Product[] products = productList.toArray(new Product[0]);
            Arrays.sort(products, Comparator.comparingInt(Product::getStock));
            int count = Math.min(3, products.length);
            StringBuilder result = new StringBuilder("Top 3 low-stock products:\n");
            for (int i = 0; i < count; i++) {
                result.append((i + 1)).append(". ").append(products[i].getName())
                        .append(" - stock ").append(products[i].getStock()).append('\n');
            }
            JOptionPane.showMessageDialog(this, result.toString());
        } catch (SQLException exception) {
            showError(exception);
        }
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
