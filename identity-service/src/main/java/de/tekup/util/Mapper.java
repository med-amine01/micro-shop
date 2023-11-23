package de.tekup.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.dto.request.RoleRequest;
import de.tekup.dto.request.UserRequest;
import de.tekup.dto.response.AuthResponse;
import de.tekup.dto.response.RoleResponse;
import de.tekup.dto.response.UserResponse;
import de.tekup.entity.Role;
import de.tekup.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class Mapper {
    
    // this class should not be instantiated
    // All methods are static
    private Mapper() {
    
    }
    
    public static RoleResponse roleToRoleResponse(Role role) {
        RoleResponse roleResponse = new RoleResponse();
        roleResponse.setRoleName(role.getRoleName());
        roleResponse.setCreatedAt(role.getCreatedAt());
        List<String> authorities = new ArrayList<>();
        role.getAuthorities().forEach(authority -> authorities.add(authority.getName()));
        roleResponse.setAuthorities(authorities);
        
        return roleResponse;
    }
    
    public static Role roleRequestToRole(RoleRequest roleRequest) {
        Role role = new Role();
        role.setRoleName(roleRequest.getRoleName());
        role.setAuthorities(roleRequest.getAuthorities());
        
        return role;
    }
    
    public static UserResponse userToUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setUserName(user.getName());
        Set<RoleResponse> roleResponses = user
                .getRoles()
                .stream()
                .map(Mapper::roleToRoleResponse)
                .collect(Collectors.toSet());
        userResponse.setRoles(roleResponses);
        
        return userResponse;
    }
    
    public static User userRequestToUser(UserRequest userRequest) {
        User user = new User();
        user.setName(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getUserPassword());
        // Get set roleRequest and map it to role (entity)
        Set<RoleRequest> roleRequests = userRequest.getRoles();
        Set<Role> roles = null;

        if (null != roleRequests) {
            roles = roleRequests.stream()
                    .map(Mapper::roleRequestToRole)
                    .collect(Collectors.toSet());
        }

        user.setRoles(roles);

        return user;
    }
    
    public static AuthResponse tokenToAuthResponse(String token) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(token);
        
        return authResponse;
    }
    
    public static String jsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
