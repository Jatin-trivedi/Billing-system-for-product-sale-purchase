package com.retail.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private int productId;
    private String name;
    private String category;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stock;

    public Product() {
    }

    // Constructor overloading - syllabus Unit 1: object construction with varying parameters
    public Product(int productId, String name, String category, BigDecimal purchasePrice,
                   BigDecimal sellingPrice, int stock) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    public Product(String name, String category, BigDecimal purchasePrice,
                   BigDecimal sellingPrice, int stock) {
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stock = stock;
    }

    public Product(String name, BigDecimal sellingPrice) {
        this.name = name;
        this.sellingPrice = sellingPrice;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @Override
    public String toString() {
        return "Product{id=" + productId + ", name='" + name + "', stock=" + stock + "}";
    }
}
