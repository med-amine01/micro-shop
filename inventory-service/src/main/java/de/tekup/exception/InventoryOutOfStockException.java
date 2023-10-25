package de.tekup.exception;

public class InventoryOutOfStockException extends Exception {
    private static final String DEFAULT_MESSAGE = "Quantity is out of stock";
    
    public InventoryOutOfStockException(String message) {
        super(message);
    }
    
    public InventoryOutOfStockException() {
        super(DEFAULT_MESSAGE);
    }
}
