package de.tekup.couponservice.service;

import de.tekup.couponservice.dto.CouponRequest;
import de.tekup.couponservice.dto.CouponRequestUpdate;
import de.tekup.couponservice.dto.CouponResponse;
import de.tekup.couponservice.exception.CouponServiceBusinessException;

import java.util.List;

public interface CouponServiceInterface {
    List<CouponResponse> getCoupons() throws CouponServiceBusinessException;
    
    CouponResponse getCouponByCode(String code) throws CouponServiceBusinessException;

    CouponResponse createCoupon(CouponRequest couponRequest);

    CouponResponse updateCoupon(String code, CouponRequestUpdate updatedCoupon) throws CouponServiceBusinessException;

    CouponResponse disableCoupon(String code);
}