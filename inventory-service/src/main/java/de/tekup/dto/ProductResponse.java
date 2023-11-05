package de.tekup.dto;

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
    private String description;
    private boolean enabled;
    private BigDecimal price;
    private String couponCode;
    private String createdAt;
    private String updatedAt;
}
