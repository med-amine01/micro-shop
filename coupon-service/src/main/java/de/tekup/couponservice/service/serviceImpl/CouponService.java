package de.tekup.couponservice.service.serviceImpl;

import de.tekup.couponservice.dto.CouponRequestDTO;
import de.tekup.couponservice.dto.CouponResponseDTO;
import de.tekup.couponservice.entity.Coupon;
import de.tekup.couponservice.exception.CouponAlreadyExistsException;
import de.tekup.couponservice.exception.CouponNotFoundException;
import de.tekup.couponservice.exception.CouponServiceBusinessException;
import de.tekup.couponservice.repository.CouponRepository;
import de.tekup.couponservice.service.CouponServiceInterface;
import de.tekup.couponservice.util.ValueMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CouponService implements CouponServiceInterface {

    private CouponRepository couponRepository;

    @Override
    @Cacheable(value = "coupon")
    public List<CouponResponseDTO> getCoupons() throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCoupons - Fetching Started.");

            List<Coupon> coupons = couponRepository.findAll();

            List<CouponResponseDTO> couponResponseDTOS = coupons.stream()
                    .map(ValueMapper::convertToDto)
                    .toList();

            log.info("CouponService::getCoupons - Fetched {} coupons", couponResponseDTOS.size());

            log.info("CouponService::getCoupons - Fetching Ends.");
            return couponResponseDTOS;

        } catch (Exception exception) {
            log.error("Exception occurred while retrieving coupons, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while fetching all coupons");
        }
    }

    @Override
    @Cacheable(value = "coupon")
    public CouponResponseDTO getCouponById(Long id) throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCouponById - Fetching Started.");

            Coupon coupon = couponRepository.findById(id)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with ID " + id + " not found"));

            CouponResponseDTO couponResponseDTO = ValueMapper.convertToDto(coupon);

            log.debug("CouponService::getCouponById - Coupon retrieved by ID: {} {}", id, ValueMapper.jsonToString(couponResponseDTO));

            log.info("CouponService::getCouponById - Fetching Ends.");

            return couponResponseDTO;

        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while fetching coupon by id");
        }
    }

    @Override
    @Cacheable(value = "coupon")
    public CouponResponseDTO getCouponByCode(String code) throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCouponByCode - Fetching Started.");

            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));

            CouponResponseDTO couponResponseDTO = ValueMapper.convertToDto(coupon);

            log.debug("CouponService::getCouponByCode - Coupon retrieved by code: {} {}", code, ValueMapper.jsonToString(couponResponseDTO));

            log.info("CouponService::getCouponByCode - Fetching Ends.");
            return couponResponseDTO;

        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while fetching coupon by code");
        }
    }

    @Override
    public CouponResponseDTO createCoupon(CouponRequestDTO couponRequestDTO) {
        try {
            log.info("CouponService::createCoupon - STARTED.");

            if (couponRepository.existsByCode(couponRequestDTO.getCode())) {
                throw new CouponAlreadyExistsException("Coupon with code " + couponRequestDTO.getCode() + " already exists");
            }

            Coupon coupon = ValueMapper.convertToEntity(couponRequestDTO);
            Coupon persistedCoupon = couponRepository.save(coupon);

            CouponResponseDTO couponResponseDTO = ValueMapper.convertToDto(persistedCoupon);
            log.debug("CouponService::createCoupon - coupon created : {}", ValueMapper.jsonToString(couponResponseDTO));

            log.info("CouponService::createCoupon - ENDS.");
            return couponResponseDTO;

        } catch (CouponAlreadyExistsException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while persisting coupon, Exception message {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while creating a new coupon");
        }
    }

    @Override
    public CouponResponseDTO updateCoupon(Long id, CouponRequestDTO updatedCoupon) throws CouponServiceBusinessException {
        try {
            log.info("CouponService::updateCoupon - Started.");

            Coupon coupon = ValueMapper.convertToEntity(updatedCoupon);

            // Fetch the existing coupon and update its properties
            CouponResponseDTO existingCoupon = getCouponById(id);
            coupon.setId(id);
            coupon.setCreatedAt(existingCoupon.getCreatedAt());

            // Update the coupon and convert to response DTO
            Coupon persistedCoupon = couponRepository.save(coupon);
            CouponResponseDTO couponResponseDTO = ValueMapper.convertToDto(persistedCoupon);

            log.debug("CouponService::updateCoupon - Updated coupon: {}", ValueMapper.jsonToString(couponResponseDTO));
            log.info("CouponService::updateCoupon - Completed for ID: {}", id);

            return couponResponseDTO;

        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;
            
        } catch (Exception exception) {
            log.error("Exception occurred while updating coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while updating coupon");
        }
    }

    @Override
    public void deleteCoupon(Long id) {
        log.info("CouponService::deleteCoupon - Starts.");

        try {
            log.info("CouponService::deleteCoupon - Deleting coupon with ID: {}", id);
            
            Coupon coupon = couponRepository.findById(id)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with ID " + id + " not found"));
            couponRepository.delete(coupon);
            
            log.info("CouponService::deleteCoupon - Deleted coupon with ID: {}", id);
        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while deleting coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while deleting a coupon");
        }

        log.info("CouponService::deleteCoupon - Ends.");
    }

}
