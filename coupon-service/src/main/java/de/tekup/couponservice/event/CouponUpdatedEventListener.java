package de.tekup.couponservice.event;

import de.tekup.couponservice.entity.Coupon;
import de.tekup.couponservice.service.serviceImpl.CouponSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponUpdatedEventListener implements ApplicationListener<CouponUpdatedEvent> {

    private final CouponSchedulerService couponSchedulerService;
    
    @Override
    public void onApplicationEvent(CouponUpdatedEvent event) {
        Coupon coupon = event.getCoupon();
        couponSchedulerService.cancelScheduledTask(coupon.getCode());
        couponSchedulerService.add2ExpirationDateMap(coupon.getCode(), event.getNewExpDate());
        couponSchedulerService.prepare2ExecuteTasks();
    }
}
