package de.tekup.couponservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.couponservice.dto.CouponRequestDTO;
import de.tekup.couponservice.dto.CouponResponseDTO;
import de.tekup.couponservice.entity.Coupon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValueMapper {

    // this class should not instantiated
    // All methods are static
    private ValueMapper() {

    }

    public static Coupon toEntity(CouponRequestDTO couponRequestDTO) {
        Coupon coupon = new Coupon();
        coupon.setCode(couponRequestDTO.getCode());
        coupon.setDiscount(couponRequestDTO.getDiscount());
        coupon.setExpirationDate(couponRequestDTO.getExpirationDate() + " 00:00:00");

        return coupon;
    }

    public static CouponResponseDTO toDto(Coupon coupon) {
        CouponResponseDTO couponResponseDTO = new CouponResponseDTO();
        couponResponseDTO.setId(coupon.getId());
        couponResponseDTO.setCode(coupon.getCode());
        couponResponseDTO.setDiscount(coupon.getDiscount());
        couponResponseDTO.setExpirationDate(coupon.getExpirationDate());
        couponResponseDTO.setCreatedAt(coupon.getCreatedAt());
        couponResponseDTO.setUpdatedAt(coupon.getUpdatedAt());

        return couponResponseDTO;
    }

    public static String jsonToString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
