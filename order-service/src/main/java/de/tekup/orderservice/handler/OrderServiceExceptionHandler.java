package de.tekup.orderservice.handler;


import de.tekup.orderservice.dto.APIResponse;
import de.tekup.orderservice.dto.ErrorDTO;
import de.tekup.orderservice.exception.InvalidResponseException;
import de.tekup.orderservice.exception.OrderServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class OrderServiceExceptionHandler {

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
    
    @ExceptionHandler(InvalidResponseException.class)
    public APIResponse<?> handleCouponNotFoundException(InvalidResponseException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(OrderServiceException.class)
    public APIResponse<?> handleCouponNotFoundException(OrderServiceException exception) {
        return getServiceResponse(exception);
    }
    
    private static APIResponse<?> getServiceResponse(Exception exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        return serviceResponse;
    }
}
