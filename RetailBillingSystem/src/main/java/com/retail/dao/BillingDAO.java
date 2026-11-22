package com.retail.dao;

import com.retail.database.DBConnection;
import com.retail.exceptions.InsufficientStockException;
import com.retail.model.Sale;
import com.retail.model.SaleItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillingDAO {
    public int saveSale(Sale sale) throws SQLException, InsufficientStockException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            connection.setAutoCommit(false);

            String saleSql =
                    "INSERT INTO sales (customer_id, sale_date, subtotal, tax, total) "
                            + "VALUES (?, CURRENT_TIMESTAMP, ?, ?, ?)";
            int saleId;
            try (PreparedStatement statement =
                         connection.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, sale.getCustomerId());
                statement.setBigDecimal(2, sale.getSubtotal());
                statement.setBigDecimal(3, sale.getTax());
                statement.setBigDecimal(4, sale.getTotal());
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Sale ID was not generated.");
                    saleId = keys.getInt(1);
                }
            }

            for (SaleItem item : sale.getItems()) {
                String stockSql =
                        "UPDATE products SET stock = stock - ? "
                                + "WHERE product_id = ? AND stock >= ?";
                try (PreparedStatement statement = connection.prepareStatement(stockSql)) {
                    statement.setInt(1, item.getQuantity());
                    statement.setInt(2, item.getProductId());
                    statement.setInt(3, item.getQuantity());
                    int updated = statement.executeUpdate();
                    if (updated != 1) {
                        try (PreparedStatement info =
                                     connection.prepareStatement(
                                             "SELECT name, stock FROM products WHERE product_id = ?")) {
                            info.setInt(1, item.getProductId());
                            try (ResultSet result = info.executeQuery()) {
                                if (result.next()) {
                                    throw new InsufficientStockException(
                                            result.getString("name"),
                                            result.getInt("stock"),
                                            item.getQuantity());
                                }
                            }
                        }
                        throw new InsufficientStockException(
                                "Product #" + item.getProductId(), 0, item.getQuantity());
                    }
                }

                String itemSql =
                        "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price) "
                                + "VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(itemSql)) {
                    statement.setInt(1, saleId);
                    statement.setInt(2, item.getProductId());
                    statement.setInt(3, item.getQuantity());
                    statement.setBigDecimal(4, item.getUnitPrice());
                    statement.executeUpdate();
                }
            }

            connection.commit();
            return saleId;
        } catch (SQLException | InsufficientStockException exception) {
            // Unit V: catch/rollback/rethrow preserves the original checked failure.
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    if (exception instanceof SQLException) {
                        ((SQLException) exception).addSuppressed(rollbackException);
                    }
                }
            }
            if (exception instanceof SQLException) throw (SQLException) exception;
            throw (InsufficientStockException) exception;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } finally {
                    connection.close();
                }
            }
        }
    }

    public List<Sale> getAllSales() throws SQLException {
        String sql = "SELECT sale_id, customer_id, sale_date, subtotal, tax, total "
                + "FROM sales ORDER BY sale_date DESC, sale_id DESC";
        List<Sale> sales = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Sale sale = mapSale(result);
                    sales.add(sale);
                }
            }
        }
        return sales;
    }

    public Sale getSaleById(int saleId) throws SQLException {
        String sql = "SELECT sale_id, customer_id, sale_date, subtotal, tax, total "
                + "FROM sales WHERE sale_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, saleId);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) return null;
                    Sale sale = mapSale(result);
                    sale.setItems(getItems(connection, saleId));
                    return sale;
                }
            }
        }
    }

    public String getCustomerName(int customerId) throws SQLException {
        String sql = "SELECT name FROM customers WHERE customer_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? result.getString("name") : "Walk-in Customer";
                }
            }
        }
    }

    public String getProductName(int productId) throws SQLException {
        String sql = "SELECT name FROM products WHERE product_id = ?";
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, productId);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? result.getString("name") : "Product #" + productId;
                }
            }
        }
    }

    private List<SaleItem> getItems(Connection connection, int saleId) throws SQLException {
        String sql = "SELECT id, sale_id, product_id, quantity, unit_price "
                + "FROM sale_items WHERE sale_id = ?";
        List<SaleItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, saleId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(new SaleItem(
                            result.getInt("id"),
                            result.getInt("sale_id"),
                            result.getInt("product_id"),
                            result.getInt("quantity"),
                            result.getBigDecimal("unit_price")));
                }
            }
        }
        return items;
    }

    private Sale mapSale(ResultSet result) throws SQLException {
        Sale sale = new Sale();
        sale.setSaleId(result.getInt("sale_id"));
        sale.setCustomerId(result.getInt("customer_id"));
        Timestamp date = result.getTimestamp("sale_date");
        if (date != null) sale.setSaleDate(date.toLocalDateTime());
        sale.setSubtotal(result.getBigDecimal("subtotal"));
        sale.setTax(result.getBigDecimal("tax"));
        sale.setTotal(result.getBigDecimal("total"));
        return sale;
    }
}
