package de.tekup.orderservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="line_items")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderLineItems extends AbstractEntity {
    
    private String skuCode;
    private float unitePrice;
    private Integer quantity;
    private float price;
}
