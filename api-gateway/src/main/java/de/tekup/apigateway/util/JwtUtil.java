package de.tekup.apigateway.util;

import de.tekup.apigateway.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {
    
    @Value("${secret.key}")
    private String secretKey;
    
    @Autowired
    private UserService userService;
    
    public List<String> getAuthorities(String jwt) {
        List<String> authorities = new ArrayList<>();
        
        try {
            List<Map<String, String>> scopes = getAllClaimsFromToken(jwt).get("scope", List.class);
            scopes.stream().map(mappedScopes -> authorities.add(mappedScopes.get("authority"))).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error(e.getMessage());
            return authorities;
        }
        
        return authorities;
    }
    
    public Boolean validateToken(String token) {
        final String username = getUsernameFromToken(token);
        return null != userService.getUser(username) && !isTokenExpired(token);
    }
    
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
}