package de.tekup.orderservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name="_order")
@Data
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity {
    
    private String orderNumber;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLineItems> orderLineItemsList;
}
