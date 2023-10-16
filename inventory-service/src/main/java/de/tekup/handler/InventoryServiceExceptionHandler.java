package de.tekup.handler;

import de.tekup.dto.APIResponse;
import de.tekup.dto.ErrorDTO;
import de.tekup.exception.InventoryAlreadyExistsException;
import de.tekup.exception.InventoryNotEnoughQuantity;
import de.tekup.exception.InventoryNotFoundException;
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

    // Bad Args exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleMethodArgumentException(MethodArgumentNotValidException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        List<ErrorDTO> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    ErrorDTO errorDTO = new ErrorDTO(error.getField(), error.getDefaultMessage());
                    errors.add(errorDTO);
                });
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(errors);
        return serviceResponse;
    }
    
    @ExceptionHandler(InventoryNotEnoughQuantity.class)
    public APIResponse<?> handleServiceException(InventoryNotEnoughQuantity exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(InventoryAlreadyExistsException.class)
    public APIResponse<?> handleServiceException(InventoryAlreadyExistsException exception) {
        return getServiceResponse(exception);
    }
    
    
    @ExceptionHandler(InventoryNotFoundException.class)
    public APIResponse<?> handleCouponAlreadyExistsException(InventoryNotFoundException exception) {
        return getServiceResponse(exception);
    }
    
    
    @ExceptionHandler(InventoryServiceException.class)
    public APIResponse<?> handleCouponNotFoundException(InventoryServiceException exception) {
        return getServiceResponse(exception);
    }
    
    private static APIResponse<?> getServiceResponse(Exception exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        return serviceResponse;
    }
}
