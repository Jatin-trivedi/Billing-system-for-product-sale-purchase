package com.retail.model;

import java.math.BigDecimal;

public class PurchaseItem {
    private int id;
    private int purchaseId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;

    public PurchaseItem() {
    }

    public PurchaseItem(int id, int purchaseId, int productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.purchaseId = purchaseId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "PurchaseItem{productId=" + productId + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice + "}";
    }
}
