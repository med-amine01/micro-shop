package de.tekup.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductRequest {
    
    @NotBlank(message = "skuCode is required")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,20}$", message = "SkuCode must consist min of 2 chars, can only contain letters, numbers, underscores, and hyphens.")
    private String skuCode;

    @NotBlank(message = "Name is required")
    @Size(min = 2, message = "Name should have at least 2 characters")
    private String name;

    private String description;

    @NotNull(message = "Price amount is required")
    @Positive(message = "Price amount must be positive")
    private BigDecimal price;
    
    @Size(min = 2, message = "Coupon Code should have at least 2 characters")
    private String couponCode;
}
