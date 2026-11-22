package com.retail.model;

// Inheritance and method overriding - syllabus Unit 2
public class Admin extends User {
    public Admin() {
    }

    public Admin(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
