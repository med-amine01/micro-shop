package de.tekup.couponservice.event;

import de.tekup.couponservice.dto.CouponResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CouponCreatedEvent extends ApplicationEvent {
    
    private CouponResponse couponResponse;
    
    public CouponCreatedEvent(Object source, CouponResponse couponResponse) {
        super(source);
        this.couponResponse = couponResponse;
    }
}
