package com.retail;

import com.retail.model.Product;
import com.retail.util.Utils;

import java.math.BigDecimal;

// Small console demo for viva syllabus units III and method/utility concepts.
public class DemoConcepts {
    public static void main(String[] args) {
        // Wrapper class and autoboxing
        Integer stock = 10;
        int stockValue = stock; // unboxing
        System.out.println("Wrapper/autoboxing stock: " + stock + ", unboxed: " + stockValue);

        // Operators
        int sold = 2;
        int remaining = stockValue - sold;
        boolean inStock = remaining > 0;
        System.out.println("Operator demo: remaining=" + remaining + ", inStock=" + inStock);

        // Method overloading
        System.out.println(Utils.formatMoney(new BigDecimal("125.50")));
        System.out.println(Utils.formatMoney(new BigDecimal("125.5")));

        // Static method
        System.out.println("Static utility: " + Utils.formatMoney(new BigDecimal("99")));

        // Unit III array demonstration
        Product[] products = {
                new Product("Pen", new BigDecimal("12.00")),
                new Product("Notebook", new BigDecimal("30.00")),
                new Product("Bottle", new BigDecimal("220.00"))
        };
        System.out.println("Product array:");
        for (Product product : products) {
            System.out.println(product);
        }

        // String methods and StringBuffer
        String name = "Retail Billing";
        System.out.println("String demo: " + name.toUpperCase()
                + ", length=" + name.length());
        StringBuffer buffer = new StringBuffer("Invoice");
        buffer.append(" - ").append("Paid");
        System.out.println("StringBuffer demo: " + buffer);
    }
}
