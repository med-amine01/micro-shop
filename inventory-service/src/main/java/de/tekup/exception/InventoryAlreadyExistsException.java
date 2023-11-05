package de.tekup.exception;

public class InventoryAlreadyExistsException extends Exception {
    private static final String DEFAULT_MESSAGE = "Product already exists in inventory";
    
    public InventoryAlreadyExistsException(String message) {
        super(message);
    }
    
    public InventoryAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
