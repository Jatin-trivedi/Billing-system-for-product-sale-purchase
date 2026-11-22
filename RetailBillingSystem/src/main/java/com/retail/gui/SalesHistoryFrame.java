package com.retail.gui;

import com.retail.model.Sale;
import com.retail.service.BillingService;
import com.retail.threads.ReportTask;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SalesHistoryFrame extends JFrame {
    private final BillingService billingService = new BillingService();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Sale ID", "Date", "Customer", "Total"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel reportStatus = new JLabel(" ");
    private final JProgressBar reportProgress = new JProgressBar();

    public SalesHistoryFrame() {
        super("Sales History");
        setSize(760, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildReportPanel(), BorderLayout.SOUTH);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (event.getClickCount() == 2) viewSelectedInvoice();
            }
        });
        refresh();
    }

    private void refresh() {
        tableModel.setRowCount(0);
        try {
            List<Sale> sales = billingService.getAllSales();
            for (Sale sale : sales) {
                tableModel.addRow(new Object[]{
                        sale.getSaleId(),
                        sale.getSaleDate(),
                        billingService.getCustomerName(sale.getCustomerId()),
                        sale.getTotal()
                });
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Could not load sales history: "
                    + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewSelectedInvoice() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int saleId = (Integer) tableModel.getValueAt(row, 0);
        try {
            Sale sale = billingService.getSaleById(saleId);
            if (sale == null) {
                JOptionPane.showMessageDialog(this, "Sale was not found.");
            } else {
                new InvoiceDialog(this, sale).setVisible(true);
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Could not load invoice: "
                    + exception.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton export = new JButton("Export Sales Report");
        export.addActionListener(event -> exportSalesReport(export));

        JButton viewInvoice = new JButton("View saved invoice");
        viewInvoice.addActionListener(event -> viewSavedInvoice());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(export);
        buttons.add(viewInvoice);
        panel.add(buttons, BorderLayout.WEST);

        reportProgress.setIndeterminate(false);
        reportProgress.setStringPainted(true);
        reportProgress.setMinimum(0);
        reportProgress.setMaximum(100);
        reportProgress.setValue(0);
        reportStatus.setText(" ");

        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.add(reportStatus, BorderLayout.NORTH);
        statusPanel.add(reportProgress, BorderLayout.SOUTH);
        panel.add(statusPanel, BorderLayout.CENTER);
        return panel;
    }

    private void exportSalesReport(JButton exportButton) {
        exportButton.setEnabled(false);
        reportProgress.setValue(15);
        reportStatus.setText("Starting background report...");

        ReportTask task = new ReportTask(status -> {
            reportStatus.setText(status);
            if (status.startsWith("Report exported")) {
                reportProgress.setValue(100);
                exportButton.setEnabled(true);
            } else if (status.startsWith("Preparing")
                    || status.startsWith("Writing")) {
                reportProgress.setValue(status.startsWith("Preparing") ? 45 : 75);
            } else {
                exportButton.setEnabled(true);
                reportProgress.setValue(0);
            }
        });
        new Thread(task, "sales-report-export").start();
    }

    private void viewSavedInvoice() {
        JFileChooser chooser = new JFileChooser(new File("invoices"));
        chooser.setDialogTitle("View Saved Invoice");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(chooser.getSelectedFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append('\n');
            }
            JTextArea area = new JTextArea(text.toString());
            area.setEditable(false);
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Saved Invoice", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Could not read invoice: "
                    + exception.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
