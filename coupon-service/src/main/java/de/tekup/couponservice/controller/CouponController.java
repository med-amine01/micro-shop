package de.tekup.couponservice.controller;

import de.tekup.couponservice.dto.request.CouponRequest;
import de.tekup.couponservice.dto.request.CouponUpdateRequest;
import de.tekup.couponservice.dto.response.ApiResponse;
import de.tekup.couponservice.dto.response.CouponResponse;
import de.tekup.couponservice.service.CouponServiceInterface;
import de.tekup.couponservice.util.Mapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/coupons")
@Slf4j
public class CouponController {
    public static final String SUCCESS = "SUCCESS";
    private final CouponServiceInterface couponServiceInterface;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAllCoupons() {
        List<CouponResponse> coupons = couponServiceInterface.getCoupons();
        
        // Builder Design pattern (to avoid complex object creation)
        ApiResponse<List<CouponResponse>> responseDTO = ApiResponse.<List<CouponResponse>>builder().status(SUCCESS).results(coupons).build();
        
        log.info("CouponController::getAllCoupons response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    
    
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> getCouponByCode(@PathVariable String code) {
        log.info("CouponController::getCouponByCode {}", code);
        CouponResponse couponResponse = couponServiceInterface.getCouponByCode(code);
        
        ApiResponse<CouponResponse> responseDTO = ApiResponse.<CouponResponse>builder().status(SUCCESS).results(couponResponse).build();
        
        log.info("CouponController::getCouponByCode {} response {}", code, Mapper.jsonToString(couponResponse));
        
        return ResponseEntity.ok(responseDTO);
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(@RequestBody @Valid CouponRequest couponRequest) {
        log.info("CouponController::createCoupon request body {}", Mapper.jsonToString(couponRequest));
        
        CouponResponse createdCoupon = couponServiceInterface.createCoupon(couponRequest);
        
        ApiResponse<CouponResponse> responseDTO = ApiResponse.<CouponResponse>builder().status(SUCCESS).results(createdCoupon).build();
        
        log.info("ProductController::createNewProduct response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> updateCoupon(@PathVariable String code, @Valid @RequestBody CouponUpdateRequest couponUpdateRequest) {
        log.info("CouponController::updateCoupon request body {}", Mapper.jsonToString(couponUpdateRequest));
        
        CouponResponse updatedCoupon = couponServiceInterface.updateCoupon(code, couponUpdateRequest);
        
        ApiResponse<CouponResponse> responseDTO = ApiResponse.<CouponResponse>builder().status(SUCCESS).results(updatedCoupon).build();
        
        log.info("ProductController::updateCoupon response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }
    
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> disableCoupon(@PathVariable String code) {
        
        CouponResponse disabledCoupon = couponServiceInterface.disableCoupon(code);
        
        ApiResponse<CouponResponse> responseDTO = ApiResponse.<CouponResponse>builder().status(SUCCESS).results(disabledCoupon).build();
        
        log.info("ProductController::disableCoupon response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }
}
