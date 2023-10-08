package de.tekup.couponservice.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Coupon extends AbstractEntity {

    private String code;
    private BigDecimal discount;
    private LocalDateTime expirationDate;
}
