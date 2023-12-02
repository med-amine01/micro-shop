package de.tekup.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsResponse {
    private String skuCode;
    private BigDecimal unitePrice;
    private Integer quantity;
    private BigDecimal price;
}