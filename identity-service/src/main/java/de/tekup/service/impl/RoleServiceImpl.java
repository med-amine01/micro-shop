package de.tekup.service.impl;

import de.tekup.dto.request.RoleRequest;
import de.tekup.dto.response.RoleResponse;
import de.tekup.exception.RoleServiceException;
import de.tekup.repository.RoleRepository;
import de.tekup.service.RoleService;
import de.tekup.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    
    @Override
    public RoleResponse saveRole(RoleRequest roleRequest) throws RoleServiceException {
        try {
            if (roleRepository.existsByRoleName(roleRequest.getRoleName())) {
                throw new RoleServiceException("Role already exists");
            }
            
            return Mapper.roleToRoleResponse(roleRepository.save(Mapper.roleRequestToRole(roleRequest)));
        } catch (RoleServiceException exception) {
            log.error("RoleService::saveRole, " + exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error("RoleService::saveRole, " + exception.getMessage());
            throw new RoleServiceException(exception.getMessage());
        }
    }
    
    @Override
    public List<RoleResponse> findRoles() {
        try {
            return roleRepository
                    .findAll()
                    .stream()
                    .map(Mapper::roleToRoleResponse)
                    .toList();
            
        } catch (Exception exception) {
            log.error("RoleService::findRoles, " + exception.getMessage());
            throw new RoleServiceException(exception.getMessage());
        }
    }
}
