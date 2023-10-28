package de.tekup.couponservice.event;

import de.tekup.couponservice.entity.Coupon;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CouponUpdatedEvent extends ApplicationEvent {
    
    private Coupon coupon;
    private String newExpDate;
    
    public CouponUpdatedEvent(Object source, Coupon coupon, String newExpDate) {
        super(source);
        this.coupon = coupon;
        this.newExpDate = newExpDate;
    }
}
