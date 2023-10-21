package de.tekup.orderservice.exception;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException(String message) {
        super(message);
    }
}
