package de.tekup.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsResponse {
    private String skuCode;
    private float unitePrice;
    private Integer quantity;
    private float price;
}