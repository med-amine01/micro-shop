package de.tekup.couponservice.event;

import de.tekup.couponservice.dto.CouponResponseDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CouponCreatedEvent extends ApplicationEvent {
    
    private CouponResponseDTO couponResponse;
    
    public CouponCreatedEvent(Object source, CouponResponseDTO couponResponse) {
        super(source);
        this.couponResponse = couponResponse;
    }
}
