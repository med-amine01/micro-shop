package de.tekup.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String skuCode;
    private String name;
    private String description;
    private float price;
    private String createdAt;
    private String updatedAt;
}
