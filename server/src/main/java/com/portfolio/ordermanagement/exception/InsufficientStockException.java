package com.portfolio.ordermanagement.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int requestedQuantity, int availableQuantity) {
        super("Insufficient stock for product: " + productName
                + ". Requested: " + requestedQuantity
                + ", available: " + availableQuantity);
    }

}
