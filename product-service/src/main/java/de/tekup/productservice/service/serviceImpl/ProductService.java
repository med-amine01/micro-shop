package de.tekup.productservice.service.serviceImpl;

import de.tekup.productservice.config.RabbitMqConfig;
import de.tekup.productservice.config.RestTemplateConfig;
import de.tekup.productservice.dto.APIResponse;
import de.tekup.productservice.dto.CouponResponse;
import de.tekup.productservice.dto.ProductRequestDTO;
import de.tekup.productservice.dto.ProductResponseDTO;
import de.tekup.productservice.entity.Product;
import de.tekup.productservice.exception.MicroserviceInvalidResponseException;
import de.tekup.productservice.exception.ProductAlreadyExistsException;
import de.tekup.productservice.exception.ProductNotFoundException;
import de.tekup.productservice.exception.ProductServiceBusinessException;
import de.tekup.productservice.repository.ProductRepository;
import de.tekup.productservice.service.ProductServiceInterface;
import de.tekup.productservice.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductServiceInterface {
    
    @Value("${microservices.coupon-service.uri}")
    private String COUPON_SERVICE_URL;
    
    private final ProductRepository productRepository;
    
    private final RestTemplateConfig restTemplate;
    
    private final RabbitTemplate rabbitTemplate;
    
    //private final WebClient.Builder webClientBuilder;
    
    @Override
    public List<ProductResponseDTO> getProducts() throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProducts - Fetching Started.");
            
            List<Product> products = productRepository.findAll();
            
            List<ProductResponseDTO> productResponseDTOS = products.stream()
                    .map(Mapper::toDto)
                    .toList();
            
            log.info("ProductService::getProducts - Fetched {} products", productResponseDTOS.size());
            
            log.info("ProductService::getProducts - Fetching Ends.");
            return productResponseDTOS;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving products, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching all products");
        }
    }
    
    @Override
    public ProductResponseDTO getProductById(Long id) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProductById - Fetching Started.");
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
            
            ProductResponseDTO productResponseDTO = Mapper.toDto(product);
            
            log.debug("ProductService::getProductById - Product retrieved by ID: {} {}", id, Mapper.jsonToString(productResponseDTO));
            
            log.info("ProductService::getProductById - Fetching Ends.");
            return productResponseDTO;
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching product by id");
        }
    }
    
    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::createProduct - STARTED.");
            
            if (productRepository.existsByName(productRequestDTO.getName())) {
                throw new ProductAlreadyExistsException("Product with name " + productRequestDTO.getName() + " already exists");
            }

            Product product = Mapper.toEntity(productRequestDTO);
            
            // Retrieving coupon from coupon-service and map it to APIResponse
            ResponseEntity<APIResponse<CouponResponse>> responseEntity = restTemplate
                    .getRestTemplate()
                    .exchange(COUPON_SERVICE_URL + "/code/" + productRequestDTO.getCouponCode(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            
            //Getting coupon code from coupon-service
            CouponResponse coupon = getCouponCode(responseEntity);
            
            // Applying discount
            product.setPrice(productRequestDTO.getPrice().subtract(coupon.getDiscount()));
            
            // Saving product
            Product persistedProduct = productRepository.save(product);
            
            ProductResponseDTO productResponseDTO = Mapper.toDto(persistedProduct);
            log.debug("ProductService::createProduct - product created : {}", Mapper.jsonToString(productResponseDTO));
            
            // Sending to rabbitMq
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, productResponseDTO);
            log.info("ProductService::createProduct - product sent to QUEUE");
            
            log.info("ProductService::createProduct - ENDS.");
            return productResponseDTO;
            
        } catch (MicroserviceInvalidResponseException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (ProductAlreadyExistsException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while persisting product, Exception message {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while creating a new product");
        }
    }
    
    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO updatedProduct) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::updateProduct - Started.");
            
            Product product = Mapper.toEntity(updatedProduct);
            
            // Fetch the existing product and update its properties
            ProductResponseDTO existingProduct = getProductById(id);
            product.setId(id);
            product.setCreatedAt(existingProduct.getCreatedAt());
            
            // Update the product and convert to response DTO
            Product persistedProduct = productRepository.save(product);
            ProductResponseDTO productResponseDTO = Mapper.toDto(persistedProduct);
            
            log.debug("ProductService::updateProduct - Updated product: {}", Mapper.jsonToString(productResponseDTO));
            log.info("ProductService::updateProduct - Completed for ID: {}", id);
            
            return productResponseDTO;
            
        } catch (Exception exception) {
            log.error("Exception occurred while updating product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while updating product");
        }
    }

    
    @Override
    public void deleteProduct(Long id) {
        log.info("ProductService::deleteProduct - Starts.");
        
        try {
            log.info("ProductService::deleteProduct - Deleting product with ID: {}", id);
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
            productRepository.delete(product);
            
            log.info("ProductService::deleteProduct - Deleted product with ID: {}", id);
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while deleting product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while deleting a product");
        }
        
        log.info("ProductService::deleteProduct - Ends.");
    }
    
    private CouponResponse getCouponCode(ResponseEntity<APIResponse<CouponResponse>> responseEntity) {
        APIResponse<CouponResponse> apiResponse = responseEntity.getBody();
        
        assert apiResponse != null;
        if (apiResponse.getStatus().equals("FAILED")) {
            if (!apiResponse.getErrors().isEmpty()) {
                String errorDetails = apiResponse.getErrors().get(0).getErrorMessage();
                
                throw new MicroserviceInvalidResponseException(errorDetails);
            }
            throw new MicroserviceInvalidResponseException("Unknown error occurred.");
        }
        
        
        return apiResponse.getResults();
    }
}
