package de.tekup.productservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.productservice.dto.ProductRequestDTO;
import de.tekup.productservice.dto.ProductResponseDTO;
import de.tekup.productservice.entity.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValueMapper {
    
    // this class should not be instantiated
    // All methods are static
    private ValueMapper() {
    
    }
    
    public static Product convertToEntity(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        product.setPrice(productRequestDTO.getPrice());
        product.setCouponCode(productRequestDTO.getCouponCode());
        
        return product;
    }
    
    public static ProductResponseDTO convertToProductResponseDto(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setName(product.getName());
        productResponseDTO.setDescription(product.getDescription());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setCouponCode(product.getCouponCode());
        productResponseDTO.setCreatedAt(product.getCreatedAt());
        productResponseDTO.setUpdatedAt(product.getUpdatedAt());
        
        return productResponseDTO;
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
