package com.retail.service;

import com.retail.dao.ProductDAO;
import com.retail.model.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();

    public int add(Product product) throws SQLException {
        validate(product);
        return productDAO.add(product);
    }

    public boolean update(Product product) throws SQLException {
        validate(product);
        if (product.getProductId() <= 0) {
            throw new IllegalArgumentException("Product ID must be positive.");
        }
        return productDAO.update(product);
    }

    public boolean delete(int productId) throws SQLException {
        if (productId <= 0) throw new IllegalArgumentException("Product ID must be positive.");
        return productDAO.delete(productId);
    }

    public Product getById(int productId) throws SQLException {
        if (productId <= 0) throw new IllegalArgumentException("Product ID must be positive.");
        return productDAO.getById(productId);
    }

    public List<Product> getAll() throws SQLException {
        return productDAO.getAll();
    }

    public List<Product> search(String keyword) throws SQLException {
        if (keyword == null) throw new IllegalArgumentException("Search keyword is required.");
        return productDAO.search(keyword.trim());
    }

    public List<Product> getLowStockProducts(int threshold) throws SQLException {
        if (threshold < 0) throw new IllegalArgumentException("Stock threshold cannot be negative.");
        return productDAO.getLowStockProducts(threshold);
    }

    public int getTotalStock() throws SQLException {
        return productDAO.getTotalStock();
    }

    private void validate(Product product) {
        if (product == null) throw new IllegalArgumentException("Product is required.");
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (product.getPurchasePrice() == null || product.getPurchasePrice().signum() < 0) {
            throw new IllegalArgumentException("Purchase price must be zero or greater.");
        }
        if (product.getSellingPrice() == null || product.getSellingPrice().signum() < 0) {
            throw new IllegalArgumentException("Selling price must be zero or greater.");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }
}
