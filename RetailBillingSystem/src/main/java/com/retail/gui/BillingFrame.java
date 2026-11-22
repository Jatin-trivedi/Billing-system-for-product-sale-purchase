package com.retail.gui;

import com.retail.exceptions.InsufficientStockException;
import com.retail.model.Customer;
import com.retail.model.Product;
import com.retail.model.Sale;
import com.retail.model.SaleItem;
import com.retail.service.BillingService;
import com.retail.service.CustomerService;
import com.retail.service.ProductService;
import com.retail.util.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

public class BillingFrame extends JFrame {
    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();
    private final BillingService billingService = new BillingService();
    private final JComboBox<CustomerOption> customerBox = new JComboBox<>();
    private final JComboBox<ProductOption> productBox = new JComboBox<>();
    private final JTextField quantityField = new JTextField();
    private final JLabel subtotalLabel = new JLabel("Rs.0.00");
    private final JLabel taxLabel = new JLabel("Rs.0.00");
    private final JLabel totalLabel = new JLabel("Rs.0.00");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Product ID", "Product", "Quantity", "Price", "Line Total"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public BillingFrame() {
        super("Generate Bill");
        setSize(900, 600);
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
        panel.setBorder(BorderFactory.createTitledBorder("Billing"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(customerBox, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        JButton addCustomer = new JButton("Add Customer");
        addCustomer.addActionListener(event -> quickAddCustomer());
        panel.add(addCustomer, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        panel.add(productBox, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3;
        quantityField.setColumns(8);
        panel.add(quantityField, gbc);
        gbc.gridx = 4;
        JButton addItem = new JButton("Add to Bill");
        addItem.addActionListener(event -> addToBill());
        panel.add(addItem, gbc);
        return panel;
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton remove = new JButton("Remove Item");
        remove.addActionListener(event -> removeItem());
        panel.add(remove, BorderLayout.WEST);

        JPanel totals = new JPanel(new GridLayout(3, 2, 8, 4));
        totals.add(new JLabel("Subtotal:")); totals.add(subtotalLabel);
        totals.add(new JLabel("Tax (5%):")); totals.add(taxLabel);
        totals.add(new JLabel("Total:")); totals.add(totalLabel);
        panel.add(totals, BorderLayout.CENTER);

        JButton generate = new JButton("Generate Bill");
        generate.addActionListener(event -> generateBill());
        panel.add(generate, BorderLayout.EAST);
        return panel;
    }

    private void loadChoices() {
        try {
            customerBox.removeAllItems();
            for (Customer customer : customerService.getAll()) {
                customerBox.addItem(new CustomerOption(customer));
            }
            productBox.removeAllItems();
            for (Product product : productService.getAll()) {
                productBox.addItem(new ProductOption(product));
            }
        } catch (SQLException exception) {
            showError(exception);
        }
    }

    private void quickAddCustomer() {
        String name = JOptionPane.showInputDialog(this, "Customer name:");
        if (name == null || name.trim().isEmpty()) return;
        String phone = JOptionPane.showInputDialog(this, "Phone:");
        String address = JOptionPane.showInputDialog(this, "Address:");
        try {
            Customer customer = new Customer();
            customer.setName(name.trim());
            customer.setPhone(phone == null ? "" : phone.trim());
            customer.setAddress(address == null ? "" : address.trim());
            customerService.add(customer);
            loadChoices();
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        }
    }

    private void addToBill() {
        try {
            ProductOption productOption = (ProductOption) productBox.getSelectedItem();
            if (productOption == null) throw new IllegalArgumentException("Select a product.");
            int quantity = Integer.parseInt(quantityField.getText().trim());
            SaleItem item = new SaleItem();
            item.setProductId(productOption.product.getProductId());
            item.setQuantity(quantity);
            item.setUnitPrice(productOption.product.getSellingPrice());
            billingService.addItem(item);
            quantityField.setText("");
            refreshCart();
        } catch (NumberFormatException exception) {
            // Unit V: unchecked NumberFormatException is converted to a clear UI message.
            showError(new IllegalArgumentException("Enter a whole-number quantity."));
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        } catch (InsufficientStockException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a cart item to remove.");
            return;
        }
        int productId = (Integer) tableModel.getValueAt(row, 0);
        billingService.removeItem(productId);
        refreshCart();
    }

    private void refreshCart() {
        tableModel.setRowCount(0);
        Map<Integer, SaleItem> cart = billingService.getCart();
        for (Map.Entry<Integer, SaleItem> entry : cart.entrySet()) {
            SaleItem item = entry.getValue();
            String name = "Product #" + item.getProductId();
            for (int i = 0; i < productBox.getItemCount(); i++) {
                ProductOption option = productBox.getItemAt(i);
                if (option.product.getProductId() == item.getProductId()) {
                    name = option.product.getName();
                    break;
                }
            }
            tableModel.addRow(new Object[]{
                    item.getProductId(), name, item.getQuantity(),
                    item.getUnitPrice(), item.getLineAmount()});
        }
        BigDecimal subtotal = billingService.calculateSubtotal();
        BigDecimal tax = billingService.calculateTax(subtotal);
        subtotalLabel.setText(Utils.formatMoney(subtotal));
        taxLabel.setText(Utils.formatMoney(tax));
        totalLabel.setText(Utils.formatMoney(subtotal.add(tax)));
    }

    private void generateBill() {
        CustomerOption customer = (CustomerOption) customerBox.getSelectedItem();
        if (customer == null) {
            showError(new IllegalArgumentException("Select a customer."));
            return;
        }
        try {
            int saleId = billingService.generateBill(customer.customer.getCustomerId());
            Sale sale = new Sale();
            sale.setSaleId(saleId);
            sale.setCustomerId(customer.customer.getCustomerId());
            sale.setSaleDate(LocalDateTime.now());
            sale.setItems(new java.util.ArrayList<>(billingService.getCart().values()));
            sale.setSubtotal(billingService.calculateSubtotal());
            sale.setTax(billingService.calculateTax(sale.getSubtotal()));
            sale.setTotal(sale.getSubtotal().add(sale.getTax()));

            billingService.clearCart();
            refreshCart();
            new InvoiceDialog(this, sale).setVisible(true);
        } catch (InsufficientStockException exception) {
            JOptionPane.showMessageDialog(this, exception.getMessage(),
                    "Insufficient Stock", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException | SQLException exception) {
            showError(exception);
        }
    }

    private void showError(Exception exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static class CustomerOption {
        private final Customer customer;
        private CustomerOption(Customer customer) { this.customer = customer; }
        @Override public String toString() { return customer.getName(); }
    }

    private static class ProductOption {
        private final Product product;
        private ProductOption(Product product) { this.product = product; }
        @Override public String toString() { return product.getName(); }
    }
}
