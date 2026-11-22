package com.retail.service;

import com.retail.dao.BillingDAO;
import com.retail.dao.ProductDAO;
import com.retail.exceptions.InsufficientStockException;
import com.retail.model.Product;
import com.retail.model.Sale;
import com.retail.model.SaleItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class BillingService {
    private final Map<Integer, SaleItem> cart = new HashMap<>();
    private final BillingDAO billingDAO = new BillingDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public void addItem(SaleItem item) throws SQLException, InsufficientStockException {
        if (item == null || item.getProductId() <= 0) {
            // Unit V: throw is used for an unchecked validation failure.
            throw new IllegalArgumentException("Select a valid product.");
        }
        if (item.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (item.getUnitPrice() == null || item.getUnitPrice().signum() <= 0) {
            throw new IllegalArgumentException("Selling price must be greater than zero.");
        }

        SaleItem existing = cart.get(item.getProductId());
        int requested = item.getQuantity()
                + (existing == null ? 0 : existing.getQuantity());
        Product product = productDAO.getById(item.getProductId());
        if (product == null) throw new IllegalArgumentException("Product not found.");
        if (requested > product.getStock()) {
            throw new InsufficientStockException(product.getName(),
                    product.getStock(), requested);
        }

        item.setQuantity(requested);
        cart.put(item.getProductId(), item);
    }

    public void removeItem(int productId) {
        cart.remove(productId);
    }

    public Map<Integer, SaleItem> getCart() {
        return cart;
    }

    public void clearCart() {
        cart.clear();
    }

    public BigDecimal calculateSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (SaleItem item : cart.values()) {
            subtotal = subtotal.add(item.getLineAmount());
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTax(BigDecimal subtotal) {
        return subtotal.multiply(new BigDecimal("0.05"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTotal() {
        BigDecimal subtotal = calculateSubtotal();
        return subtotal.add(calculateTax(subtotal)).setScale(2, RoundingMode.HALF_UP);
    }

    public int generateBill(int customerId)
            throws SQLException, InsufficientStockException {
        if (customerId <= 0) throw new IllegalArgumentException("Select a customer.");
        if (cart.isEmpty()) throw new IllegalArgumentException("Add at least one item.");

        Sale sale = new Sale();
        sale.setCustomerId(customerId);
        sale.setItems(new java.util.ArrayList<>(cart.values()));
        sale.setSubtotal(calculateSubtotal());
        sale.setTax(calculateTax(sale.getSubtotal()));
        sale.setTotal(sale.getSubtotal().add(sale.getTax())
                .setScale(2, RoundingMode.HALF_UP));
        return billingDAO.saveSale(sale);
    }

    public List<Sale> getAllSales() throws SQLException {
        return billingDAO.getAllSales();
    }

    public Sale getSaleById(int saleId) throws SQLException {
        return billingDAO.getSaleById(saleId);
    }

    public String getCustomerName(int customerId) throws SQLException {
        return billingDAO.getCustomerName(customerId);
    }

    public String getProductName(int productId) throws SQLException {
        return billingDAO.getProductName(productId);
    }
}
