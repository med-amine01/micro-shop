package de.tekup.productservice.service;

import de.tekup.productservice.dto.request.ProductRequest;
import de.tekup.productservice.dto.request.ProductUpdateRequest;
import de.tekup.productservice.dto.response.CouponResponse;
import de.tekup.productservice.dto.response.ProductResponse;
import de.tekup.productservice.exception.ProductServiceBusinessException;

import java.util.List;

public interface ProductServiceInterface {
    
    List<ProductResponse> getProducts(boolean enabled) throws ProductServiceBusinessException;
    
    ProductResponse getProductBySkuCode(String skuCode) throws ProductServiceBusinessException;
    
    ProductResponse createProduct(ProductRequest productRequest) throws ProductServiceBusinessException;
    
    ProductResponse updateProduct(String skuCode, ProductUpdateRequest updatedProduct) throws ProductServiceBusinessException;
    
    ProductResponse disableProduct(String skuCode) throws ProductServiceBusinessException;
    
    void updateProductsFromQueue(CouponResponse couponResponse);
}
