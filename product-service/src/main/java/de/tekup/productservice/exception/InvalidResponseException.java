package de.tekup.productservice.exception;

public class InvalidResponseException extends RuntimeException {
    public InvalidResponseException(String message) {
        super(message);
    }
}
