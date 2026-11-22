package com.retail.gui;

import com.retail.model.Product;
import com.retail.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InventoryFrame extends JFrame {
    private final ProductService productService = new ProductService();
    private final JLabel warningLabel = new JLabel(" ");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Name", "Category", "Purchase Price",
                    "Selling Price", "Stock"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public InventoryFrame() {
        super("Inventory");
        setSize(800, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        warningLabel.setForeground(new Color(180, 0, 0));
        warningLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        add(warningLabel, BorderLayout.NORTH);
        table.getColumnModel().getColumn(5).setCellRenderer(new LowStockRenderer());
        add(new JScrollPane(table), BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        tableModel.setRowCount(0);
        int lowStockCount = 0;
        try {
            List<Product> products = productService.getAll();
            for (Product product : products) {
                tableModel.addRow(new Object[]{
                        product.getProductId(), product.getName(), product.getCategory(),
                        product.getPurchasePrice(), product.getSellingPrice(), product.getStock()
                });
                if (product.getStock() <= 5) lowStockCount++;
            }
            warningLabel.setText(lowStockCount == 0
                    ? "All product stocks are above 5 units."
                    : lowStockCount + " product(s) have low stock (5 or fewer).");
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Could not load inventory: "
                    + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class LowStockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component cell = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            int stock = value == null ? 0 : Integer.parseInt(value.toString());
            if (stock <= 5) {
                cell.setBackground(isSelected ? new Color(150, 60, 60) : new Color(255, 180, 180));
                cell.setForeground(Color.BLACK);
            } else {
                cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                cell.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            }
            return cell;
        }
    }
}
