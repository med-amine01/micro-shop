package de.tekup.service;

import de.tekup.dto.request.RoleRequest;
import de.tekup.dto.response.RoleResponse;
import de.tekup.exception.RoleServiceException;

import java.util.List;

public interface RoleService {
    RoleResponse saveRole(RoleRequest role) throws RoleServiceException;
    List<RoleResponse> findRoles();
}
