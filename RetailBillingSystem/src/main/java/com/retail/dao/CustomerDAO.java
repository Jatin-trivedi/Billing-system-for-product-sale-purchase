package com.retail.dao;

import com.retail.database.DBConnection;
import com.retail.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public int add(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, phone, address) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setValues(statement, customer);
                statement.executeUpdate();
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Customer was not added.");
    }

    public boolean update(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name=?, phone=?, address=? WHERE customer_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                setValues(statement, customer);
                statement.setInt(4, customer.getCustomerId());
                return statement.executeUpdate() > 0;
            }
        }
    }

    public boolean delete(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                return statement.executeUpdate() > 0;
            }
        }
    }

    public Customer getById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id=?";
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, customerId);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() ? map(result) : null;
                }
            }
        }
    }

    public List<Customer> getAll() throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY customer_id";
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet result = statement.executeQuery()) {
                while (result.next()) customers.add(map(result));
            }
        }
        return customers;
    }

    public List<Customer> search(String keyword) throws SQLException {
        String sql = "SELECT * FROM customers WHERE name LIKE ? ORDER BY name";
        List<Customer> customers = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            requireConnection(connection);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "%" + keyword + "%");
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) customers.add(map(result));
                }
            }
        }
        return customers;
    }

    private void setValues(PreparedStatement statement, Customer customer) throws SQLException {
        statement.setString(1, customer.getName());
        statement.setString(2, customer.getPhone());
        statement.setString(3, customer.getAddress());
    }

    private Customer map(ResultSet result) throws SQLException {
        return new Customer(result.getInt("customer_id"), result.getString("name"),
                result.getString("phone"), result.getString("address"));
    }

    private void requireConnection(Connection connection) throws SQLException {
        if (connection == null) throw new SQLException("Could not connect to MySQL.");
    }
}
