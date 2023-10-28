package de.tekup.couponservice.controller;

import de.tekup.couponservice.dto.APIResponse;
import de.tekup.couponservice.dto.CouponRequestDTO;
import de.tekup.couponservice.dto.CouponResponseDTO;
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
    public ResponseEntity<APIResponse<List<CouponResponseDTO>>> getAllCoupons() {
        List<CouponResponseDTO> coupons = couponServiceInterface.getCoupons();

        // Builder Design pattern (to avoid complex object creation)
        APIResponse<List<CouponResponseDTO>> responseDTO = APIResponse
                .<List<CouponResponseDTO>>builder()
                .status(SUCCESS)
                .results(coupons)
                .build();

        log.info("CouponController::getAllCoupons response {}", Mapper.jsonToString(responseDTO));
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
    

    @GetMapping("/{code}")
    public ResponseEntity<APIResponse<CouponResponseDTO>> getCouponByCode(@PathVariable String code) {
        log.info("CouponController::getCouponByCode {}", code);
        CouponResponseDTO couponResponseDTO = couponServiceInterface.getCouponByCode(code);

        APIResponse<CouponResponseDTO> responseDTO = APIResponse
                .<CouponResponseDTO>builder()
                .status(SUCCESS)
                .results(couponResponseDTO)
                .build();

        log.info("CouponController::getCouponByCode {} response {}", code, Mapper.jsonToString(couponResponseDTO));

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping
    public ResponseEntity<APIResponse<CouponResponseDTO>> createCoupon(@RequestBody @Valid CouponRequestDTO couponRequestDTO) {
        log.info("CouponController::createCoupon request body {}", Mapper.jsonToString(couponRequestDTO));

        CouponResponseDTO createdCoupon = couponServiceInterface.createCoupon(couponRequestDTO);

        APIResponse<CouponResponseDTO> responseDTO = APIResponse
                .<CouponResponseDTO>builder()
                .status(SUCCESS)
                .results(createdCoupon)
                .build();

        log.info("ProductController::createNewProduct response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("/{code}")
    public ResponseEntity<APIResponse<CouponResponseDTO>> updateCoupon(
            @PathVariable String code,
            @Valid @RequestBody CouponRequestDTO couponRequestDTO
    ) {
        log.info("CouponController::updateCoupon request body {}", Mapper.jsonToString(couponRequestDTO));

        CouponResponseDTO updatedCoupon = couponServiceInterface.updateCoupon(code, couponRequestDTO);

        APIResponse<CouponResponseDTO> responseDTO = APIResponse
                .<CouponResponseDTO>builder()
                .status(SUCCESS)
                .results(updatedCoupon)
                .build();

        log.info("ProductController::updateCoupon response {}", Mapper.jsonToString(responseDTO));
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<APIResponse<CouponResponseDTO>> disableCoupon(@PathVariable String code) {
        
        CouponResponseDTO disabledCoupon = couponServiceInterface.disableCoupon(code);

        APIResponse<CouponResponseDTO> responseDTO = APIResponse
                .<CouponResponseDTO>builder()
                .status(SUCCESS)
                .results(disabledCoupon)
                .build();
        
        return ResponseEntity.ok(responseDTO);
    }
}
