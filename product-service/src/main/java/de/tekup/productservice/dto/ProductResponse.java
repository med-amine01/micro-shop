package de.tekup.productservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private String skuCode;
    private String name;
    private String couponCode;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
}
