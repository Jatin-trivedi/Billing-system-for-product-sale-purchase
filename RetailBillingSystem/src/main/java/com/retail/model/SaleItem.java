package com.retail.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SaleItem {
    private int id;
    private int saleId;
    private int productId;
    private int quantity;
    private BigDecimal unitPrice;

    public SaleItem() {
    }

    public SaleItem(int id, int saleId, int productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.saleId = saleId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public BigDecimal getLineAmount() {
        BigDecimal price = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        return price.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public String toString() {
        return "SaleItem{productId=" + productId + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice + "}";
    }
}
