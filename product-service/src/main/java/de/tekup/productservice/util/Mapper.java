package de.tekup.productservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.productservice.dto.APIResponse;
import de.tekup.productservice.dto.ProductRequest;
import de.tekup.productservice.dto.ProductRequestUpdate;
import de.tekup.productservice.dto.ProductResponse;
import de.tekup.productservice.entity.Product;
import de.tekup.productservice.exception.InvalidResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@Slf4j
public class Mapper {
    
    // this class should not be instantiated
    // All methods are static
    private Mapper() {
    
    }
    
    public static Product toEntity(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setCouponCode(productRequest.getCouponCode());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        
        return product;
    }
    
    public static Product toEntity(ProductRequestUpdate requestUpdate, Product productInDb) {
        Product product = new Product();
        product.setId(productInDb.getId());
        product.setSkuCode(productInDb.getSkuCode());
        
        String name = requestUpdate.getName() != null ? requestUpdate.getName() : productInDb.getName();
        product.setName(name);
        
        // Check if request code is present in update the code in Db ELSE keep the Db
        String couponCode = requestUpdate.getCouponCode() != null ? requestUpdate.getCouponCode() : productInDb.getCouponCode();
        product.setCouponCode(couponCode);
        
        BigDecimal price = requestUpdate.getPrice() != null ? requestUpdate.getPrice() : productInDb.getPrice();
        product.setPrice(price);
        
        // we can distinguish if the requested product contains any field other than enabled =>
        // it's an update from product
        // ELSE it's an update from inventory to set enabled to false or true
        
        if (null == requestUpdate.getName()
                && null == requestUpdate.getPrice()
                && null == requestUpdate.getCouponCode()
        ) {
            product.setEnabled(requestUpdate.isEnabled());
        } else {
            product.setEnabled(productInDb.isEnabled());
        }
        
        // Check if request desc is present in update the desc in Db ELSE keep the Db desc
        String description = requestUpdate.getDescription() != null ? requestUpdate.getDescription() : productInDb.getDescription();
        product.setDescription(description);
        
        return product;
    }
    
    public static ProductResponse toDto(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setSkuCode(product.getSkuCode());
        productResponse.setName(product.getName());
        productResponse.setCouponCode(product.getCouponCode());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setDiscountedPrice(product.getDiscountedPrice());
        productResponse.setEnabled(product.isEnabled());
        
        return productResponse;
    }
    
    public static String jsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    public static <T> T getApiResponseData(ResponseEntity<APIResponse<T>> responseEntity) {
        APIResponse<T> apiResponse = responseEntity.getBody();
        
        if (apiResponse != null && "FAILED".equals(apiResponse.getStatus())) {
            String errorDetails = apiResponse.getErrors().isEmpty()
                    ? "Unknown error occurred."
                    : apiResponse.getErrors().get(0).getErrorMessage();
            throw new InvalidResponseException(errorDetails);
        }
        
        return apiResponse != null ? apiResponse.getResults() : null;
    }
}
