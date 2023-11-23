package de.tekup.service.impl;

import de.tekup.dto.request.RoleRequest;
import de.tekup.dto.request.UserRequest;
import de.tekup.dto.response.UserResponse;
import de.tekup.entity.Role;
import de.tekup.entity.User;
import de.tekup.enums.Roles;
import de.tekup.exception.RoleServiceException;
import de.tekup.exception.UserServiceException;
import de.tekup.repository.RoleRepository;
import de.tekup.repository.UserRepository;
import de.tekup.service.UserService;
import de.tekup.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    
    @Override
    public List<UserResponse> findUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            return users
                    .stream()
                    .map(Mapper::userToUserResponse)
                    .toList();
        } catch (Exception exception) {
            log.error("UserService::findUsers " + exception.getMessage());
            throw new UserServiceException(exception.getMessage());
        }
    }
    
    @Override
    public UserResponse findUserByUsername(String username) {
        try {
            User user = userRepository.findByName(username)
                    .orElseThrow(() -> new UserServiceException("User not found :: " + username));
            
            return Mapper.userToUserResponse(user);
        } catch (Exception exception) {
            log.error("UserService::findUserByUsername " + exception.getMessage());
            throw new UserServiceException(exception.getMessage());
        }
    }
    
    @Override
    public UserResponse saveUser(UserRequest userRequest) {
        try {
            if (userRepository.existsByName(userRequest.getUsername())) {
                throw new UserServiceException("User already exists");
            }
            
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                throw new UserServiceException("User email already exists");
            }
            
            // Encrypt password
            userRequest.setUserPassword(passwordEncoder.encode(userRequest.getUserPassword()));
            
            // Map user
            User user = Mapper.userRequestToUser(userRequest);
            
            // Set user roles (if not provided, assign USER_ROLE by default)
            user.setRoles(getRoles(userRequest));
            
            return Mapper.userToUserResponse(userRepository.save(user));
        } catch (Exception exception) {
            log.error("UserService::saveUser " + exception.getMessage());
            throw new UserServiceException(exception.getMessage());
        }
    }
    
    private Set<Role> getRoles(UserRequest userRequest) {
        Set<RoleRequest> userRequestRoles = userRequest.getRoles();
        
        // Use default role USER_ROLE from roleRepository if no roles are provided
        if (userRequestRoles == null || userRequestRoles.isEmpty()) {
            Role defaultRole = roleRepository.findByRoleName(Roles.ROLE_USER.name());
            if (defaultRole == null) {
                throw new RoleServiceException("Default role ROLE_USER not found");
            }
            return Collections.singleton(defaultRole);
        }
        
        Set<Role> persistedRoles = new HashSet<>();
        userRequestRoles.forEach(role -> {
            Role r = roleRepository.findByRoleName(role.getRoleName());
            if (r == null) {
                throw new RoleServiceException("Role not found: " + role.getRoleName());
            }
            persistedRoles.add(r);
        });
        
        return persistedRoles;
    }
}
