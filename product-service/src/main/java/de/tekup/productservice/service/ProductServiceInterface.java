package de.tekup.productservice.service;

import de.tekup.productservice.dto.ProductRequest;
import de.tekup.productservice.dto.ProductResponse;
import de.tekup.productservice.exception.ProductServiceBusinessException;

import java.util.List;

public interface ProductServiceInterface {
    
    List<ProductResponse> getProducts() throws ProductServiceBusinessException;

    ProductResponse getProductBySkuCode(String skuCode) throws ProductServiceBusinessException;

    ProductResponse createProduct(ProductRequest productRequest) throws ProductServiceBusinessException;
    
    ProductResponse updateProduct(String skuCode, ProductRequest updatedProduct) throws ProductServiceBusinessException;
    
    ProductResponse disableProduct(String skuCode) throws ProductServiceBusinessException;
}
