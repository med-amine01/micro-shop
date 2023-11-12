package de.tekup.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.dto.request.InventoryRequest;
import de.tekup.dto.response.ApiResponse;
import de.tekup.dto.response.InventoryResponse;
import de.tekup.entity.Inventory;
import de.tekup.exception.InventoryServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

@Slf4j
public class Mapper {
    private Mapper() {
    
    }
    
    public static Inventory toEntity(InventoryRequest request, String skuCode) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(skuCode);
        inventory.setQuantity(request.getQuantity());
        
        return inventory;
    }
    
    public static InventoryResponse toDto(Inventory inventory) {
        InventoryResponse responseDTO = new InventoryResponse();
        responseDTO.setSkuCode(inventory.getSkuCode());
        responseDTO.setQuantity(inventory.getQuantity());
        responseDTO.setInStock(inventory.getQuantity() > 0);
        responseDTO.setCreatedAt(inventory.getCreatedAt());
        responseDTO.setUpdatedAt(inventory.getUpdatedAt());
        
        return responseDTO;
    }
    
    public static String jsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    public static <T> T getApiResponseData(ResponseEntity<ApiResponse<T>> responseEntity) {
        ApiResponse<T> apiResponse = responseEntity.getBody();
        
        if (apiResponse != null && "FAILED".equals(apiResponse.getStatus())) {
            String errorDetails = apiResponse.getErrors().isEmpty()
                    ? "Unknown error occurred."
                    : apiResponse.getErrors().get(0).getErrorMessage();
            throw new InventoryServiceException(errorDetails);
        }
        
        return apiResponse != null ? apiResponse.getResults() : null;
    }
}
