package de.tekup.orderservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequest {
    
    @Positive(message = "quantity should be positive.")
    private Integer quantity;
    
    @NotNull(message = "increase value should not be null")
    private boolean increase;
}
