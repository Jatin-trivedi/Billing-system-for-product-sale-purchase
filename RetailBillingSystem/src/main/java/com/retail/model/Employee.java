package com.retail.model;

// Inheritance and method overriding - syllabus Unit 2
public class Employee extends User {
    public Employee() {
    }

    public Employee(int userId, String username, String password) {
        super(userId, username, password);
    }

    @Override
    public String getRole() {
        return "EMPLOYEE";
    }
}
