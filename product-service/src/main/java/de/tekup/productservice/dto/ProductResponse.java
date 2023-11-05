package de.tekup.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String skuCode;
    private String name;
    private String couponCode;
    private String description;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private boolean enabled;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        ProductResponse that = (ProductResponse) object;
        return Objects.equals(name, that.name) &&
                Objects.equals(couponCode, that.couponCode) &&
                Objects.equals(description, that.description) &&
                Objects.equals(enabled, that.enabled) &&
                Objects.equals(price.setScale(2, RoundingMode.HALF_UP),
                        that.price.setScale(2, RoundingMode.HALF_UP));
    }
}
