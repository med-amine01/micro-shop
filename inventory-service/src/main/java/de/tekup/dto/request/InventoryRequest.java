package de.tekup.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InventoryRequest {
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "The increase field must not be null. Please provide a value (true or false).")
    private boolean increase;
}
