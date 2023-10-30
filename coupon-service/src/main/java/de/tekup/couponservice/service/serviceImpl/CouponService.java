package de.tekup.couponservice.service.serviceImpl;

import de.tekup.couponservice.config.RabbitMqConfig;
import de.tekup.couponservice.dto.CouponRequestDTO;
import de.tekup.couponservice.dto.CouponResponseDTO;
import de.tekup.couponservice.entity.Coupon;
import de.tekup.couponservice.event.CouponCreatedEvent;
import de.tekup.couponservice.event.CouponUpdatedEvent;
import de.tekup.couponservice.exception.CouponAlreadyExistsException;
import de.tekup.couponservice.exception.CouponNotFoundException;
import de.tekup.couponservice.exception.CouponServiceBusinessException;
import de.tekup.couponservice.repository.CouponRepository;
import de.tekup.couponservice.service.CouponServiceInterface;
import de.tekup.couponservice.util.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService implements CouponServiceInterface {

    private final CouponRepository couponRepository;

    private final ApplicationEventPublisher eventPublisher;
    
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Cacheable(value = "coupon")
    public List<CouponResponseDTO> getCoupons() throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCoupons - Fetching Started.");

            List<Coupon> coupons = couponRepository.findAll();

            List<CouponResponseDTO> couponResponseDTOS = coupons.stream()
                    .map(Mapper::toDto)
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
    public CouponResponseDTO getCouponByCode(String code) throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCouponByCode - Fetching Started.");

            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));
            
            CouponResponseDTO couponResponseDTO = Mapper.toDto(coupon);

            log.debug("CouponService::getCouponByCode - Coupon retrieved by code: {} {}", code, Mapper.jsonToString(couponResponseDTO));

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

            if (couponRepository.existsByCode(couponRequestDTO.getCode())) {
                throw new CouponAlreadyExistsException("Coupon with code " + couponRequestDTO.getCode() + " already exists");
            }

            Coupon coupon = Mapper.toEntity(couponRequestDTO);
            Coupon persistedCoupon = couponRepository.save(coupon);

            CouponResponseDTO couponResponseDTO = Mapper.toDto(persistedCoupon);
            log.debug("CouponService::createCoupon - coupon created : {}", Mapper.jsonToString(couponResponseDTO));
            
            eventPublisher.publishEvent(new CouponCreatedEvent(this, couponResponseDTO));
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
    public CouponResponseDTO updateCoupon(String code, CouponRequestDTO updatedCoupon) throws CouponServiceBusinessException {
        try {
            log.info("CouponService::updateCoupon - Started.");
            
            // Fetch the existing coupon and update its properties
            Coupon existingCoupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));

            if (couponRepository.existsByCode(updatedCoupon.getCode()) &&
                !code.equalsIgnoreCase(updatedCoupon.getCode())
            ) {
                throw new CouponAlreadyExistsException("Coupon already exists.");
            }

            // Checking if nothing changed no need to update
            CouponResponseDTO convertResponse = Mapper.toDto(Mapper.toEntity(updatedCoupon));
            if (Mapper.toDto(existingCoupon).equals(convertResponse)) {
                throw new CouponServiceBusinessException("No update needed, nothing changed.");
            }

            boolean flag = true;
            // In case that expiration date changed
            if (!updatedCoupon.getExpirationDate().equalsIgnoreCase(existingCoupon.getExpirationDate())) {
                // Publish event to cancel previous task (old exp-date) and run the new updated task (new exp-date)
                eventPublisher.publishEvent(new CouponUpdatedEvent(
                        this,
                        existingCoupon,
                        updatedCoupon.getExpirationDate()
                ));
                flag = false;
            }

            Coupon coupon = Mapper.toEntity(updatedCoupon);
            coupon.setId(existingCoupon.getId());
            // Update the coupon and convert to response DTO
            CouponResponseDTO couponResponse = Mapper.toDto(couponRepository.save(coupon));
            log.debug("CouponService::updateCoupon - Updated coupon: {}", Mapper.jsonToString(couponResponse));

            // If any other fields got changed : (code name || discount percentage)
            if (flag) {
                rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, couponResponse);
                log.info("pushing updated coupon to queue = {}", couponResponse);
            }

            return couponResponse;
        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (CouponAlreadyExistsException exception) {
            log.error("Error updating the same provided coupon code");
            throw exception;

        } catch (CouponServiceBusinessException exception) {
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while updating coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while updating coupon");
        }
    }

    @Override
    public CouponResponseDTO disableCoupon(String code) {
        try {
            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));
            coupon.setEnabled(false);

            // Disabled coupon
            CouponResponseDTO couponResponse = Mapper.toDto(couponRepository.save(coupon));

            // In case that coupon is disabled -> notify product service
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTING_KEY, couponResponse);
            log.info("pushing disabled coupon to queue = {}", couponResponse);

            return couponResponse;
        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            if (exception.getClass().getName().contains("CachingConnectionFactory")) {
                log.error("Queue error occurred : " + exception.getMessage());
                throw new CouponServiceBusinessException("Couldn't connect to rabbitMq");
            }

            log.error("Exception occurred while disabling coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while disabling a coupon");
        }
    }
    
}
