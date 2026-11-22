package com.retail.dao;

import com.retail.database.DBConnection;
import com.retail.model.Admin;
import com.retail.model.Employee;
import com.retail.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username=? AND password=?";
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                throw new SQLException("Could not connect to MySQL.");
            }
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) {
                        return null;
                    }
                    int userId = result.getInt("user_id");
                    String role = result.getString("role");
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        return new Admin(userId, result.getString("username"),
                                result.getString("password"));
                    }
                    if ("EMPLOYEE".equalsIgnoreCase(role)) {
                        return new Employee(userId, result.getString("username"),
                                result.getString("password"));
                    }
                    return null;
                }
            }
        }
    }
}
