package de.tekup.couponservice.service;

import de.tekup.couponservice.dto.CouponRequestDTO;
import de.tekup.couponservice.dto.CouponResponseDTO;
import de.tekup.couponservice.exception.CouponServiceBusinessException;

import java.util.List;

public interface CouponServiceInterface {
    List<CouponResponseDTO> getCoupons() throws CouponServiceBusinessException;
    
    CouponResponseDTO getCouponByCode(String code) throws CouponServiceBusinessException;

    CouponResponseDTO createCoupon(CouponRequestDTO couponRequestDTO);

    CouponResponseDTO updateCoupon(String code, CouponRequestDTO updatedCoupon) throws CouponServiceBusinessException;

    CouponResponseDTO disableCoupon(String code);
}