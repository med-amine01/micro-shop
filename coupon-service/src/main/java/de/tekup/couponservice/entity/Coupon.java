package de.tekup.couponservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Coupon extends AbstractEntity {

    @Column(unique = true)
    private String code;

    private String name;

    private BigDecimal discount;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private String expirationDate;

    private boolean enabled = true;
}
