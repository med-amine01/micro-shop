package de.tekup.couponservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CouponRequestDTO {

    @NotBlank(message = "Coupon code is required")
    @Size(min = 2, message = "Code should have at least 2 characters")
    private String code;

    @NotNull(message = "Discount amount is required")
    @Positive(message = "Discount amount must be positive")
    private BigDecimal discount;

    @NotNull(message = "Expiration date is required")
    private LocalDateTime expirationDate;
}
