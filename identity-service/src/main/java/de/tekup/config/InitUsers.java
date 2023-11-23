package de.tekup.config;

import de.tekup.entity.Authority;
import de.tekup.entity.Role;
import de.tekup.entity.User;
import de.tekup.enums.Authorities;
import de.tekup.enums.Roles;
import de.tekup.repository.AuthorityRepository;
import de.tekup.repository.RoleRepository;
import de.tekup.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitUsers {
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Authority readAuthority = createAuthority(Authorities.READ.name());
        Authority writeAuthority = createAuthority(Authorities.WRITE.name());
        Authority deleteAuthority = createAuthority(Authorities.DELETE.name());
        
        createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority));
        Role roleAdmin = createRole(Roles.ROLE_ADMIN.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
        
        if (null == roleAdmin) return;
        
        User admin = new User();
        admin.setName("admin-01");
        admin.setPassword(passwordEncoder.encode("test"));
        admin.setEmail("mad.chicken211@gmail.com");
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(roleAdmin);
        admin.setRoles(adminRoles);
        if (userRepository.existsByName(admin.getName())) return;
        
        userRepository.save(admin);
    }
    
    @Transactional
    public Authority createAuthority(String name) {
        Authority authority = authorityRepository.findByName(name);
        
        if (null == authority) {
            authority = new Authority();
            authority.setName(name);
            return authorityRepository.save(authority);
        }
        
        return authority;
    }
    
    @Transactional
    public Role createRole(String name, Collection<Authority> authorities) {
        Role role = roleRepository.findByRoleName(name);
        
        if (null == role) {
            role = new Role();
            role.setRoleName(name);
            role.setAuthorities(authorities);
            return roleRepository.save(role);
        }
        
        return role;
    }
}
