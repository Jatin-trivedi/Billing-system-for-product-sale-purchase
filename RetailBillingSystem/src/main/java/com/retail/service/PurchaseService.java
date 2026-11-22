package com.retail.service;

import com.retail.dao.PurchaseDAO;
import com.retail.model.Purchase;
import com.retail.model.PurchaseItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

public class PurchaseService {
    private final PurchaseDAO purchaseDAO = new PurchaseDAO();

    public int savePurchase(Purchase purchase) throws SQLException {
        if (purchase == null) {
            throw new IllegalArgumentException("Purchase is required.");
        }
        if (purchase.getSupplierId() <= 0) {
            throw new IllegalArgumentException("Select a supplier.");
        }
        if (purchase.getItems() == null || purchase.getItems().isEmpty()) {
            throw new IllegalArgumentException("Add at least one item.");
        }

        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem item : purchase.getItems()) {
            if (item.getProductId() <= 0) {
                throw new IllegalArgumentException("Select a valid product.");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }
            if (item.getUnitPrice() == null || item.getUnitPrice().signum() <= 0) {
                throw new IllegalArgumentException("Unit price must be greater than zero.");
            }
            total = total.add(item.getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        purchase.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        return purchaseDAO.savePurchase(purchase);
    }

    public BigDecimal calculateTotal(java.util.List<PurchaseItem> items) {
        if (items == null) return BigDecimal.ZERO.setScale(2);
        BigDecimal total = BigDecimal.ZERO;
        for (PurchaseItem item : items) {
            if (item.getUnitPrice() != null) {
                total = total.add(item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
