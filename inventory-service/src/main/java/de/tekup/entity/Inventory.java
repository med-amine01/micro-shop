package de.tekup.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Inventory extends AbstractEntity {
    
    @Column(unique = true)
    private String skuCode;
    private Integer quantity;
    
    public Integer increaseQte(Integer qte) {
        return this.quantity += qte;
    }
    
    public Integer decreaseQte(Integer qte) {
        return this.quantity -= qte;
    }
}
