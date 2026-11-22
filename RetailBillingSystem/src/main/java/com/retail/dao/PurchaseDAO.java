package com.retail.dao;

import com.retail.database.DBConnection;
import com.retail.model.Purchase;
import com.retail.model.PurchaseItem;

import java.sql.*;

public class PurchaseDAO {
    public int savePurchase(Purchase purchase) throws SQLException {
        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            if (connection == null) {
                throw new SQLException("Could not connect to MySQL.");
            }
            connection.setAutoCommit(false);

            String purchaseSql =
                    "INSERT INTO purchases (supplier_id, purchase_date, total_amount) VALUES (?, CURRENT_TIMESTAMP, ?)";
            int purchaseId;
            try (PreparedStatement purchaseStatement =
                         connection.prepareStatement(purchaseSql, Statement.RETURN_GENERATED_KEYS)) {
                purchaseStatement.setInt(1, purchase.getSupplierId());
                purchaseStatement.setBigDecimal(2, purchase.getTotalAmount());
                purchaseStatement.executeUpdate();

                try (ResultSet keys = purchaseStatement.getGeneratedKeys()) {
                    if (!keys.next()) {
                        throw new SQLException("Purchase ID was not generated.");
                    }
                    purchaseId = keys.getInt(1);
                }
            }

            String itemSql =
                    "INSERT INTO purchase_items (purchase_id, product_id, quantity, unit_price) "
                            + "VALUES (?, ?, ?, ?)";
            String stockSql =
                    "UPDATE products SET stock = stock + ?, purchase_price = ? WHERE product_id = ?";

            for (PurchaseItem item : purchase.getItems()) {
                try (PreparedStatement itemStatement = connection.prepareStatement(itemSql)) {
                    itemStatement.setInt(1, purchaseId);
                    itemStatement.setInt(2, item.getProductId());
                    itemStatement.setInt(3, item.getQuantity());
                    itemStatement.setBigDecimal(4, item.getUnitPrice());
                    itemStatement.executeUpdate();
                }

                try (PreparedStatement stockStatement = connection.prepareStatement(stockSql)) {
                    stockStatement.setInt(1, item.getQuantity());
                    stockStatement.setBigDecimal(2, item.getUnitPrice());
                    stockStatement.setInt(3, item.getProductId());
                    if (stockStatement.executeUpdate() != 1) {
                        throw new SQLException("Product stock update failed for product ID "
                                + item.getProductId());
                    }
                }
            }

            connection.commit();
            return purchaseId;
        } catch (SQLException exception) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            throw exception;
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
}
