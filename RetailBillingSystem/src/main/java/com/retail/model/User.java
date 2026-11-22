package com.retail.model;

// Inheritance and abstraction - syllabus Unit 2: abstract base class
public abstract class User {
    private int userId;
    private String username;
    private String password;

    protected User() {
    }

    protected User(int userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public abstract String getRole();

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return getRole() + "{id=" + userId + ", username='" + username + "'}";
    }
}
