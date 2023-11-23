package de.tekup.controller;

import de.tekup.dto.request.UserRequest;
import de.tekup.dto.response.ApiResponse;
import de.tekup.dto.response.UserResponse;
import de.tekup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private static String SUCCESS = "SUCCESS";
    
    private final UserService userService;
    
    
    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        UserResponse userResponse = userService.findUserByUsername(username);
        
        ApiResponse<UserResponse> response = ApiResponse
                .<UserResponse>builder()
                .status(SUCCESS)
                .results(userResponse)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        List<UserResponse> users = userService.findUsers();
        
        ApiResponse<List<UserResponse>> response = ApiResponse
                .<List<UserResponse>>builder()
                .status(SUCCESS)
                .results(users)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.saveUser(userRequest);
        
        ApiResponse<UserResponse> response = ApiResponse
                .<UserResponse>builder()
                .status(SUCCESS)
                .results(userResponse)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
