package de.tekup.productservice.controller;

import de.tekup.productservice.dto.request.ProductRequest;
import de.tekup.productservice.dto.request.ProductUpdateRequest;
import de.tekup.productservice.dto.response.ApiResponse;
import de.tekup.productservice.dto.response.ProductResponse;
import de.tekup.productservice.service.ProductServiceInterface;
import de.tekup.productservice.util.Mapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductController {
    public static final String SUCCESS = "SUCCESS";
    private final ProductServiceInterface productServiceInterface;
    
    @GetMapping()
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts
            (
                    @RequestParam(required = false, defaultValue = "true") boolean enabled
            ) {
        List<ProductResponse> products = productServiceInterface.getProducts(enabled);
        
        ApiResponse<List<ProductResponse>> responseDTO = ApiResponse
                .<List<ProductResponse>>builder()
                .status(SUCCESS)
                .results(products)
                .build();
        
        log.info("ProductController::getAllProducts response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{skuCode}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductBySkuCode(@PathVariable String skuCode) {
        log.info("ProductController::getProductBySkuCode {}", skuCode);
        ProductResponse productResponse = productServiceInterface.getProductBySkuCode(skuCode);
        
        ApiResponse<ProductResponse> responseDTO = ApiResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(productResponse)
                .build();
        
        log.info("ProductController::getProductBySkuCode {} response {}", skuCode, Mapper.jsonToString(productResponse));
        
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        log.info("ProductController::createProduct request body {}", Mapper.jsonToString(productRequest));
        
        ProductResponse createdProduct = productServiceInterface.createProduct(productRequest);
        
        ApiResponse<ProductResponse> responseDTO = ApiResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(createdProduct)
                .build();
        
        log.info("ProductController::createProduct response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @PutMapping("/{skuCode}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable String skuCode,
            @Valid @RequestBody ProductUpdateRequest productRequest
    ) {
        log.info("ProductController::updateProduct request body {}", Mapper.jsonToString(productRequest));
        
        ProductResponse updatedProduct = productServiceInterface.updateProduct(skuCode, productRequest);
        
        ApiResponse<ProductResponse> responseDTO = ApiResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(updatedProduct)
                .build();
        
        log.info("ProductController::updateProduct response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }
    
    @DeleteMapping("/{skuCode}")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProduct(@PathVariable String skuCode) {
        ProductResponse disabledProduct = productServiceInterface.disableProduct(skuCode);
        
        ApiResponse<ProductResponse> responseDTO = ApiResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(disabledProduct)
                .build();
        
        return ResponseEntity.ok(responseDTO);
    }
}
