package com.retail.service;

import com.retail.dao.SupplierDAO;
import com.retail.model.Supplier;

import java.sql.SQLException;
import java.util.List;

public class SupplierService {
    private final SupplierDAO supplierDAO = new SupplierDAO();

    public int add(Supplier supplier) throws SQLException {
        validate(supplier);
        return supplierDAO.add(supplier);
    }

    public boolean update(Supplier supplier) throws SQLException {
        validate(supplier);
        if (supplier.getSupplierId() <= 0) throw new IllegalArgumentException("Supplier ID must be positive.");
        return supplierDAO.update(supplier);
    }

    public boolean delete(int supplierId) throws SQLException {
        if (supplierId <= 0) throw new IllegalArgumentException("Supplier ID must be positive.");
        return supplierDAO.delete(supplierId);
    }

    public Supplier getById(int supplierId) throws SQLException {
        if (supplierId <= 0) throw new IllegalArgumentException("Supplier ID must be positive.");
        return supplierDAO.getById(supplierId);
    }

    public List<Supplier> getAll() throws SQLException {
        return supplierDAO.getAll();
    }

    public List<Supplier> search(String keyword) throws SQLException {
        if (keyword == null) throw new IllegalArgumentException("Search keyword is required.");
        return supplierDAO.search(keyword.trim());
    }

    private void validate(Supplier supplier) {
        if (supplier == null) throw new IllegalArgumentException("Supplier is required.");
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name is required.");
        }
    }
}
