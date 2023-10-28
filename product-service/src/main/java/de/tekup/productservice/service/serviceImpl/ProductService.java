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

import java.math.BigDecimal;
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

            // If there is no coupon code => discounted price should be null
            if (null == product.getCouponCode()) {
                product.setDiscountedPrice(null);
            } else {
                // Retrieving coupon from coupon-service and map it to APIResponse
                ResponseEntity<APIResponse<CouponResponse>> responseEntity = restTemplate
                        .getRestTemplate()
                        .exchange(COUPON_SERVICE_URL + "/" + productRequest.getCouponCode(),
                                HttpMethod.GET,
                                null,
                                new ParameterizedTypeReference<>() {
                                });
                
                //Getting coupon code from coupon-service
                CouponResponse coupon = Mapper.getApiResponseData(responseEntity);
                // If coupon is disabled
                if (!coupon.isEnabled()) {
                    throw new ProductServiceBusinessException("Coupon code expired");
                }

                // Apply discount (discountedPrice)
                BigDecimal mainPrice = product.getPrice();
                BigDecimal discountPercentage = coupon.getDiscount().divide(BigDecimal.valueOf(100));
                BigDecimal discountedPrice = mainPrice.subtract(mainPrice.multiply(discountPercentage));

                product.setDiscountedPrice(discountedPrice);
            }
            
            // Saving product
            Product persistedProduct = productRepository.save(product);
            
            // Mapping to product to response
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
            if (exception.getClass().getName().contains("CachingConnectionFactory")) {
                throw new ProductServiceBusinessException("Couldn't connect to rabbitMq");
            }

            log.error("Exception occurred while persisting product, Exception message {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while creating a new product");
        }
    }
    
    @Override
    public ProductResponse updateProduct(String skuCode, ProductRequest updatedProduct) throws ProductServiceBusinessException {
        try {
            log.info("ProductService::updateProduct - Started.");
            
            // Fetch the existing product and update its properties
            Product existingProduct = productRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new ProductNotFoundException("Product with SkuCode " + skuCode + " not found"));
            
            if (productRepository.existsBySkuCode(updatedProduct.getSkuCode()) &&
                    !skuCode.equalsIgnoreCase(updatedProduct.getSkuCode())
            ) {
                throw new ProductAlreadyExistsException("Product already exists.");
            }

            Product product = Mapper.toEntity(updatedProduct);
            product.setId(existingProduct.getId());

            // Update the product and convert to response DTO
            Product persistedProduct = productRepository.save(product);
            ProductResponse productResponse = Mapper.toDto(persistedProduct);
            
            log.debug("ProductService::updateProduct - Updated product: {}", Mapper.jsonToString(productResponse));
            log.info("ProductService::updateProduct - Completed for skuCode: {}", skuCode);
            
            return productResponse;
            
        } catch (Exception exception) {
            log.error("Exception occurred while updating product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while updating product");
        }
    }

    
    @Override
    public ProductResponse disableProduct(String skuCode) throws ProductServiceBusinessException{
        log.info("ProductService::disableProduct - Starts.");
        
        try {
            log.info("ProductService::disableProduct - Disabling product with skuCode: {}", skuCode);
            
            Product product = productRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new ProductNotFoundException("Product with SkuCode " + skuCode + " not found"));
            product.setEnabled(false);
            
            log.info("ProductService::disableProduct - disabled product with skuCode: {}", skuCode);
            return Mapper.toDto(productRepository.save(product));
            
        } catch (ProductNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while deleting product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while deleting a product");
        }
    }
}
