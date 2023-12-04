package de.tekup.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inventories")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Inventory {
    
    @Id
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String skuCode;
    private Integer quantity;
}
