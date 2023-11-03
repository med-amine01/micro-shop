package de.tekup.productservice.service.serviceImpl;

import de.tekup.productservice.config.RabbitMqConfig;
import de.tekup.productservice.config.RestTemplateConfig;
import de.tekup.productservice.dto.*;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
            List<Product> products = productRepository.findAll();
            
            List<ProductResponse> productResponses = products.stream()
                    .map(Mapper::toDto)
                    .toList();
            
            log.info("ProductService::getProducts - Fetched {} products", productResponses.size());

            return productResponses;
        } catch (Exception exception) {
            log.error("Exception occurred while retrieving products, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while fetching all products");
        }
    }

    @Override
    public ProductResponse getProductBySkuCode(String skuCode) throws ProductServiceBusinessException {
        try {
            Product product = productRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new ProductNotFoundException("Product with SkuCode " + skuCode + " not found"));
            
            ProductResponse productResponse = Mapper.toDto(product);
            
            log.debug("ProductService::getProductBySkuCode - Product retrieved by SkuCode: {} {}",
                    skuCode,
                    Mapper.jsonToString(productResponse));
            
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
            if (productRepository.existsByName(productRequest.getName())) {
                throw new ProductAlreadyExistsException("Product with name " + productRequest.getName() + " already exists");
            }

            Product product = Mapper.toEntity(productRequest);

            // Generate skuCode for product
            product.setSkuCode(generateSkuCode(product.getName()));

            // If there is no coupon code in route params => discounted price should be null
            if (null == product.getCouponCode()) {
                product.setDiscountedPrice(null);
            } else {
                // Check requested coupon from coupon-service
                CouponResponse coupon = getCouponResponse(productRequest.getCouponCode());
                // Apply discount
                applyDiscount(product, coupon);
            }

            // Save product
            Product persistedProduct = productRepository.save(product);

            // Mapping to product to response
            ProductResponse productResponse = Mapper.toDto(persistedProduct);
            log.debug("ProductService::createProduct - product created : {}", Mapper.jsonToString(productResponse));

            // Sending to rabbitMq
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, productResponse);
            log.info("product created and sent to the queue");

            return productResponse;
        } catch (ProductServiceBusinessException | InvalidResponseException | ProductAlreadyExistsException exception) {
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
    public ProductResponse updateProduct(String skuCode, ProductRequestUpdate productRequest) throws ProductServiceBusinessException {
        try {
            // Fetch the existing product and update its properties
            Product existingProduct = productRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new ProductNotFoundException("Product with SkuCode " + skuCode + " not found"));

            Product product = Mapper.toEntity(productRequest, existingProduct);
            product.setId(existingProduct.getId());

            // Put the two object on the same page (which is common object ProductResponse)
            ProductResponse convertResponse = Mapper.toDto(Mapper.toEntity(productRequest, existingProduct));
            // Check if nothing changes
            if (Mapper.toDto(existingProduct).equals(convertResponse)) {
                throw new ProductServiceBusinessException("No update needed, nothing changed.");
            }

            if (null == product.getCouponCode()) {
                product.setDiscountedPrice(null);
            } else {
                // Check for requested coupon
                CouponResponse coupon = getCouponResponse(productRequest.getCouponCode());
                // Apply discount
                applyDiscount(product, coupon);
            }

            // Update the product and convert to response DTO
            Product persistedProduct = productRepository.save(product);
            ProductResponse productResponse = Mapper.toDto(persistedProduct);

            log.debug("ProductService::updateProduct - Updated product: {}", Mapper.jsonToString(productResponse));
            
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, productResponse);
            log.info("product created and sent to the queue");

            return productResponse;
        } catch (ProductServiceBusinessException | InvalidResponseException | ProductAlreadyExistsException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while updating product, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException("Exception occurred while updating product");
        }
    }

    @Override
    public ProductResponse disableProduct(String skuCode) throws ProductServiceBusinessException {
        try {
            Product product = productRepository.findBySkuCode(skuCode)
                    .orElseThrow(() -> new ProductNotFoundException("Product with SkuCode " + skuCode + " not found"));

            // Disable product
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

    @Override
    public void updateProductsFromQueue(CouponResponse couponResponse) {
        Optional<Product> products = productRepository.findByCouponCode(couponResponse.getCode());
        products.ifPresent(product -> {
            if (!couponResponse.isEnabled()) {
                product.setDiscountedPrice(null);
                product.setCouponCode(null);
            } else {
                applyDiscount(product, couponResponse);
            }
            productRepository.save(product);
        });
    }

    private CouponResponse getCouponResponse(String couponCode) {
        try {
            // Retrieving coupon from coupon-service and map it to APIResponse
            ResponseEntity<APIResponse<CouponResponse>> responseEntity = restTemplate
                    .getRestTemplate()
                    .exchange(COUPON_SERVICE_URL + "/" + couponCode,
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

            return coupon;
        } catch (InvalidResponseException | ProductServiceBusinessException exception) {
            if (exception instanceof ProductServiceBusinessException) {
                log.error(exception.getMessage());
            }
            throw exception;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    private void applyDiscount(Product product, CouponResponse coupon) {
        // Apply discount (discountedPrice)
        BigDecimal mainPrice = product.getPrice();
        BigDecimal discountPercentage = coupon.getDiscount().divide(BigDecimal.valueOf(100));
        BigDecimal discountedPrice = mainPrice.subtract(mainPrice.multiply(discountPercentage));
        
        product.setDiscountedPrice(discountedPrice.setScale(2, RoundingMode.HALF_UP));
    }

    private String generateSkuCode(String name) {
        String randomUUID = UUID.randomUUID().toString().substring(0, 4);
        
        return name
                .replace(" ", "")
                .concat(randomUUID)
                .toUpperCase();
    }
}
