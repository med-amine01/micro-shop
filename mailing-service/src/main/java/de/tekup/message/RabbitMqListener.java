package de.tekup.message;

import de.tekup.dto.response.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMqListener {
    
    private static final String QUEUE = "mailing.queue";
    
    @RabbitListener(queues = QUEUE)
    public void orderListener(OrderResponse order) {
        try {
            System.err.println(order);
            //System.err.println("order number " + order.getOrderNumber());
        } catch (Exception exception) {
            log.error("Exception occurred, Exception message: {}", exception.getMessage());
            throw exception;
        }
    }
}
