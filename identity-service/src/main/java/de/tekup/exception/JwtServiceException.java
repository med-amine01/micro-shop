package de.tekup.exception;

public class JwtServiceException extends RuntimeException {
    public JwtServiceException(String message) {
        super(message);
    }
}
