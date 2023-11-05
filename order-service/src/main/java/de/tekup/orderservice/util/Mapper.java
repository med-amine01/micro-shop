package de.tekup.orderservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.orderservice.dto.APIResponse;
import de.tekup.orderservice.dto.OrderLineItemsResponse;
import de.tekup.orderservice.dto.OrderResponse;
import de.tekup.orderservice.entity.Order;
import de.tekup.orderservice.entity.OrderLineItems;
import de.tekup.orderservice.enums.OrderStatus;
import de.tekup.orderservice.exception.InvalidResponseException;
import de.tekup.orderservice.exception.OrderServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Mapper {
    
    // this class should not be instantiated
    // All methods are static
    private Mapper() {
    
    }
    
    public static Order toEntity(OrderResponse orderResponse) {
        Order order = new Order();
        order.setOrderNumber(orderResponse.getOrderNumber());
        
        OrderStatus orderStatus = convertStatusToEnum(orderResponse.getOrderStatus());
        order.setOrderStatus(orderStatus);
        
        // Setting converting order line dto to order line entity
        List<OrderLineItems> orderLineItemsList = new ArrayList<>();
        orderResponse.getItems().forEach(itemsResponse -> {
            OrderLineItems ol = new OrderLineItems();
            ol.setSkuCode(itemsResponse.getSkuCode());
            ol.setUnitePrice(itemsResponse.getUnitePrice());
            ol.setQuantity(itemsResponse.getQuantity());
            ol.setPrice(itemsResponse.getPrice());
            orderLineItemsList.add(ol);
        });
        order.setOrderLineItemsList(orderLineItemsList);
        
        // TODO : don't forget to update these fields
        order.setCreateBy("order-service");
        order.setUpdatedBy("order-service");
        
        //Setting total price
        order.setTotalPrice(orderResponse.getTotalPrice());
        
        return order;
    }
    
    public static OrderStatus convertStatusToEnum(String orderStatus) {
        String[] stArray = Stream.of(OrderStatus.values()).map(OrderStatus::name).toArray(String[]::new);
        String status = orderStatus.toUpperCase();

        if (!Arrays.stream(stArray).anyMatch(status::equals)) {
            throw new OrderServiceException("Order status not found : " + orderStatus);
        }
        
        if (status.equalsIgnoreCase("pending")) {
            return OrderStatus.PENDING;
        } else if (status.equalsIgnoreCase("placed")) {
            return OrderStatus.PLACED;
        }
        
        return OrderStatus.CANCELED;
    }
    
    public static OrderResponse toDto(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setOrderStatus(order.getOrderStatus().toString());
        List<OrderLineItemsResponse> orderLineItemsResponseList = new ArrayList<>();
        order.getOrderLineItemsList().forEach(orderLineItems -> {
            OrderLineItemsResponse itemsResponse = new OrderLineItemsResponse();
            itemsResponse.setSkuCode(orderLineItems.getSkuCode());
            itemsResponse.setUnitePrice(orderLineItems.getUnitePrice());
            itemsResponse.setQuantity(orderLineItems.getQuantity());
            itemsResponse.setPrice(orderLineItems.getPrice());
            orderLineItemsResponseList.add(itemsResponse);
        });
        orderResponse.setItems(orderLineItemsResponseList);
        orderResponse.setTotalPrice(order.getTotalPrice());
        
        return orderResponse;
    }
    
    
    public static BigDecimal formatBigDecimalDecimal(BigDecimal value) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
        
        String formattedValue = decimalFormat.format(value);
        
        return new BigDecimal(formattedValue);
    }
    
    public static String jsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
    
    
    public static <T> T getApiResponseData(ResponseEntity<APIResponse<T>> responseEntity) {
        APIResponse<T> apiResponse = responseEntity.getBody();
        
        if (apiResponse != null && "FAILED".equals(apiResponse.getStatus())) {
            String errorDetails = apiResponse.getErrors().isEmpty()
                    ? "Unknown error occurred."
                    : apiResponse.getErrors().get(0).getErrorMessage();
            throw new InvalidResponseException(errorDetails);
        }
        
        return apiResponse != null ? apiResponse.getResults() : null;
    }
    
}

