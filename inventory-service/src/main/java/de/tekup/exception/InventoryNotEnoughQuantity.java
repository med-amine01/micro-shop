package de.tekup.exception;

public class InventoryNotEnoughQuantity extends Exception {
    public InventoryNotEnoughQuantity(String messsage) {
        super(messsage);
    }
}
