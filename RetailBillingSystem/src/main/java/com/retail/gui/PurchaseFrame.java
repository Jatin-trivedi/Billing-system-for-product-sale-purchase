package com.retail.gui;

import com.retail.model.Product;
import com.retail.model.Purchase;
import com.retail.model.PurchaseItem;
import com.retail.model.Supplier;
import com.retail.service.ProductService;
import com.retail.service.PurchaseService;
import com.retail.service.SupplierService;
import com.retail.util.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchaseFrame extends JFrame {
    private final SupplierService supplierService = new SupplierService();
    private final ProductService productService = new ProductService();
    private final PurchaseService purchaseService = new PurchaseService();
    private final JComboBox<SupplierOption> supplierBox = new JComboBox<>();
    private final JComboBox<ProductOption> productBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField();
    private final JTextField unitPriceField = new JTextField();
    private final JLabel totalLabel = new JLabel("Rs.0.00");
    private final List<PurchaseItem> cart = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Product", "Quantity", "Unit Price", "Amount"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public PurchaseFrame() {
        super("Purchase Stock");
        setSize(850, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(buildControls(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);
        loadChoices();
    }

    private JPanel buildControls() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Purchase Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Supplier:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(supplierBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(productBox, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3;
        quantityField.setColumns(8);
        panel.add(quantityField, gbc);

        gbc.gridx = 4;
        panel.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 5;
        unitPriceField.setColumns(8);
        panel.add(unitPriceField, gbc);

        gbc.gridx = 6;
        JButton addItem = new JButton("Add Item");
        addItem.addActionListener(event -> addItem());
        panel.add(addItem, gbc);
        return panel;
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JButton remove = new JButton("Remove Item");
        remove.addActionListener(event -> removeItem());
        panel.add(remove, BorderLayout.WEST);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        totalPanel.add(new JLabel("Running Total:"));
        totalPanel.add(totalLabel);
        panel.add(totalPanel, BorderLayout.CENTER);

        JButton save = new JButton("Save Purchase");
        save.addActionListener(event -> savePurchase());
        panel.add(save, BorderLayout.EAST);
        return panel;
    }

    private void loadChoices() {
        try {
            supplierBox.removeAllItems();
            for (Supplier supplier : supplierService.getAll()) {
                supplierBox.addItem(new SupplierOption(supplier));
            }
            productBox.removeAllItems();
            for (Product product : productService.getAll()) {
                productBox.addItem(new ProductOption(product));
            }
            if (productBox.getItemCount() > 0) {
                productBox.setSelectedIndex(0);
                setPriceFromSelectedProduct();
            }
            productBox.addActionListener(event -> setPriceFromSelectedProduct());
        } catch (SQLException exception) {
            showError(exception);
        }
    }

    private void setPriceFromSelectedProduct() {
        ProductOption selected = (ProductOption) productBox.getSelectedItem();
        if (selected != null && selected.product.getPurchasePrice() != null) {
            unitPriceField.setText(selected.product.getPurchasePrice().toPlainString());
        }
    }

    private void addItem() {
        try {
            ProductOption productOption = (ProductOption) productBox.getSelectedItem();
            if (productOption == null) throw new IllegalArgumentException("Select a product.");
            int quantity = Integer.parseInt(quantityField.getText().trim());
            BigDecimal unitPrice = new BigDecimal(unitPriceField.getText().trim());
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero.");
            if (unitPrice.signum() <= 0) throw new IllegalArgumentException("Unit price must be greater than zero.");

            PurchaseItem item = new PurchaseItem();
            item.setProductId(productOption.product.getProductId());
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            cart.add(item);
            refreshCart();
            quantityField.setText("");
        } catch (NumberFormatException exception) {
            showError(new IllegalArgumentException("Enter valid quantity and unit price."));
        } catch (IllegalArgumentException exception) {
            showError(exception);
        }
    }

    private void removeItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a cart item to remove.");
            return;
        }
        cart.remove(row);
        refreshCart();
    }

    private void refreshCart() {
        tableModel.setRowCount(0);
        for (PurchaseItem item : cart) {
            tableModel.addRow(new Object[]{
                    findProductName(item.getProductId()),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            });
        }
        totalLabel.setText(Utils.formatMoney(purchaseService.calculateTotal(cart)));
    }

    private String findProductName(int productId) {
        for (int i = 0; i < productBox.getItemCount(); i++) {
            ProductOption option = productBox.getItemAt(i);
            if (option.product.getProductId() == productId) {
                return option.product.getName();
            }
        }
        return "Product #" + productId;
    }

    private void savePurchase() {
        SupplierOption supplierOption = (SupplierOption) supplierBox.getSelectedItem();
        if (supplierOption == null) {
            showError(new IllegalArgumentException("Select a supplier."));
            return;
        }
        try {
            Purchase purchase = new Purchase();
            purchase.setSupplierId(supplierOption.supplier.getSupplierId());
            purchase.setItems(new ArrayList<>(cart));
            int purchaseId = purchaseService.savePurchase(purchase);
            cart.clear();
            refreshCart();
            JOptionPane.showMessageDialog(this, "Purchase saved successfully. ID: " + purchaseId);
            loadChoices();
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        }
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class SupplierOption {
        private final Supplier supplier;
        private SupplierOption(Supplier supplier) { this.supplier = supplier; }
        @Override public String toString() { return supplier.getName(); }
    }

    private static class ProductOption {
        private final Product product;
        private ProductOption(Product product) { this.product = product; }
        @Override public String toString() { return product.getName(); }
    }
}
