package de.tekup.exception;

public class InventoryNotFoundException extends Exception {
    private static final String DEFAULT_MESSAGE = "Product not found in inventory";
    
    public InventoryNotFoundException(String message) {
        super(message);
    }
    
    public InventoryNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
