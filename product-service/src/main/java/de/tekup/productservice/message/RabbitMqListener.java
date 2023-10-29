package de.tekup.productservice.message;

import de.tekup.productservice.dto.CouponResponse;
import de.tekup.productservice.exception.ProductServiceBusinessException;
import de.tekup.productservice.service.ProductServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListener {
    
    private final ProductServiceInterface productServiceInterface;
    
    private static final String QUEUE = "coupon.queue";
    
    @RabbitListener(queues = QUEUE)
    public void productListener(CouponResponse coupon) {
        try {
            productServiceInterface.updateProductsFromQueue(coupon);
            log.info("coupon fetched from message queue and updated product");
            
        } catch (Exception exception) {
            log.error("Exception occurred while fetching product from queue, Exception message: {}", exception.getMessage());
            throw new ProductServiceBusinessException(exception.getMessage());
        }
    }
}
