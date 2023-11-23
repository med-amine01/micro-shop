package de.tekup.apigateway.service;

import de.tekup.apigateway.config.RestTemplateConfig;
import de.tekup.apigateway.dto.response.ApiResponse;
import de.tekup.apigateway.dto.response.UserResponse;
import de.tekup.apigateway.exception.InvalidResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final RestTemplateConfig restTemplate;
    
    @Value("${microservices.identity-service.user.uri}")
    private String userIdentityServiceUri;
    
    public Mono<UserResponse> getUser(String username) {
        return Mono.fromCallable(() -> {
            ResponseEntity<ApiResponse<UserResponse>> responseEntity = restTemplate
                    .getRestTemplate()
                    .exchange(userIdentityServiceUri + "/" + username,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                    });

            return getApiResponseData(responseEntity);
        });
    }
    
    public static <T> T getApiResponseData(ResponseEntity<ApiResponse<T>> responseEntity) {
        ApiResponse<T> apiResponse = responseEntity.getBody();
        
        if (apiResponse != null && "FAILED".equals(apiResponse.getStatus())) {
            String errorDetails = apiResponse.getErrors().isEmpty()
                    ? "Unknown error occurred."
                    : apiResponse.getErrors().get(0).getErrorMessage();
            throw new InvalidResponseException(errorDetails);
        }
        return apiResponse != null ? apiResponse.getResults() : null;
    }
}
