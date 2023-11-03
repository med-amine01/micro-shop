package de.tekup.productservice.controller;

import de.tekup.productservice.dto.APIResponse;
import de.tekup.productservice.dto.ProductRequest;
import de.tekup.productservice.dto.ProductRequestUpdate;
import de.tekup.productservice.dto.ProductResponse;
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
    public ResponseEntity<APIResponse<List<ProductResponse>>> getAllProducts
    (
            @RequestParam(required = false, defaultValue = "true") boolean enabled
    )
    {
        List<ProductResponse> products = productServiceInterface.getProducts(enabled);

        APIResponse<List<ProductResponse>> responseDTO = APIResponse
                .<List<ProductResponse>>builder()
                .status(SUCCESS)
                .results(products)
                .build();

        log.info("ProductController::getAllProducts response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{skuCode}")
    public ResponseEntity<APIResponse<ProductResponse>> getProductBySkuCode(@PathVariable String skuCode)
    {
        log.info("ProductController::getProductBySkuCode {}", skuCode);
        ProductResponse productResponse = productServiceInterface.getProductBySkuCode(skuCode);

        APIResponse<ProductResponse> responseDTO = APIResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(productResponse)
                .build();
        
        log.info("ProductController::getProductBySkuCode {} response {}", skuCode, Mapper.jsonToString(productResponse));

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    @PostMapping
    public ResponseEntity<APIResponse<ProductResponse>> createProduct(@RequestBody @Valid ProductRequest productRequest)
    {
        log.info("ProductController::createProduct request body {}", Mapper.jsonToString(productRequest));

        ProductResponse createdProduct = productServiceInterface.createProduct(productRequest);

        APIResponse<ProductResponse> responseDTO = APIResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(createdProduct)
                .build();

        log.info("ProductController::createProduct response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{skuCode}")
    public ResponseEntity<APIResponse<ProductResponse>> updateProduct(
            @PathVariable String skuCode,
            @Valid @RequestBody ProductRequestUpdate productRequest
    )
    {
        log.info("ProductController::updateProduct request body {}", Mapper.jsonToString(productRequest));

        ProductResponse updatedProduct = productServiceInterface.updateProduct(skuCode, productRequest);

        APIResponse<ProductResponse> responseDTO = APIResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(updatedProduct)
                .build();

        log.info("ProductController::updateProduct response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{skuCode}")
    public ResponseEntity<APIResponse<ProductResponse>> deleteProduct(@PathVariable String skuCode)
    {
        ProductResponse disabledProduct = productServiceInterface.disableProduct(skuCode);

        APIResponse<ProductResponse> responseDTO = APIResponse
                .<ProductResponse>builder()
                .status(SUCCESS)
                .results(disabledProduct)
                .build();

        return ResponseEntity.ok(responseDTO);
    }
}
