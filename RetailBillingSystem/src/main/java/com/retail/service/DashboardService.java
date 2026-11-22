package com.retail.service;

import com.retail.dao.ProductDAO;
import com.retail.dao.SalesDAO;

import java.math.BigDecimal;
import java.sql.SQLException;

public class DashboardService {
    private final ProductDAO productDAO = new ProductDAO();
    private final SalesDAO salesDAO = new SalesDAO();

    public int getProductCount() throws SQLException {
        return productDAO.getAll().size();
    }

    public int getTotalStock() throws SQLException {
        return productDAO.getTotalStock();
    }

    public int getSaleCount() throws SQLException {
        return salesDAO.getSaleCount();
    }

    public BigDecimal getTotalSalesAmount() throws SQLException {
        return salesDAO.getTotalSalesAmount();
    }
}
