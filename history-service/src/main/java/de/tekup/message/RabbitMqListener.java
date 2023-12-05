package de.tekup.message;

import de.tekup.entity.Inventory;
import de.tekup.entity.Order;
import de.tekup.service.InventoryService;
import de.tekup.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMqListener {
    
    private static final String INVENTORY ="history.inventory.queue";
    private static final String ORDER ="history.order.queue";
    
    private final InventoryService inventoryService;
    private final OrderService orderService;
    
    @RabbitListener(queues = INVENTORY)
    public void inventoryListener(Inventory inventory) {
        try {
            Inventory persistedInventory = inventoryService.saveInventory(inventory);
            log.info("Inventory fetched from message queue and saved : {}", persistedInventory);
            
        } catch (Exception exception) {
            log.error("Exception occurred while fetching inventory from queue, Exception message: {}", exception.getMessage());
            throw exception;
        }
    }
    
    @RabbitListener(queues = ORDER)
    public void orderListener(Order order) {
        try {
            Order persistedOrder = orderService.saveOrder(order);
            log.info("Order fetched from message queue and saved : {}", persistedOrder);
            
        } catch (Exception exception) {
            log.error("Exception occurred while fetching product from queue, Exception message: {}", exception.getMessage());
            throw exception;
        }
    }
}
