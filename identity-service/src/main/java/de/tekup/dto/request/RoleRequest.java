package de.tekup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
    
    @Pattern(regexp = "^ROLE_[a-zA-Z0-9]+$", message = "Role must start with ROLE_ and be in uppercase")
    private String roleName;
}
