package de.tekup.couponservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponResponseDTO {
    private Long id;
    private String code;
    private BigDecimal discount;
    private String expirationDate;
    private String createdAt;
    private String updatedAt;
}
