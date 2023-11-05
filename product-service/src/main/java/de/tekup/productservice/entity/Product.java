package de.tekup.productservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity {
    
    @Column(unique = true)
    private String skuCode;
    
    private String name;
    
    private String description;
    
    private String couponCode;
    
    private BigDecimal price;
    
    private BigDecimal discountedPrice;
}