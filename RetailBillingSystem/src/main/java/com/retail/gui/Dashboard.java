package com.retail.gui;

import com.retail.model.User;
import com.retail.service.DashboardService;
import com.retail.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.sql.SQLException;

public class Dashboard extends JFrame {
    private final User user;
    private final DashboardService dashboardService = new DashboardService();
    private final JLabel productCountLabel = cardValueLabel();
    private final JLabel stockLabel = cardValueLabel();
    private final JLabel salesLabel = cardValueLabel();

    public Dashboard(User user) {
        super("Retail Billing & Inventory - Dashboard");
        this.user = user;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCards(), BorderLayout.CENTER);
        add(buildMenu(), BorderLayout.SOUTH);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent event) {
                refreshCards();
            }
        });
        refreshCards();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        panel.add(new JLabel("Welcome, " + user.getUsername()), BorderLayout.WEST);
        JButton logout = new JButton("Logout");
        logout.addActionListener(event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        panel.add(logout, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCards() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 18, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.add(createCard("Total Products", productCountLabel));
        panel.add(createCard("Total Stock", stockLabel));
        panel.add(createCard("Total Sales Amount", salesLabel));
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildMenu() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        panel.add(button("Products", () -> new ProductFrame().setVisible(true)));
        panel.add(button("Customers", () -> new CustomerFrame().setVisible(true)));
        panel.add(button("Suppliers", () -> new SupplierFrame().setVisible(true)));
        panel.add(button("Purchases", () -> new PurchaseFrame().setVisible(true)));
        panel.add(button("Billing", () -> new BillingFrame().setVisible(true)));
        panel.add(button("Inventory", () -> new InventoryFrame().setVisible(true)));
        panel.add(button("Sales History", () -> new SalesHistoryFrame().setVisible(true)));
        panel.add(button("Refresh", this::refreshCards));
        return panel;
    }

    private JButton button(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void comingSoon(String name) {
        JOptionPane.showMessageDialog(this, name + " screen is coming soon.",
                name, JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshCards() {
        try {
            productCountLabel.setText(String.valueOf(dashboardService.getProductCount()));
            stockLabel.setText(String.valueOf(dashboardService.getTotalStock()));
            BigDecimal salesAmount = dashboardService.getTotalSalesAmount();
            salesLabel.setText(Utils.formatMoney(salesAmount));
        } catch (SQLException exception) {
            productCountLabel.setText("-");
            stockLabel.setText("-");
            salesLabel.setText("-");
            JOptionPane.showMessageDialog(this, "Could not refresh dashboard: "
                    + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static JLabel cardValueLabel() {
        JLabel label = new JLabel("0", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        return label;
    }
}
