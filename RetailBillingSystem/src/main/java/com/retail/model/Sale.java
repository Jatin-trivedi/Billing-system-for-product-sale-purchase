package com.retail.model;

import com.retail.interfaces.Billable;
import com.retail.interfaces.Printable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale implements Billable, Printable {
    public static final BigDecimal TAX_RATE = new BigDecimal("0.05");

    private int saleId;
    private int customerId;
    private LocalDateTime saleDate;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private List<SaleItem> items = new ArrayList<>();

    public Sale() {
    }

    public Sale(int saleId, int customerId, LocalDateTime saleDate, List<SaleItem> items) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.saleDate = saleDate;
        this.items = items == null ? new ArrayList<>() : items;
        calculateTotals();
    }

    public BigDecimal calculateSubtotal() {
        BigDecimal amount = BigDecimal.ZERO;
        for (SaleItem item : items) {
            amount = amount.add(item.getLineAmount());
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTax(BigDecimal subtotalAmount) {
        return subtotalAmount.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public void calculateTotals() {
        subtotal = calculateSubtotal();
        tax = calculateTax(subtotal);
        total = subtotal.add(tax).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getBillAmount() {
        calculateTotals();
        return total;
    }

    @Override
    public String getPrintableText() {
        calculateTotals();
        return "Sale #" + saleId + " | Customer #" + customerId
                + " | Subtotal: " + subtotal + " | Tax: " + tax
                + " | Total: " + total;
    }

    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public List<SaleItem> getItems() { return items; }
    public void setItems(List<SaleItem> items) {
        this.items = items == null ? new ArrayList<>() : items;
    }

    @Override
    public String toString() {
        calculateTotals();
        return "Sale{id=" + saleId + ", customerId=" + customerId
                + ", subtotal=" + subtotal + ", tax=" + tax + ", total=" + total + "}";
    }
}
