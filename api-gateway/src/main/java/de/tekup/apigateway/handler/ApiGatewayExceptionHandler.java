package de.tekup.apigateway.handler;

import de.tekup.apigateway.dto.response.ApiResponse;
import de.tekup.apigateway.dto.response.ErrorResponse;
import de.tekup.apigateway.exception.InvalidResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class ApiGatewayExceptionHandler {
    
    private static final String FAILED = "FAILED";
    
    private static ApiResponse<?> getServiceResponse(Exception exception) {
        ApiResponse<?> serviceResponse = new ApiResponse<>();
        serviceResponse.setStatus(FAILED);
        serviceResponse.setErrors(Collections.singletonList(new ErrorResponse("", exception.getMessage())));
        return serviceResponse;
    }
    
    // Business Product service exception handler
    @ExceptionHandler(InvalidResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMicroserviceInvalidResponseException(InvalidResponseException exception) {
        return getServiceResponse(exception);
    }
}
