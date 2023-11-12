package de.tekup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    
    private String userName;
    
    private String email;
    
    private Set<RoleResponse> roles;
}
