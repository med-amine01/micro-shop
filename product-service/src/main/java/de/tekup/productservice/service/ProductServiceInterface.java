package de.tekup.productservice.service;

import de.tekup.productservice.dto.ProductRequest;
import de.tekup.productservice.dto.ProductResponse;
import de.tekup.productservice.exception.ProductServiceBusinessException;

import java.util.List;

public interface ProductServiceInterface {
    
    List<ProductResponse> getProducts() throws ProductServiceBusinessException;
    
    ProductResponse getProductById(Long id) throws ProductServiceBusinessException;

    ProductResponse getProductBySkuCode(String skuCode) throws ProductServiceBusinessException;
    
    ProductResponse createProduct(ProductRequest productRequest);
    
    ProductResponse updateProduct(Long id, ProductRequest updatedProduct) throws ProductServiceBusinessException;
    
    void deleteProduct(Long id);
}
