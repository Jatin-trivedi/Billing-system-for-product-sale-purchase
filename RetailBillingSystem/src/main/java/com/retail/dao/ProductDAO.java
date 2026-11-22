package com.retail.dao;

import com.retail.database.DBConnection;
import com.retail.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public int add(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category, purchase_price, selling_price, stock) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setValues(statement, product);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }
        throw new SQLException("Product was not added.");
    }

    public boolean update(Product product) throws SQLException {
        String sql = "UPDATE products SET name=?, category=?, purchase_price=?, selling_price=?, stock=? WHERE product_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setValues(statement, product);
                statement.setInt(6, product.getProductId());
                return statement.executeUpdate() > 0;
            }
        }
    }

    public boolean delete(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, productId);
                return statement.executeUpdate() > 0;
            }
        }
    }

    public Product getById(int productId) throws SQLException {
        String sql = "SELECT * FROM products WHERE product_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, productId);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? map(result) : null;
                }
            }
        }
    }

    public List<Product> getAll() throws SQLException {
        String sql = "SELECT * FROM products ORDER BY product_id";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    products.add(map(result));
                }
            }
        }
        return products;
    }

    public List<Product> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY name";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "%" + keyword + "%");
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        products.add(map(result));
                    }
                }
            }
        }
        return products;
    }

    public List<Product> getLowStockProducts(int threshold) throws SQLException {
        String sql = "SELECT * FROM products WHERE stock < ? ORDER BY stock";
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, threshold);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        products.add(map(result));
                    }
                }
            }
        }
        return products;
    }

    public int getTotalStock() throws SQLException {
        String sql = "SELECT COALESCE(SUM(stock), 0) FROM products";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    private void setValues(PreparedStatement statement, Product product) throws SQLException {
        statement.setString(1, product.getName());
        statement.setString(2, product.getCategory());
        statement.setBigDecimal(3, product.getPurchasePrice());
        statement.setBigDecimal(4, product.getSellingPrice());
        statement.setInt(5, product.getStock());
    }

    private Product map(ResultSet result) throws SQLException {
        return new Product(
                result.getInt("product_id"),
                result.getString("name"),
                result.getString("category"),
                result.getBigDecimal("purchase_price"),
                result.getBigDecimal("selling_price"),
                result.getInt("stock"));
    }

    private void requireConnection(Connection connection) throws SQLException {
        if (connection == null) {
            throw new SQLException("Could not connect to MySQL.");
        }
    }
}
