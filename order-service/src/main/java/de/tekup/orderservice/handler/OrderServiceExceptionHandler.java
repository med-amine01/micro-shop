package de.tekup.orderservice.handler;


import de.tekup.orderservice.dto.response.ApiResponse;
import de.tekup.orderservice.dto.response.ErrorResponse;
import de.tekup.orderservice.exception.InvalidRequestException;
import de.tekup.orderservice.exception.InvalidResponseException;
import de.tekup.orderservice.exception.OrderNotFoundException;
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
    
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleOrderNotFoundException(OrderNotFoundException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInvalidRequestException(InvalidRequestException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(InvalidResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInvalidResponseException(InvalidResponseException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(OrderServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleOrderServiceException(OrderServiceException exception) {
        return getServiceResponse(exception);
    }
}
