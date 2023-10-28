package de.tekup.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private String code;
    private BigDecimal discount;
    private String expirationDate;
    private boolean enabled;
}
