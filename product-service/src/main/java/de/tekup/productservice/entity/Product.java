package de.tekup.productservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends AbstractEntity  {

    private String skuCode;
    private String name;
    private String description;
    private BigDecimal price;
    @Transient
    private String couponCode;
}