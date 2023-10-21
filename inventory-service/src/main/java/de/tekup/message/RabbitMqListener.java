package de.tekup.message;

import de.tekup.dto.ProductDTO;
import de.tekup.exception.InventoryServiceException;
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
        try {
            inventoryService.initQuantityFromQueue(product.getSkuCode());
            log.info("product fetched from message queue and saved in inventory");

        } catch (Exception exception) {
            log.error("Exception occurred while fetching product from queue, Exception message: {}", exception.getMessage());
            throw new InventoryServiceException(exception.getMessage());
        }
    }
}
