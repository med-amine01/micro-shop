package de.tekup.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Document(collection = "line_items")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderLineItems {
    @Id
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String skuCode;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal price;
}
