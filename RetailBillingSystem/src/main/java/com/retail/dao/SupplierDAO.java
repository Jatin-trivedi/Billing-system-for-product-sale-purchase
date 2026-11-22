package com.retail.dao;

import com.retail.database.DBConnection;
import com.retail.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public int add(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (name, phone, address) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setValues(statement, supplier);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Supplier was not added.");
    }

    public boolean update(Supplier supplier) throws SQLException {
        String sql = "UPDATE suppliers SET name=?, phone=?, address=? WHERE supplier_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setValues(statement, supplier);
                statement.setInt(4, supplier.getSupplierId());
                return statement.executeUpdate() > 0;
            }
        }
    }

    public boolean delete(int supplierId) throws SQLException {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, supplierId);
                return statement.executeUpdate() > 0;
            }
        }
    }

    public Supplier getById(int supplierId) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE supplier_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, supplierId);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? map(result) : null;
                }
            }
        }
    }

    public List<Supplier> getAll() throws SQLException {
        String sql = "SELECT * FROM suppliers ORDER BY supplier_id";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                while (result.next()) suppliers.add(map(result));
            }
        }
        return suppliers;
    }

    public List<Supplier> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM suppliers WHERE name LIKE ? ORDER BY name";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "%" + keyword + "%");
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) suppliers.add(map(result));
                }
            }
        }
        return suppliers;
    }

    private void setValues(PreparedStatement statement, Supplier supplier) throws SQLException {
        statement.setString(1, supplier.getName());
        statement.setString(2, supplier.getPhone());
        statement.setString(3, supplier.getAddress());
    }

    private Supplier map(ResultSet result) throws SQLException {
        return new Supplier(result.getInt("supplier_id"), result.getString("name"),
                result.getString("phone"), result.getString("address"));
    }

    private void requireConnection(Connection connection) throws SQLException {
        if (connection == null) throw new SQLException("Could not connect to MySQL.");
    }
}
