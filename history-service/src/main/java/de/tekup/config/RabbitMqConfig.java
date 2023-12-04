package de.tekup.config;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Data
public class RabbitMqConfig {
    
    public static String INVENTORY_HISTORY_QUEUE;
    public static String INVENTORY_HISTORY_EXCHANGE;
    public static String INVENTORY_HISTORY_ROUTING_KEY;
    
    @Value("${rabbitmq.history.inventory.queue}")
    private String inventoryHistoryQueue;
    @Value("${rabbitmq.history.inventory.exchange}")
    private String inventoryHistoryExchange;
    @Value("${rabbitmq.history.inventory.routing-key}")
    private String inventoryHistoryRoutingKey;
    
    public static String ORDER_HISTORY_QUEUE;
    public static String ORDER_HISTORY_EXCHANGE;
    public static String ORDER_HISTORY_ROUTING_KEY;
    
    @Value("${rabbitmq.history.order.queue}")
    private String orderHistoryQueue;
    @Value("${rabbitmq.history.order.exchange}")
    private String orderHistoryExchange;
    @Value("${rabbitmq.history.order.routing-key}")
    private String orderHistoryRoutingKey;
    
    // Inventory History Queue
    @Bean
    public Queue inventoryHistoryQueue() {
        return new Queue(inventoryHistoryQueue);
    }
    
    @Bean
    public TopicExchange inventoryHistoryExchange() {
        return new TopicExchange(inventoryHistoryExchange);
    }
    
    @Bean
    public Binding inventoryHistoryBinding(Queue inventoryHistoryQueue, TopicExchange inventoryHistoryExchange) {
        return BindingBuilder
                .bind(inventoryHistoryQueue)
                .to(inventoryHistoryExchange)
                .with(inventoryHistoryRoutingKey);
    }
    
    // Order History Queue
    @Bean
    public Queue orderHistoryQueue() {
        return new Queue(orderHistoryQueue);
    }
    
    @Bean
    public TopicExchange orderHistoryExchange() {
        return new TopicExchange(orderHistoryExchange);
    }
    
    @Bean
    public Binding orderHistoryBinding(Queue orderHistoryQueue, TopicExchange orderHistoryExchange) {
        return BindingBuilder
                .bind(orderHistoryQueue)
                .to(orderHistoryExchange)
                .with(inventoryHistoryRoutingKey);
    }
    
    @Bean
    public MessageConverter getMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(getMessageConverter());
        
        return template;
    }
    
    @PostConstruct
    public void setValues() {
        // Inventory history config
        INVENTORY_HISTORY_QUEUE = this.inventoryHistoryQueue;
        INVENTORY_HISTORY_EXCHANGE = this.inventoryHistoryExchange;
        INVENTORY_HISTORY_ROUTING_KEY = this.inventoryHistoryRoutingKey;
        
        // Order history config
        ORDER_HISTORY_QUEUE = this.orderHistoryQueue;
        ORDER_HISTORY_EXCHANGE = this.orderHistoryExchange;
        ORDER_HISTORY_ROUTING_KEY = this.orderHistoryRoutingKey;
    }
}
