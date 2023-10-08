package de.tekup.productservice.controller;

import de.tekup.productservice.dto.APIResponse;
import de.tekup.productservice.dto.ProductRequestDTO;
import de.tekup.productservice.dto.ProductResponseDTO;
import de.tekup.productservice.service.ProductServiceInterface;
import de.tekup.productservice.util.ValueMapper;
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
    
    @GetMapping
    public ResponseEntity<APIResponse<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productServiceInterface.getProducts();
        
        APIResponse<List<ProductResponseDTO>> responseDTO = APIResponse
                .<List<ProductResponseDTO>>builder()
                .status(SUCCESS)
                .results(products)
                .build();
        
        log.info("ProductController::getAllProducts response {}", ValueMapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        log.info("ProductController::getProductById {}", id);
        ProductResponseDTO productResponseDTO = productServiceInterface.getProductById(id);
        
        APIResponse<ProductResponseDTO> responseDTO = APIResponse
                .<ProductResponseDTO>builder()
                .status(SUCCESS)
                .results(productResponseDTO)
                .build();
        
        log.info("ProductController::getProductById {} response {}", id, ValueMapper.jsonToString(productResponseDTO));
        
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<ProductResponseDTO>> createProduct(@RequestBody @Valid ProductRequestDTO productRequestDTO) {
        log.info("ProductController::createProduct request body {}", ValueMapper.jsonToString(productRequestDTO));
        
        ProductResponseDTO createdProduct = productServiceInterface.createProduct(productRequestDTO);
        
        APIResponse<ProductResponseDTO> responseDTO = APIResponse
                .<ProductResponseDTO>builder()
                .status(SUCCESS)
                .results(createdProduct)
                .build();
        
        log.info("ProductController::createProduct response {}", ValueMapper.jsonToString(responseDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO
    ) {
        log.info("ProductController::updateProduct request body {}", ValueMapper.jsonToString(productRequestDTO));
        
        ProductResponseDTO updatedProduct = productServiceInterface.updateProduct(id, productRequestDTO);
        
        APIResponse<ProductResponseDTO> responseDTO = APIResponse
                .<ProductResponseDTO>builder()
                .status(SUCCESS)
                .results(updatedProduct)
                .build();
        
        log.info("ProductController::updateProduct response {}", ValueMapper.jsonToString(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productServiceInterface.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
