package de.tekup.controller;

import de.tekup.dto.request.RoleRequest;
import de.tekup.dto.response.APIResponse;
import de.tekup.dto.response.RoleResponse;
import de.tekup.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    
    public static final String SUCCESS = "SUCCESS";
    
    private final RoleService roleService;
    
    @GetMapping
    public ResponseEntity<APIResponse<List<RoleResponse>>> getRoles() {
        
        APIResponse<List<RoleResponse>> response = APIResponse
                .<List<RoleResponse>>builder()
                .status(SUCCESS)
                .results(roleService.findRoles())
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<RoleResponse>> createRole(@RequestBody @Valid RoleRequest roleRequest) {
        APIResponse<RoleResponse> response = APIResponse
                .<RoleResponse>builder()
                .status(SUCCESS)
                .results(roleService.saveRole(roleRequest))
                .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
