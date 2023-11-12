package de.tekup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AuthRequest {
    
    @Size(min=2, message = "no such username")
    @NotNull(message = "username is required")
    private String username;
    
    @Size(min=2, message = "no such password")
    @NotNull(message = "password is required")
    private String userPassword;
}
