package com.retail.dao;

import com.retail.database.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SalesDAO {
    public int getSaleCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    public BigDecimal getTotalSalesAmount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total), 0) FROM sales";
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) throw new SQLException("Could not connect to MySQL.");
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                return result.next() && result.getBigDecimal(1) != null
                        ? result.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }
}
