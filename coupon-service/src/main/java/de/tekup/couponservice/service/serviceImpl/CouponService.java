package de.tekup.couponservice.service.serviceImpl;

import de.tekup.couponservice.config.RabbitMqConfig;
import de.tekup.couponservice.dto.request.CouponRequest;
import de.tekup.couponservice.dto.request.CouponUpdateRequest;
import de.tekup.couponservice.dto.response.CouponResponse;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService implements CouponServiceInterface {

    private final CouponRepository couponRepository;

    private final ApplicationEventPublisher eventPublisher;
    
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Cacheable(value = "coupon")
    public List<CouponResponse> getCoupons() throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCoupons - Fetching Started.");

            List<Coupon> coupons = couponRepository.findAll();

            List<CouponResponse> couponResponses = coupons.stream()
                    .map(Mapper::toDto)
                    .toList();

            log.info("CouponService::getCoupons - Fetched {} coupons", couponResponses.size());

            log.info("CouponService::getCoupons - Fetching Ends.");
            return couponResponses;

        } catch (Exception exception) {
            log.error("Exception occurred while retrieving coupons, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while fetching all coupons");
        }
    }

    @Override
    @Cacheable(value = "coupon")
    public CouponResponse getCouponByCode(String code) throws CouponServiceBusinessException {
        try {
            log.info("CouponService::getCouponByCode - Fetching Started.");

            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));
            
            CouponResponse couponResponse = Mapper.toDto(coupon);

            log.debug("CouponService::getCouponByCode - Coupon retrieved by code: {} {}", code, Mapper.jsonToString(couponResponse));

            log.info("CouponService::getCouponByCode - Fetching Ends.");
            return couponResponse;

        } catch (CouponNotFoundException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while retrieving Coupon, Exception message: {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while fetching coupon by code");
        }
    }

    @Override
    public CouponResponse createCoupon(CouponRequest couponRequest) {
        try {
            // Map it to entity and save it
            Coupon coupon = Mapper.toEntity(couponRequest);
            // Generate unique coupon code based on "name" and "discount"
            coupon.setCode(generateCouponCode(coupon.getName(), coupon.getDiscount()));

            Coupon persistedCoupon = couponRepository.save(coupon);

            CouponResponse couponResponse = Mapper.toDto(persistedCoupon);
            log.debug("Coupon created : {}", Mapper.jsonToString(couponResponse));
            
            // Notify event scheduler to take expiration date and set it to scheduler
            eventPublisher.publishEvent(new CouponCreatedEvent(this, couponResponse));

            return couponResponse;
        } catch (CouponAlreadyExistsException exception) {
            log.error(exception.getMessage());
            throw exception;

        } catch (Exception exception) {
            log.error("Exception occurred while persisting coupon, Exception message {}", exception.getMessage());
            throw new CouponServiceBusinessException("Exception occurred while creating a new coupon");
        }
    }

    @Override
    public CouponResponse updateCoupon(String code, CouponUpdateRequest updatedCoupon) throws CouponServiceBusinessException {
        try {
            // Fetch the existing coupon by code
            Coupon existingCoupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));

            // Checking if nothing changed no need to update
            CouponResponse convertResponse = Mapper.toDto(Mapper.toEntity(updatedCoupon, existingCoupon));
            if (Mapper.toDto(existingCoupon).equals(convertResponse)) {
                throw new CouponServiceBusinessException("No update needed, nothing changed.");
            }

            // This flag will let us know if discount percentage changed or not
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

            Coupon coupon = Mapper.toEntity(updatedCoupon, existingCoupon);
            coupon.setId(existingCoupon.getId());

            // Update the coupon and convert to response DTO
            CouponResponse couponResponse = Mapper.toDto(couponRepository.save(coupon));
            log.debug("CouponService::updateCoupon - Updated coupon: {}", Mapper.jsonToString(couponResponse));

            // Discount percentage changed
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
    public CouponResponse disableCoupon(String code) {
        try {
            Coupon coupon = couponRepository.findByCode(code)
                    .orElseThrow(() -> new CouponNotFoundException("Coupon with code " + code + " not found"));
            coupon.setEnabled(false);

            // Disable coupon
            CouponResponse couponResponse = Mapper.toDto(couponRepository.save(coupon));

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

    private String generateCouponCode(String name, BigDecimal discount) {
        String discountString = discount.setScale(0, BigDecimal.ROUND_DOWN).toString();
        String randomUUID = UUID.randomUUID().toString().substring(0, 4);
        
        return name
                .toUpperCase()
                .replace(" ", "")
                .concat("-" + discountString + "-" + randomUUID);
    }
}
