package de.tekup.couponservice.dto;

import de.tekup.couponservice.validation.FutureExpirationDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CouponRequestDTO {

    @NotBlank(message = "Coupon code is required")
    @Size(min = 2, message = "Code should have at least 2 characters")
    private String code;
    
    @NotNull(message = "Discount percentage is required")
    @DecimalMin(value = "0.01", message = "Discount percentage must be a positive value")
    @DecimalMax(value = "100.00", message = "Discount percentage must be between 0.01 and 100.00")
    private BigDecimal discount;

    @NotNull(message = "Expiration date is required")
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})$", message = "Expiration date must be in the format dd-MM-yyyy")
    @FutureExpirationDate
    private String expirationDate;
}
