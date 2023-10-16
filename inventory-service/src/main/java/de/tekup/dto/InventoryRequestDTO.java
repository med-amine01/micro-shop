package de.tekup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InventoryRequestDTO {
    
    @Positive(message = "Quantity must be positive")
    private Integer quantity;
    
    @NotNull(message = "The increase field must not be null. Please provide a value (true or false).")
    private boolean increase;
}
