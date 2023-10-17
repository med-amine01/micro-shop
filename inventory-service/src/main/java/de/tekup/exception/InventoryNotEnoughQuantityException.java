package de.tekup.exception;

public class InventoryNotEnoughQuantityException extends Exception {
    public InventoryNotEnoughQuantityException(String messsage) {
        super(messsage);
    }
}
