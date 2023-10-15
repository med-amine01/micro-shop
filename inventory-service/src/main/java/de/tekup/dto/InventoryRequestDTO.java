package de.tekup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InventoryRequestDTO {
    
    @NotBlank(message = "SkuCode code is required")
    @Size(min = 2, message = "SkuCode should have at least 2 characters")
    private String skuCode;
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "The increase field must not be null. Please provide a value (true or false).")
    private boolean increase;
}
