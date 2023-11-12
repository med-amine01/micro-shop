package de.tekup.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemsRequest {
    
    @NotBlank(message = "skuCode is required")
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,20}$", message = "SkuCode must consist min of 2 chars, can only contain letters, numbers, underscores, and hyphens.")
    private String skuCode;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
}