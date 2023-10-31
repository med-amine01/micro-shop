package de.tekup.couponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private String code;
    private String name;
    private BigDecimal discount;
    private String expirationDate;
    private boolean enabled;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CouponResponse other = (CouponResponse) o;
        
        // Compare name and expirationDate as before
        if (!Objects.equals(name, other.name)) return false;
        if (!Objects.equals(expirationDate, other.expirationDate)) return false;

        // Compare discount with 2 decimal places
        BigDecimal thisDiscount = discount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal otherDiscount = other.discount.setScale(2, RoundingMode.HALF_UP);

        return thisDiscount.equals(otherDiscount);
    }
}
