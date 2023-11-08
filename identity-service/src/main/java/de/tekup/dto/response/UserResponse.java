package de.tekup.dto.response;

import de.tekup.entity.Role;
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
