package de.tekup.handler;

import de.tekup.dto.response.ApiResponse;
import de.tekup.dto.response.ErrorResponse;
import de.tekup.exception.*;
import de.tekup.util.Mapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class IdentityServiceHandler {
    
    private static final String FAILED = "FAILED";
    
    public static void handleOutsideScopeException(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        
        response.getWriter().write(Objects.requireNonNull(Mapper.jsonToString(getServiceResponse(exception))));
    }
    
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
    
    // Authority Service Exception handler
    @ExceptionHandler(AuthorityServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleRoleServiceException(AuthorityServiceException exception) {
        return getServiceResponse(exception);
    }
    
    // Role Service Exception handler
    @ExceptionHandler(RoleServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleRoleServiceException(RoleServiceException exception) {
        return getServiceResponse(exception);
    }
    
    // User Service Exception handler
    @ExceptionHandler(UserServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleUserServiceException(UserServiceException exception) {
        return getServiceResponse(exception);
    }
    
    // Invalid response exception handler
    @ExceptionHandler(InvalidResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleInvalidResponseException(InvalidResponseException exception) {
        return getServiceResponse(exception);
    }
    
    // Invalid jwt service exception handler
    @ExceptionHandler(JwtServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleJwtServiceException(JwtServiceException exception) {
        return getServiceResponse(exception);
    }
    
    // Bad credentials exception handler
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadCredentialsException(BadCredentialsException exception) {
        return getServiceResponse(exception);
    }
    
    // Bad credentials exception handler
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleBadCredentialsException(UsernameNotFoundException exception) {
        return getServiceResponse(exception);
    }
}
