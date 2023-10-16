package de.tekup.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponseDTO {
    private String skuCode;
    private Integer quantity;
    private boolean isInStock;
    private String createdAt;
    private String updatedAt;
}
