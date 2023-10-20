package de.tekup.orderservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name="_order_line_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderLineItems extends AbstractEntity {
    
    private String skuCode;
    private Float unitePrice;
    private Integer quantity;
    private Float price;
}
