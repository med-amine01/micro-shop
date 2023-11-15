package de.tekup.apigateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import java.security.Key;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    
    @Value("${secret.key}")
    private String secretKey;
    
    public AuthorizationHeaderFilter() {
        super(Config.class);
    }
    
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
                validateJwt(jwt);
            } catch (Exception e) {
                log.error(e.getMessage());
                return onError(exchange, "Jwt token not valid", HttpStatus.UNAUTHORIZED);
            }
            
            return chain.filter(exchange);
        };
    }
    
    
    private void validateJwt(final String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }
    
    
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
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
    
    public static class Config {
        // Put configuration properties here
    }
}
