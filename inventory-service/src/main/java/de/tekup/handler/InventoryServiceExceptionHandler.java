package de.tekup.handler;

import de.tekup.dto.response.ApiResponse;
import de.tekup.dto.response.ErrorResponse;
import de.tekup.exception.InventoryAlreadyExistsException;
import de.tekup.exception.InventoryNotFoundException;
import de.tekup.exception.InventoryOutOfStockException;
import de.tekup.exception.InventoryServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class InventoryServiceExceptionHandler {
    
    private static final String FAILED = "FAILED";
    
    private static ApiResponse<?> getServiceResponse(Exception exception) {
        ApiResponse<?> serviceResponse = new ApiResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorResponse("", exception.getMessage())));
        return serviceResponse;
    }
    
    // Bad Args exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgumentException(MethodArgumentNotValidException exception) {
        ApiResponse<?> serviceResponse = new ApiResponse<>();
        List<ErrorResponse> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    ErrorResponse errorResponse = new ErrorResponse(error.getField(), error.getDefaultMessage());
                    errors.add(errorResponse);
                });
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(errors);
        return serviceResponse;
    }
    
    @ExceptionHandler(InventoryServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInventoryServiceException(InventoryServiceException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(InventoryOutOfStockException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInventoryOutOfStockException(InventoryOutOfStockException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(InventoryAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInventoryAlreadyExistsException(InventoryAlreadyExistsException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(InventoryNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInventoryNotFoundException(InventoryNotFoundException exception) {
        return getServiceResponse(exception);
    }
}
