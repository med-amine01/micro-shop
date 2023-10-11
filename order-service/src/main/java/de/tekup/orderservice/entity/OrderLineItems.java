package de.tekup.orderservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderLineItems extends AbstractEntity {
    
    private String skuCode;
    private BigDecimal price;
    private Integer quantity;
}
