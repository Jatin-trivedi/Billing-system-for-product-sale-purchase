package com.retail.exceptions;

public class InsufficientStockException extends Exception {
    public InsufficientStockException(String productName, int available, int requested) {
        super("Insufficient stock for " + productName + ": available=" + available
                + ", requested=" + requested);
    }
}
