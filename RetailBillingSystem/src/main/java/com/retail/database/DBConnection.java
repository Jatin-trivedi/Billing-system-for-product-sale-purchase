package com.retail.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/retail_billing";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException exception) {
            System.err.println("Database connection failed. Check MySQL is running and DBConnection credentials.");
            System.err.println("Error: " + exception.getMessage());
            return null;
        }
    }
}
