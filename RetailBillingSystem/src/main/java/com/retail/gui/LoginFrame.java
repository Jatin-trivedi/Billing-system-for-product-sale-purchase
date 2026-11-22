package com.retail.gui;

import com.retail.model.User;
import com.retail.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField usernameField = new JTextField(16);
    private final JPasswordField passwordField = new JPasswordField(16);
    private final JLabel messageLabel = new JLabel(" ");
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        super("Retail Billing System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildPanel());
    }

    private JPanel buildPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Retail Billing System", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> login());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        gbc.gridy = 4;
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(messageLabel, gbc);

        getRootPane().setDefaultButton(loginButton);
        return panel;
    }

    private void login() {
        try {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            User user = authService.authenticate(username, password);
            if (user == null) {
                messageLabel.setText("Invalid username or password.");
            } else {
                dispose();
                new Dashboard(user).setVisible(true);
            }
        } catch (IllegalArgumentException | SQLException exception) {
            messageLabel.setText(exception instanceof SQLException
                    ? "Database error: " + exception.getMessage()
                    : exception.getMessage());
        }
    }
}
