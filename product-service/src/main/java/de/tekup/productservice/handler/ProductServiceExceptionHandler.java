package de.tekup.productservice.handler;

import de.tekup.productservice.dto.APIResponse;
import de.tekup.productservice.dto.ErrorDTO;
import de.tekup.productservice.exception.InvalidResponseException;
import de.tekup.productservice.exception.ProductAlreadyExistsException;
import de.tekup.productservice.exception.ProductNotFoundException;
import de.tekup.productservice.exception.ProductServiceBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class ProductServiceExceptionHandler {
    
    private static final String FAILED = "FAILED";
    
    private static APIResponse<?> getServiceResponse(Exception exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        return serviceResponse;
    }
    
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
                    ;
                });
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(errors);
        
        return serviceResponse;
    }
    
    // Business Product service exception handler
    @ExceptionHandler(ProductServiceBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleServiceException(ProductServiceBusinessException exception) {
        return getServiceResponse(exception);
    }
    
    // Product Already Exists exception handler
    @ExceptionHandler(ProductAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleProductAlreadyExistsException(ProductAlreadyExistsException exception) {
        return getServiceResponse(exception);
    }
    
    // Product Not Found exception handler
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleProductNotFoundException(ProductNotFoundException exception) {
        return getServiceResponse(exception);
    }
    
    // Business Product service exception handler
    @ExceptionHandler(InvalidResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleMicroserviceInvalidResponseException(InvalidResponseException exception) {
        return getServiceResponse(exception);
    }
}
