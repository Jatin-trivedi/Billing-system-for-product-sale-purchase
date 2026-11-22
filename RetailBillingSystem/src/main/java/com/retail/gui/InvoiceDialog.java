package com.retail.gui;

import com.retail.model.Sale;
import com.retail.model.SaleItem;
import com.retail.service.BillingService;
import com.retail.util.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

public class InvoiceDialog extends JDialog {
    private final JTextArea invoiceArea;
    private final String invoiceText;
    private final int saleId;

    public InvoiceDialog(Frame owner, Sale sale) {
        super(owner, "Invoice #" + sale.getSaleId(), true);
        this.saleId = sale.getSaleId();
        this.invoiceText = buildInvoice(sale);
        setSize(560, 620);
        setLocationRelativeTo(owner);

        invoiceArea = new JTextArea(invoiceText);
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JButton print = new JButton("Print");
        print.addActionListener(event -> {
            try {
                invoiceArea.print();
            } catch (PrinterException exception) {
                JOptionPane.showMessageDialog(this, "Could not print: "
                        + exception.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton save = new JButton("Save as text file");
        save.addActionListener(event -> saveInvoice());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(save);
        buttons.add(print);
        setLayout(new BorderLayout());
        add(new JScrollPane(invoiceArea), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    private String buildInvoice(Sale sale) {
        StringBuilder invoice = new StringBuilder();
        invoice.append("RETAIL BILLING SYSTEM\n");
        invoice.append("=====================\n");
        invoice.append("Invoice No: ").append(sale.getSaleId()).append('\n');
        invoice.append("Date: ").append(sale.getSaleDate()).append('\n');
        try {
            BillingService billingService = new BillingService();
            invoice.append("Customer: ").append(
                    billingService.getCustomerName(sale.getCustomerId())).append('\n');
            invoice.append("Items:\n");
            for (SaleItem item : sale.getItems()) {
                invoice.append(String.format("%-20s x%-3d %10s %12s%n",
                        billingService.getProductName(item.getProductId()),
                        item.getQuantity(),
                        Utils.formatMoney(item.getUnitPrice()),
                        Utils.formatMoney(item.getLineAmount())));
            }
        } catch (SQLException exception) {
            invoice.append("Customer ID: ").append(sale.getCustomerId()).append('\n');
            invoice.append("Could not load item names: ").append(exception.getMessage()).append('\n');
        }
        invoice.append("---------------------\n");
        invoice.append("Subtotal: ").append(Utils.formatMoney(sale.getSubtotal())).append('\n');
        invoice.append("Tax (5%): ").append(Utils.formatMoney(sale.getTax())).append('\n');
        invoice.append("TOTAL: ").append(Utils.formatMoney(sale.getTotal())).append('\n');
        invoice.append("=====================\n");
        invoice.append("Thank you for your purchase!\n");
        return invoice.toString();
    }

    private void saveInvoice() {
        File folder = new File("invoices");
        if (!folder.exists() && !folder.mkdirs()) {
            JOptionPane.showMessageDialog(this, "Could not create invoices folder.");
            return;
        }
        File file = new File(folder, "invoice_" + saleId + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(invoiceText);
            JOptionPane.showMessageDialog(this, "Invoice saved to " + file.getPath());
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Could not save invoice: "
                    + exception.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
