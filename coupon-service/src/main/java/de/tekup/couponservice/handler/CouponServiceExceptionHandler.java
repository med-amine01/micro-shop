package de.tekup.couponservice.handler;

import de.tekup.couponservice.dto.response.ApiResponse;
import de.tekup.couponservice.dto.response.ErrorResponse;
import de.tekup.couponservice.exception.CouponAlreadyExistsException;
import de.tekup.couponservice.exception.CouponNotFoundException;
import de.tekup.couponservice.exception.CouponServiceBusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class CouponServiceExceptionHandler {
    
    private static final String FAILED = "FAILED";
    
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
    
    @ExceptionHandler(CouponAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleCouponAlreadyExistsException(CouponAlreadyExistsException exception) {
        return getServiceResponse(exception);
    }
    
    @ExceptionHandler(CouponNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleCouponNotFoundException(CouponNotFoundException exception) {
        return getServiceResponse(exception);
    }
    
    
    @ExceptionHandler(CouponServiceBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleCouponServiceBusinessException(CouponServiceBusinessException exception) {
        return getServiceResponse(exception);
    }

    private static ApiResponse<?> getServiceResponse(Exception exception) {
        ApiResponse<?> serviceResponse = new ApiResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorResponse("", exception.getMessage())));
        return serviceResponse;
    }
}
