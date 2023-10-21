package de.tekup.orderservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.orderservice.dto.OrderLineItemsResponse;
import de.tekup.orderservice.dto.OrderResponse;
import de.tekup.orderservice.entity.Order;
import de.tekup.orderservice.entity.OrderLineItems;
import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
public class Mapper {
    
    // this class should not be instantiated
    // All methods are static
    private Mapper() {
    
    }
    
    public static Order toEntity(OrderResponse orderResponse) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
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
        order.setTotalPrice(orderResponse.getTotalPrice());

        return order;
    }
    
    public static OrderResponse toDto(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(order.getOrderNumber());
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
        
        return orderResponse;
    }
    
    public static float formatFloatDecimal(float value)
    {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        
        return Float.parseFloat(decimalFormat.format(value));
    }
    
    public static String jsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}

