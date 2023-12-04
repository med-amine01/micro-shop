package de.tekup.orderservice.service;

import de.tekup.orderservice.config.RabbitMqConfig;
import de.tekup.orderservice.dto.request.InventoryRequest;
import de.tekup.orderservice.dto.request.OrderLineItemsRequest;
import de.tekup.orderservice.dto.request.OrderRequest;
import de.tekup.orderservice.dto.request.OrderStatusRequest;
import de.tekup.orderservice.dto.response.*;
import de.tekup.orderservice.entity.Order;
import de.tekup.orderservice.enums.OrderStatus;
import de.tekup.orderservice.exception.InvalidRequestException;
import de.tekup.orderservice.exception.OrderNotFoundException;
import de.tekup.orderservice.exception.OrderServiceException;
import de.tekup.orderservice.repository.OrderRepository;
import de.tekup.orderservice.util.JwtUtil;
import de.tekup.orderservice.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    
    @Value("${microservices.inventory-service.uri}")
    private String INVENTORY_SERVICE_URL;
    
    @Value("${microservices.product-service.uri}")
    private String PRODUCT_SERVICE_URL;
    
    @Value("${microservices.coupon-service.uri}")
    private String COUPON_SERVICE_URL;
    
    private final OrderRepository orderRepository;
    
    private final WebClient.Builder webClientBuilder;
    
    private final RabbitTemplate rabbitTemplate;
    
    private final JwtUtil jwtUtil;
    
    public List<OrderResponse> getOrdersByStatus(String status) {
        List<Order> orders;
        
        if (null != status) {
            orders = orderRepository.findAllByOrderStatusOrderByIdDesc(Mapper.convertStatusToEnum(status));
        } else {
            orders = orderRepository.findAllByOrderByIdDesc();
        }
        
        return orders.stream().map(Mapper::toDto).toList();
    }
    
    public OrderResponse placeOrder(OrderRequest orderRequest, String couponCode, String authorization) {
        CouponResponse couponResponse = null;
        // Check coupon code existence and validity
        if (null != couponCode) {
            couponResponse = getCoupon(couponCode);
        }
        
        // Check if there is replication in items of same skuCode
        if (hasDuplicateSkuCodes(orderRequest.getOrderLineItemsRequestList())) {
            throw new InvalidRequestException("Duplicate items were found");
        }
        
        List<OrderLineItemsResponse> orderItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        
        // All requested products and quantity are in stock
        sniffStock(orderRequest);
        
        // If sniffing from stock passed here ! means that quantity is in stock
        for (OrderLineItemsRequest item : orderRequest.getOrderLineItemsRequestList()) {
            // For every item checks product-service
            // Get the product info and calculate : price =  qte * unit price
            // Sum all the items prices
            totalPrice = getTotalPrice(item, orderItems, totalPrice, couponResponse);
            
            // Decrease inventory stock whether it's (default:PENDING) or PLACED
            updateStock(item.getSkuCode(), item.getQuantity(), false);
        }
        
        if (orderItems.isEmpty()) {
            throw new OrderServiceException("Couldn't create order, no items were found");
        }
        
        // Setting up order response
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(UUID.randomUUID().toString());
        orderResponse.setOrderStatus(OrderStatus.PENDING.name());
        orderResponse.setItems(orderItems);
        orderResponse.setTotalPrice(totalPrice);
        
        // Getting createBy from jwt token
        String createdBy = jwtUtil.getUsernameFromToken(authorization);
        orderResponse.setCreatedBy(createdBy);
        
        // Map it to entity and save it in database
        orderRepository.save(Mapper.toEntity(orderResponse));
        
        // Sending order response to mailing queue
        sendingToQueue(orderResponse);
        
        return orderResponse;
    }
    
    public OrderResponse updateOrderStatus(OrderStatusRequest orderStatusRequest, String uuid, String authorization) {
        // Get order by uuid
        Order order = orderRepository
                .findByOrderNumber(uuid)
                .orElseThrow(() -> new OrderNotFoundException("Order with uuid : " + uuid + " not found"));
        
        String fetchedStatus = order.getOrderStatus().toString();
        
        // You can't change update a canceled order
        if (fetchedStatus.equalsIgnoreCase(OrderStatus.CANCELED.name())) {
            throw new OrderServiceException("the order already canceled");
        }
        
        // If fetched order(db) status is the same order(rq) status throw exception
        if (fetchedStatus.equalsIgnoreCase(orderStatusRequest.getOrderStatus())) {
            throw new OrderServiceException("the order already " + fetchedStatus + " you can't update a " + fetchedStatus + " order");
        }
        
        // Patch order status
        String requestedStatus = orderStatusRequest.getOrderStatus();
        if (requestedStatus.equalsIgnoreCase(OrderStatus.PLACED.name())) {
            order.setOrderStatus(OrderStatus.PLACED);
        } else {
            order.setOrderStatus(OrderStatus.CANCELED);
            order.getOrderLineItemsList().forEach(items -> {
                // Re-increase the stock if the order is canceled
                updateStock(items.getSkuCode(), items.getQuantity(), true);
            });
        }
        
        // Getting updated from jwt token
        String updatedBy = jwtUtil.getUsernameFromToken(authorization);
        order.setUpdatedBy(updatedBy);
        
        // Saving it and map it to queue
        OrderResponse orderResponse = Mapper.toDto(orderRepository.save(order));
        
        // Sending order response to mailing queue
        sendingToQueue(orderResponse);
        
        return orderResponse;
    }
    
    private CouponResponse getCoupon(String couponCode) {
        try {
            ResponseEntity<ApiResponse<CouponResponse>> responseEntity = webClientBuilder
                    .build()
                    .get()
                    .uri(COUPON_SERVICE_URL + "/" + couponCode)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiResponse<CouponResponse>>() {
                    })
                    .block();
            
            CouponResponse couponResponse = Mapper.getApiResponseData(responseEntity);
            if (null == couponResponse) {
                throw new OrderServiceException("couldn't fetch coupon = " + couponCode);
            }
            
            // Check for validity
            if (!couponResponse.isEnabled()) {
                throw new OrderServiceException("coupon already expired");
            }
            
            return couponResponse;
        } catch (OrderServiceException exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }
    
    private boolean hasDuplicateSkuCodes(List<OrderLineItemsRequest> orderLineItems) {
        Map<String, Long> skuCodeCounts = orderLineItems
                .stream()
                .collect(Collectors.groupingBy(OrderLineItemsRequest::getSkuCode, Collectors.counting()));
        
        return skuCodeCounts
                .values()
                .stream()
                .anyMatch(count -> count > 1);
    }
    
    private BigDecimal getTotalPrice(
            OrderLineItemsRequest item,
            List<OrderLineItemsResponse> orderItems,
            BigDecimal totalPrice,
            CouponResponse couponResponse
    ) {
        try {
            // Call the product service and get product info (unit price)
            ResponseEntity<ApiResponse<ProductResponse>> responseEntity = webClientBuilder
                    .build()
                    .get()
                    .uri(PRODUCT_SERVICE_URL + "/" + item.getSkuCode())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiResponse<ProductResponse>>() {
                    })
                    .block(); // Blocking to get the response until i get a response
            
            ProductResponse productResponse = Mapper.getApiResponseData(responseEntity);
            
            if (null == productResponse) {
                throw new OrderServiceException("couldn't fetch requested product");
            }
            
            // Product is in stock, create the order line item
            OrderLineItemsResponse orderLineItem = new OrderLineItemsResponse();
            orderLineItem.setSkuCode(item.getSkuCode());
            orderLineItem.setQuantity(item.getQuantity());
            // Setting unit price
            BigDecimal unitePrice = BigDecimal.ZERO;
            
            // Fetching product code
            String productCode = productResponse.getCouponCode() != null ? productResponse.getCouponCode() : "";
            
            // Check if the coupon in request param == coupon code of PRODUCT
            if (null != couponResponse && productCode.equalsIgnoreCase(couponResponse.getCode())) {
                // Take the discounted price
                unitePrice = Mapper.formatBigDecimalDecimal(productResponse.getDiscountedPrice());
            } else {
                // Take the original price
                unitePrice = Mapper.formatBigDecimalDecimal(productResponse.getPrice());
            }
            
            orderLineItem.setUnitePrice(unitePrice);
            
            // Setting total price of a single item
            BigDecimal totalSingleItem = Mapper.formatBigDecimalDecimal(unitePrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            orderLineItem.setPrice(totalSingleItem);
            
            // Adding item to orderItems
            orderItems.add(orderLineItem);
            
            // Calculate the total price for each iteration
            totalPrice = totalPrice.add(Mapper.formatBigDecimalDecimal(totalSingleItem));
        } catch (Exception exception) {
            throw new OrderServiceException(exception.getMessage());
        }
        return totalPrice;
    }
    
    private void sniffStock(OrderRequest orderRequest) {
        try {
            List<String> skuCodes = orderRequest
                    .getOrderLineItemsRequestList()
                    .stream()
                    .map(OrderLineItemsRequest::getSkuCode)
                    .toList();
            
            // Call Inventory Service, and place order if product is in stock
            InventoryResponse[] inventoryResponseArray = webClientBuilder
                    .build()
                    .get()
                    .uri(INVENTORY_SERVICE_URL + "/product/check",
                            uriBuilder -> uriBuilder
                                    .queryParam("skuCode", skuCodes)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();
            
            Arrays.stream(inventoryResponseArray)
                    .toList()
                    .forEach(inventoryResponse -> {
                        orderRequest.getOrderLineItemsRequestList()
                                .forEach(itemsRequest -> {
                                    if (inventoryResponse.isInStock()) {
                                        if (itemsRequest.getSkuCode().equals(inventoryResponse.getSkuCode())) {
                                            if (itemsRequest.getQuantity() > inventoryResponse.getQuantity()) {
                                                throw new OrderServiceException("Not enough quantity for " + itemsRequest.getSkuCode());
                                            }
                                        }
                                    } else {
                                        throw new OrderServiceException(inventoryResponse.getSkuCode() + " out of stock");
                                    }
                                });
                    });
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new OrderServiceException(exception.getMessage());
        }
    }
    
    private void updateStock(String skuCode, int quantity, boolean increase) {
        try {
            InventoryRequest inventoryRequest = new InventoryRequest();
            inventoryRequest.setQuantity(quantity);
            inventoryRequest.setIncrease(increase);
            
            ResponseEntity<ApiResponse<InventoryResponse>> responseEntity = webClientBuilder
                    .build()
                    .put()
                    .uri(INVENTORY_SERVICE_URL + "/product/quantity/" + skuCode)
                    .bodyValue(inventoryRequest)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<ApiResponse<InventoryResponse>>() {
                    })
                    .block(); // Blocking to get the response until i get a response
            
            InventoryResponse inventoryResponse = Mapper.getApiResponseData(responseEntity);
            
            if (inventoryResponse == null) {
                throw new OrderServiceException("Couldn't update the stock of " + skuCode);
            }
            
        } catch (Exception exception) {
            throw new OrderServiceException("Error updating stock " + exception.getMessage());
        }
    }
    
    private void sendingToQueue(OrderResponse orderResponse) {
        try {
            // Sending to rabbitMq
            rabbitTemplate.convertAndSend(RabbitMqConfig.MAILING_EXCHANGE, RabbitMqConfig.MAILING_ROUTING_KEY, orderResponse);
            log.info("Order placed and sent to the mailing queue");
        } catch (Exception e) {
            log.error("Couldn't send order to queue " + e.getMessage());
        }
    }
}
