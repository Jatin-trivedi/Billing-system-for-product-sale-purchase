package com.retail.threads;

import com.retail.model.Sale;
import com.retail.service.BillingService;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

// Unit IX: Runnable is executed by a background Thread.
public class ReportTask implements Runnable {
    private final Consumer<String> statusCallback;
    private static final Object EXPORT_LOCK = new Object();

    public ReportTask(Consumer<String> statusCallback) {
        this.statusCallback = statusCallback;
    }

    @Override
    public void run() {
        synchronized (EXPORT_LOCK) {
            // synchronized prevents two report threads writing the report file together.
            try {
                updateStatus("Preparing sales report...");
                Thread.sleep(700);

                BillingService billingService = new BillingService();
                List<Sale> sales = billingService.getAllSales();
                updateStatus("Writing report...");

                File folder = new File("reports");
                if (!folder.exists() && !folder.mkdirs()) {
                    updateStatus("Could not create reports folder.");
                    return;
                }
                File reportFile = new File(folder, "sales_report.txt");
                try (BufferedWriter writer =
                             new BufferedWriter(new FileWriter(reportFile))) {
                    writer.write("SALES REPORT\n");
                    writer.write("============\n");
                    for (Sale sale : sales) {
                        writer.write("Sale #" + sale.getSaleId()
                                + " | Date: " + sale.getSaleDate()
                                + " | Customer ID: " + sale.getCustomerId()
                                + " | Total: " + sale.getTotal() + "\n");
                    }
                    Thread.sleep(700);
                }
                updateStatus("Report exported: " + reportFile.getPath());
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                updateStatus("Report export was interrupted.");
            } catch (SQLException | IOException exception) {
                updateStatus("Report export failed: " + exception.getMessage());
            }
        }
    }

    private void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusCallback.accept(status));
    }
}
