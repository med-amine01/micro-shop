package de.tekup.service;

import de.tekup.dto.request.AuthRequest;
import de.tekup.dto.response.AuthResponse;
import de.tekup.entity.User;
import de.tekup.repository.UserRepository;
import de.tekup.util.JwtUtil;
import de.tekup.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Component
@Service
@Slf4j
public class JwtService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository
                    .findByName(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            
            return new org.springframework.security.core.userdetails.User(
                    user.getName(),
                    user.getPassword(),
                    getAuthorities(user)
            );
        } catch (UsernameNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }
    
    //this will take jwt request, and it will process it to get jwt response
    public AuthResponse generateJwtToken(AuthRequest authRequest) throws Exception {
        String userName = authRequest.getUsername();
        String userPassword = authRequest.getUserPassword();
        
        //check for authentication to provide jwt Token
        authenticate(userName, userPassword);
        
        final UserDetails userDetails = loadUserByUsername(userName);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);
        
        return Mapper.tokenToAuthResponse(newGeneratedToken);
    }
    
    private Collection<GrantedAuthority> getAuthorities(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
            role.getAuthorities().forEach(authority -> {
                authorities.add(new SimpleGrantedAuthority(authority.getName()));
            });
        });
        
        return authorities;
    }
    
    private void authenticate(String userName, String userPassword) throws Exception {
        // With this approach will have to handle two exceptions :
        // The user is disabled
        // The user gave bad credentials
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
            
        } catch (BadCredentialsException e) {
            log.error(e.getMessage());
            throw new de.tekup.exception.BadCredentialsException("Bad Credentials");
        } catch (DisabledException e) {
            log.error(e.getMessage());
            throw new DisabledException("User is disabled");
        }
    }
}
