package de.tekup.productservice.service.serviceImpl;

import de.tekup.productservice.config.RabbitMqConfig;
import de.tekup.productservice.config.RestTemplateConfig;
import de.tekup.productservice.dto.APIResponse;
import de.tekup.productservice.dto.CouponResponse;
import de.tekup.productservice.dto.ProductRequest;
import de.tekup.productservice.dto.ProductResponse;
import de.tekup.productservice.entity.Product;
import de.tekup.productservice.exception.InvalidResponseException;
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
    public List<ProductResponse> getProducts() throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProducts - Fetching Started.");
            
            List<Product> products = productRepository.findAll();
            
            List<ProductResponse> productResponses = products.stream()
                    .map(Mapper::toDto)
                    .toList();
            
            log.info("ProductService::getProducts - Fetched {} products", productResponses.size());
            
            log.info("ProductService::getProducts - Fetching Ends.");
            return productResponses;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving products, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching all products");
        }
    }
    
    @Override
    public ProductResponse getProductById(Long id) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProductById - Fetching Started.");
            
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
            
            ProductResponse productResponse = Mapper.toDto(product);
            
            log.debug("ProductService::getProductById - Product retrieved by ID: {} {}", id, Mapper.jsonToString(productResponse));
            
            log.info("ProductService::getProductById - Fetching Ends.");
            return productResponse;
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching product by id");
        }
    }
    
    @Override
    public ProductResponse getProductBySkuCode(String skuCode) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::getProductBySkuCode - Fetching Started.");
            
            Product product = productRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new ProductNotFoundException("Product with SkuCode " + skuCode + " not found"));
            
            ProductResponse productResponse = Mapper.toDto(product);
            
            log.debug("ProductService::getProductBySkuCode - Product retrieved by SkuCode: {} {}", skuCode, Mapper.jsonToString(productResponse));
            
            log.info("ProductService::getProductBySkuCode - Fetching Ends.");
            return productResponse;
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching product by SkuCode");
        }
    }
    
    @Override
    public ProductResponse createProduct(ProductRequest productRequest) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::createProduct - STARTED.");
            
            if (productRepository.existsByName(productRequest.getName())) {
                throw new ProductAlreadyExistsException("Product with name " + productRequest.getName() + " already exists");
            }

            Product product = Mapper.toEntity(productRequest);
            
            // Retrieving coupon from coupon-service and map it to APIResponse
            ResponseEntity<APIResponse<CouponResponse>> responseEntity = restTemplate
                    .getRestTemplate()
                    .exchange(COUPON_SERVICE_URL + "/code/" + productRequest.getCouponCode(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            
            //Getting coupon code from coupon-service
            CouponResponse coupon = Mapper.getApiResponseData(responseEntity);
            
            // Applying discount
            product.setPrice(productRequest.getPrice().subtract(coupon.getDiscount()));
            
            // Saving product
            Product persistedProduct = productRepository.save(product);
            
            ProductResponse productResponse = Mapper.toDto(persistedProduct);
            log.debug("ProductService::createProduct - product created : {}", Mapper.jsonToString(productResponse));
            
            // Sending to rabbitMq
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, productResponse);
            log.info("ProductService::createProduct - product sent to QUEUE");
            
            log.info("ProductService::createProduct - ENDS.");
            return productResponse;
            
        } catch (InvalidResponseException exception) {
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
    public ProductResponse updateProduct(Long id, ProductRequest updatedProduct) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::updateProduct - Started.");
            
            Product product = Mapper.toEntity(updatedProduct);
            
            // Fetch the existing product and update its properties
            ProductResponse existingProduct = getProductById(id);
            product.setId(id);
            product.setCreatedAt(existingProduct.getCreatedAt());
            
            // Update the product and convert to response DTO
            Product persistedProduct = productRepository.save(product);
            ProductResponse productResponse = Mapper.toDto(persistedProduct);
            
            log.debug("ProductService::updateProduct - Updated product: {}", Mapper.jsonToString(productResponse));
            log.info("ProductService::updateProduct - Completed for ID: {}", id);
            
            return productResponse;
            
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
}
