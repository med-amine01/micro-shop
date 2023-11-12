package de.tekup.productservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProductRequest {
    
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
