package de.tekup.message;

import de.tekup.dto.ProductDTO;
import de.tekup.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListener {
    
    private static final String QUEUE = "product.queue";
    
    private final InventoryService inventoryService;
    
    @RabbitListener(queues = QUEUE)
    public void productListener(ProductDTO product) {
        inventoryService.initQuantityFromQueue(product.getSkuCode());
        log.info("product fetched from message queue and saved in inventory");
    }
}
