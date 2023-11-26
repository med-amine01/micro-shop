package de.tekup.config;

import de.tekup.exception.JwtServiceException;
import de.tekup.handler.IdentityServiceHandler;
import de.tekup.service.JwtService;
import de.tekup.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private WebSecurityProperties webSecurityProperties;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        List<String> allowedRoutes = new ArrayList<>();
        allowedRoutes.addAll(Arrays.asList(webSecurityProperties.getAllowedGetRoutes()));
        allowedRoutes.addAll(Arrays.asList(webSecurityProperties.getAllowedPostRoutes()));
        
        boolean hasAllowedRoutes = allowedRoutes.stream()
                .anyMatch(allowedRoute -> requestURI.contains(allowedRoute.replace("/**", "")));
        
        if (hasAllowedRoutes) {
            filterChain.doFilter(request, response);
            return;
        }
        
        final String requestTokenHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwtToken = null;
        
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.getUsernameFromToken(jwtToken);
                
            } catch (IllegalArgumentException e) {
                IdentityServiceHandler.handleOutsideScopeException(response,e);
                return;
            } catch (ExpiredJwtException e) {
                IdentityServiceHandler.handleOutsideScopeException(response,e);
                return;
            }
        } else {
            IdentityServiceHandler.handleOutsideScopeException(response,
                    new JwtServiceException("JWT token does not start with Bearer")
            );
            return;
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            UserDetails userDetails = jwtService.loadUserByUsername(username);
            
            if (jwtUtil.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
