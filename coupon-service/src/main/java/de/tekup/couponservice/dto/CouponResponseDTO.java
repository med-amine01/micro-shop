package de.tekup.couponservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponResponseDTO {
    private String code;
    private BigDecimal discount;
    private String expirationDate;
    private boolean enabled;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CouponResponseDTO other = (CouponResponseDTO) o;
        
        // Compare code and expirationDate as before
        if (!Objects.equals(code, other.code)) return false;
        if (!Objects.equals(expirationDate, other.expirationDate)) return false;

        // Compare discount with 2 decimal places
        BigDecimal thisDiscount = discount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal otherDiscount = other.discount.setScale(2, RoundingMode.HALF_UP);

        return thisDiscount.equals(otherDiscount);
    }
}
