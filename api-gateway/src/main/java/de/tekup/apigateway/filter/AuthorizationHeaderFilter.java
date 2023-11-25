package de.tekup.apigateway.filter;

import de.tekup.apigateway.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;
    
    public AuthorizationHeaderFilter() {
        super(Config.class);
    }
    
    // This class will match the args from
    public static class Config {
        // Put configuration properties here
        
        // When we pass list of properties from application.yml filter separated by space eg: ROLE_ADMIN READ
        // It's considered as a single string like this : "ROLE_ADMIN READ"
        private List<String> authorities;
        
        public List<String> getAuthorities() {
            return authorities;
        }
        
        public void setAuthorities(String authorities) {
            this.authorities = Arrays.asList(authorities.split(" "));
        }
// When we pass properties from application.yml filter separated by comma eg: ROLE_ADMIN,READ
// It's considered as keys (which is match the config class attributes (role, authority))
//        private String role;
//        private String authority;
//
//        public String getRole() {
//            return role;
//        }
//
//        public void setRole(String role) {
//            this.role = role;
//        }
//
//        public String getAuthority() {
//            return authority;
//        }
//
//        public void setAuthority(String authority) {
//            this.authority = authority;
//        }
    }
    
    @Override
    public List<String> shortcutFieldOrder() {
        // This will get list of args passed to filter (application.yml)
        // It could be more than one arg, so we return a list of role (reference to static class Config)
        return Arrays.asList("authorities");
    }
    
//    @Override
//    public List<String> shortcutFieldOrder() {
//        // This will get list of args passed to filter (application.yml)
//        // It could be more than one arg, so we return a list of role (reference to static class Config)
//        return Arrays.asList("role","authority");
//    }
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }
            
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (!authorizationHeader.startsWith("Bearer ")) {
                return onError(exchange, "Jwt token doesn't start with Bearer", HttpStatus.UNAUTHORIZED);
            }
            
            String jwt = authorizationHeader.replace("Bearer", "").trim();
            
            try {
                if (!jwtUtil.validateToken(jwt)) {
                    return onError(exchange, "Jwt token not valid", HttpStatus.UNAUTHORIZED);
                }
                
                if (!hasRequiredAuthority(jwtUtil.getAuthorities(jwt), config.getAuthorities())) {
                    return onError(exchange, "User is not allowed to perform this operation", HttpStatus.FORBIDDEN);
                }
                
            } catch (ExpiredJwtException e) {
                log.error(e.getMessage());
                return onError(exchange, "Jwt token expired", HttpStatus.UNAUTHORIZED);
            } catch (MalformedJwtException e) {
                log.error(e.getMessage());
                return onError(exchange, "Bad jwt token format", HttpStatus.UNAUTHORIZED);
            } catch (SignatureException e) {
                log.error(e.getMessage());
                return onError(exchange, "Bad jwt token signature", HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                log.error(e.getMessage());
                return onError(exchange, "Jwt token not valid : " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            
            return chain.filter(exchange);
        };
    }
    
    private boolean hasRequiredAuthority(List<String> tokenAuthorities, List<String> filterAuthorities) {
        return IntStream.range(0, filterAuthorities.size() / 2)
                .anyMatch(i -> {
                    String role = filterAuthorities.get(i * 2);
                    if (tokenAuthorities.contains(role)) {
                        List<String> requiredAuthorities = Arrays.asList(filterAuthorities.get(i * 2 + 1).split(","));
                        return requiredAuthorities.isEmpty() || tokenAuthorities.containsAll(requiredAuthorities);
                    }
                    return false;
                });
    }

    
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // Create a JSON response body with the error message
        String responseBody = "{\"error\": \"" + err + "\"}";
        
        // Write the JSON response body to the response
        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBody.getBytes())));
    }
    
}
