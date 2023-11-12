package de.tekup.couponservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tekup.couponservice.dto.request.CouponRequest;
import de.tekup.couponservice.dto.request.CouponUpdateRequest;
import de.tekup.couponservice.dto.response.CouponResponse;
import de.tekup.couponservice.entity.Coupon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper {

    // this class should not instantiate
    // All methods are static
    private Mapper() {}

    public static Coupon toEntity(CouponRequest couponRequest) {
        Coupon coupon = new Coupon();
        coupon.setName(couponRequest.getName().trim());
        coupon.setDiscount(couponRequest.getDiscount());
        coupon.setExpirationDate(couponRequest.getExpirationDate());

        return coupon;
    }

    public static Coupon toEntity(CouponUpdateRequest requestUpdate, Coupon couponInDb) {
        Coupon coupon = new Coupon();
        coupon.setCode(couponInDb.getCode());
        String name = requestUpdate.getName() != null ? requestUpdate.getName() : couponInDb.getName();
        coupon.setName(name);
        coupon.setDiscount(requestUpdate.getDiscount());
        coupon.setExpirationDate(requestUpdate.getExpirationDate());
        
        return coupon;
    }

    public static CouponResponse toDto(Coupon coupon) {
        CouponResponse couponResponse = new CouponResponse();
        couponResponse.setCode(coupon.getCode());
        couponResponse.setName(coupon.getName());
        couponResponse.setDiscount(coupon.getDiscount());
        couponResponse.setExpirationDate(coupon.getExpirationDate());
        couponResponse.setEnabled(coupon.isEnabled());

        return couponResponse;
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
