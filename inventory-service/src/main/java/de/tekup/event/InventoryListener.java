package de.tekup.event;

import de.tekup.config.RabbitMqConfig;
import de.tekup.entity.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;

@Component
@Slf4j
public class InventoryListener implements PostInsertEventListener, PostUpdateEventListener {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    public InventoryListener(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory.unwrap(SessionFactoryImpl.class) == null) {
            throw new NullPointerException("EntityManagerFactory is not a hibernate factory");
        }
        
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(this);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(this);
    }
    
    @Override
    public void onPostInsert(PostInsertEvent event) {
        try {
            Object entity = event.getEntity();
            if (entity instanceof Inventory) {
                Inventory inventory = (Inventory) entity;
                rabbitTemplate.convertAndSend(RabbitMqConfig.HISTORY_EXCHANGE, RabbitMqConfig.HISTORY_ROUTING_KEY, inventory);
                log.info("pushing to queue = {}", inventory);
                System.err.println("Inventory created with SKU code: " + inventory.getSkuCode());
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
    
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        try {
            Object entity = event.getEntity();
            if (entity instanceof Inventory) {
                Inventory inventory = (Inventory) entity;
                rabbitTemplate.convertAndSend(RabbitMqConfig.HISTORY_EXCHANGE, RabbitMqConfig.HISTORY_ROUTING_KEY, inventory);
                log.info("pushing to queue = {}", inventory);
                System.err.println("Inventory updated with SKU code: " + inventory.getSkuCode());
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
    
    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
