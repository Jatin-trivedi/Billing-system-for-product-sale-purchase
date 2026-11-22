package com.retail.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Purchase {
    private int purchaseId;
    private int supplierId;
    private LocalDateTime purchaseDate;
    private BigDecimal totalAmount;
    private List<PurchaseItem> items = new ArrayList<>();

    public Purchase() {
    }

    public Purchase(int purchaseId, int supplierId, LocalDateTime purchaseDate,
                    BigDecimal totalAmount, List<PurchaseItem> items) {
        this.purchaseId = purchaseId;
        this.supplierId = supplierId;
        this.purchaseDate = purchaseDate;
        this.totalAmount = totalAmount;
        this.items = items == null ? new ArrayList<>() : items;
    }

    public int getPurchaseId() { return purchaseId; }
    public void setPurchaseId(int purchaseId) { this.purchaseId = purchaseId; }
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDateTime purchaseDate) { this.purchaseDate = purchaseDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<PurchaseItem> getItems() { return items; }
    public void setItems(List<PurchaseItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }

    @Override
    public String toString() {
        return "Purchase{id=" + purchaseId + ", supplierId=" + supplierId
                + ", total=" + totalAmount + ", items=" + items.size() + "}";
    }
}
