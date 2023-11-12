package de.tekup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserRequest {

    @NotNull
    @Size(min = 2, message = "username should be at least two characters")
    private String username;
    
    @NotNull
    private String userPassword;
    
    @Email
    private String email;
    
    private Set<RoleRequest> roles;
}
