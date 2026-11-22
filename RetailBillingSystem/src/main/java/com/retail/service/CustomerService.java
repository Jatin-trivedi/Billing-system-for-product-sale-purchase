package com.retail.service;

import com.retail.dao.CustomerDAO;
import com.retail.model.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerService {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public int add(Customer customer) throws SQLException {
        validate(customer);
        return customerDAO.add(customer);
    }

    public boolean update(Customer customer) throws SQLException {
        validate(customer);
        if (customer.getCustomerId() <= 0) throw new IllegalArgumentException("Customer ID must be positive.");
        return customerDAO.update(customer);
    }

    public boolean delete(int customerId) throws SQLException {
        if (customerId <= 0) throw new IllegalArgumentException("Customer ID must be positive.");
        return customerDAO.delete(customerId);
    }

    public Customer getById(int customerId) throws SQLException {
        if (customerId <= 0) throw new IllegalArgumentException("Customer ID must be positive.");
        return customerDAO.getById(customerId);
    }

    public List<Customer> getAll() throws SQLException {
        return customerDAO.getAll();
    }

    public List<Customer> search(String keyword) throws SQLException {
        if (keyword == null) throw new IllegalArgumentException("Search keyword is required.");
        return customerDAO.search(keyword.trim());
    }

    private void validate(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is required.");
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
    }
}
