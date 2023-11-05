package de.tekup.orderservice.entity;

import de.tekup.orderservice.enums.OrderStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity {
    
    private String orderNumber;
    
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    
    @Column(name = "items")
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLineItems> orderLineItemsList;
    
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
}
