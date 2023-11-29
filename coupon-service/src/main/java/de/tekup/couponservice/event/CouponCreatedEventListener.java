package de.tekup.couponservice.event;

import de.tekup.couponservice.dto.response.CouponResponse;
import de.tekup.couponservice.service.serviceImpl.CouponSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponCreatedEventListener implements ApplicationListener<CouponCreatedEvent> {

    private final CouponSchedulerService couponSchedulerService;
    
    @Override
    public void onApplicationEvent(CouponCreatedEvent event) {
        CouponResponse coupon = event.getCouponResponse();
        couponSchedulerService.saveTaskToRedis(coupon.getCode(), coupon.getExpirationDate());
        couponSchedulerService.prepare2ExecuteTasks();
    }
}
