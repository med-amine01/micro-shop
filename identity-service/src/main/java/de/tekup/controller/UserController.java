package de.tekup.controller;

import de.tekup.dto.request.UserRequest;
import de.tekup.dto.response.APIResponse;
import de.tekup.dto.response.UserResponse;
import de.tekup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private static String SUCCESS = "SUCCESS";
    
    private final UserService userService;
    
//    @GetMapping("/")
//    public ResponseEntity<APIResponse<List<UserResponse>>> getUsers() {
//        List<UserResponse> users = userService.findUsers();
//
//        APIResponse<List<UserResponse>> response = APIResponse
//                .<List<UserResponse>>builder()
//                .status(SUCCESS)
//                .results(users)
//                .build();
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
    
    @PostMapping
    public ResponseEntity<APIResponse<UserResponse>> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.saveUser(userRequest);
        
        APIResponse<UserResponse> response = APIResponse
                .<UserResponse>builder()
                .status(SUCCESS)
                .results(userResponse)
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
