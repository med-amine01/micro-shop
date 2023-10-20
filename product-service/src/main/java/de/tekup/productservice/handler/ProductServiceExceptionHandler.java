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

    // Bad Args exception handler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<?> handleMethodArgumentException(MethodArgumentNotValidException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        List<ErrorDTO> errors = new ArrayList<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    ErrorDTO errorDTO = new ErrorDTO(error.getField(), error.getDefaultMessage());
                    errors.add(errorDTO);;
                });
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(errors);
        
        return serviceResponse;
    }
    
    // Business Product service exception handler
    @ExceptionHandler(ProductServiceBusinessException.class)
    public APIResponse<?> handleServiceException(ProductServiceBusinessException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        
        return serviceResponse;
    }
    
    // Product Already Exists exception handler
    @ExceptionHandler(ProductAlreadyExistsException.class)
    public APIResponse<?> handleProductAlreadyExistsException(ProductAlreadyExistsException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        
        return serviceResponse;
    }
    
    // Product Not Found exception handler
    @ExceptionHandler(ProductNotFoundException.class)
    public APIResponse<?> handleProductNotFoundException(ProductNotFoundException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        
        return serviceResponse;
    }
    
    // Business Product service exception handler
    @ExceptionHandler(InvalidResponseException.class)
    public APIResponse<?> handleMicroserviceInvalidResponseException(InvalidResponseException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        
        return serviceResponse;
    }
}
