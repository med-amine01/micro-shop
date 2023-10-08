package de.tekup.productservice.exception;

public class MicroserviceInvalidResponseException extends RuntimeException {
    public MicroserviceInvalidResponseException(String message) {
        super(message);
    }
}
