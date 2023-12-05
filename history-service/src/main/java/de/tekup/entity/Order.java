package de.tekup.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "_order")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Order {
    
    @Id
    private Long id;
    private String createdAt;
    private String updatedAt;
    private String orderNumber;
    private BigDecimal totalPrice;
    private String orderStatus;
    private List<OrderLineItems> orderLineItemsList;
    private String createdBy;
    private String updatedBy;
}
