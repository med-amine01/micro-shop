package de.tekup.orderservice.service;

import de.tekup.orderservice.dto.*;
import de.tekup.orderservice.exception.InvalidRequestException;
import de.tekup.orderservice.exception.OrderServiceException;
import de.tekup.orderservice.repository.OrderRepository;
import de.tekup.orderservice.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    
    private final OrderRepository orderRepository;
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${microservices.inventory-service.uri}")
    private String INVENTORY_SERVICE_URL;
    
    @Value("${microservices.product-service.uri}")
    private String PRODUCT_SERVICE_URL;
    
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // Check if there is replication in items of same skuCode
        if (hasDuplicateSkuCodes(orderRequest.getOrderLineItemsRequestList())) {
            throw new InvalidRequestException("Duplicate items were found");
        }
        
        List<OrderLineItemsResponse> orderItems = new ArrayList<>();
        float totalPrice = 0.0F;
        
        // All requested products and quantity are in stock
        sniffStock(orderRequest);
        
        for (OrderLineItemsRequest item : orderRequest.getOrderLineItemsRequestList()) {
            totalPrice = getTotalPrice(item, orderItems, totalPrice);
        }
        
        // Check if all items in the order are in stock
        if (orderItems.isEmpty()) {
            throw new OrderServiceException("Couldn't create order, no items were found");
        }
        
        // Setting up order response
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(UUID.randomUUID().toString());
        orderResponse.setItems(orderItems);
        orderResponse.setTotalPrice(totalPrice);
        
        // Map it to entity and save it in database
        orderRepository.save(Mapper.toEntity(orderResponse));
        
        return orderResponse;
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

    private float getTotalPrice(OrderLineItemsRequest item, List<OrderLineItemsResponse> orderItems, float totalPrice) {
        try {
            // Call the product service and get product info (unit price)
            ResponseEntity<APIResponse<ProductResponse>> responseEntity = webClientBuilder
                    .build()
                    .get()
                    .uri(PRODUCT_SERVICE_URL + "/skuCode/" + item.getSkuCode())
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<APIResponse<ProductResponse>>() {})
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
            float unitePrice = Mapper.formatFloatDecimal(productResponse.getPrice());
            orderLineItem.setUnitePrice(unitePrice);
            
            // Setting total price of a single item
            float totalSingleItem = Mapper.formatFloatDecimal(unitePrice * item.getQuantity());
            orderLineItem.setPrice(totalSingleItem);
            
            // Adding item to orderItems
            orderItems.add(orderLineItem);
            
            // Calculate the total price for each iteration
            totalPrice += Mapper.formatFloatDecimal(totalSingleItem);
            
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
            throw new OrderServiceException("Error checking product stock " + exception.getMessage());
        }
    }

}
