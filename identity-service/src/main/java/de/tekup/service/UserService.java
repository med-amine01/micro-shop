package de.tekup.service;

import de.tekup.dto.request.UserRequest;
import de.tekup.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    
    List<UserResponse> findUsers();
    UserResponse saveUser(UserRequest userRequest);
    UserResponse findUserByUsername(String username);
}

