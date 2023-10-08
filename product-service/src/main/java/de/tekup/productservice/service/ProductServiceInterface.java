package de.tekup.productservice.service;

import de.tekup.productservice.dto.ProductRequestDTO;
import de.tekup.productservice.dto.ProductResponseDTO;
import de.tekup.productservice.exception.ProductServiceBusinessException;

import java.util.List;

public interface ProductServiceInterface {
    
    List<ProductResponseDTO> getProducts() throws ProductServiceBusinessException;
    
    ProductResponseDTO getProductById(Long id) throws ProductServiceBusinessException;
    
    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);
    
    ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct) throws ProductServiceBusinessException;
    
    void deleteProduct(Long id);
}
