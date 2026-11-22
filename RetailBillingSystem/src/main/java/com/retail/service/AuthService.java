package com.retail.service;

import com.retail.dao.UserDAO;
import com.retail.model.User;

import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public User authenticate(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }
        return userDAO.authenticate(username.trim(), password);
    }
}
