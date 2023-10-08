package de.tekup.couponservice.handler;

import de.tekup.couponservice.dto.APIResponse;
import de.tekup.couponservice.dto.ErrorDTO;
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

    // Business Coupon service exception handler
    @ExceptionHandler(CouponServiceBusinessException.class)
    public APIResponse<?> handleServiceException(CouponServiceBusinessException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        return serviceResponse;
    }

    // Coupon already exists exception handler
    @ExceptionHandler(CouponAlreadyExistsException.class)
    public APIResponse<?> handleCouponAlreadyExistsException(CouponAlreadyExistsException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        return serviceResponse;
    }

    // Coupon Not Found exception handler
    @ExceptionHandler(CouponNotFoundException.class)
    public APIResponse<?> handleCouponNotFoundException(CouponNotFoundException exception) {
        APIResponse<?> serviceResponse = new APIResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorDTO("", exception.getMessage())));
        return serviceResponse;
    }
}
