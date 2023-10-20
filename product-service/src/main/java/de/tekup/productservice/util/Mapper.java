package de.tekup.productservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.productservice.dto.ProductRequest;
import de.tekup.productservice.dto.ProductResponse;
import de.tekup.productservice.entity.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper {
    
    // this class should not be instantiated
    // All methods are static
    private Mapper() {
    
    }
    
    public static Product toEntity(ProductRequest productRequest) {
        Product product = new Product();
        product.setSkuCode(productRequest.getSkuCode());
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCouponCode(productRequest.getCouponCode());
        
        return product;
    }
    
    public static ProductResponse toDto(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setSkuCode(product.getSkuCode());
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setCouponCode(product.getCouponCode());
        productResponse.setCreatedAt(product.getCreatedAt());
        productResponse.setUpdatedAt(product.getUpdatedAt());
        
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
}
