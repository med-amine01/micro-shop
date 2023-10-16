package de.tekup.couponservice.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Coupon extends AbstractEntity {

    private String code;
    private BigDecimal discount;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private String expirationDate;
}
