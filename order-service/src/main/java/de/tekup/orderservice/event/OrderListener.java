package de.tekup.orderservice.event;

import de.tekup.orderservice.config.RabbitMqConfig;
import de.tekup.orderservice.entity.Order;
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
public class OrderListener implements PostInsertEventListener, PostUpdateEventListener {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    public OrderListener(EntityManagerFactory entityManagerFactory) {
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
            if (entity instanceof Order) {
                Order order = (Order) entity;
                rabbitTemplate.convertAndSend(RabbitMqConfig.HISTORY_EXCHANGE, RabbitMqConfig.HISTORY_ROUTING_KEY, order);
                log.info("pushing to queue (create) = {}", order);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
    
    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        try {
            Object entity = event.getEntity();
            if (entity instanceof Order) {
                Order order = (Order) entity;
                rabbitTemplate.convertAndSend(RabbitMqConfig.HISTORY_EXCHANGE, RabbitMqConfig.HISTORY_ROUTING_KEY, order);
                log.info("pushing to queue (update) = {}", order);
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
