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
    
    public static String PRODUCT_QUEUE;
    public static String PRODUCT_EXCHANGE;
    public static String PRODUCT_ROUTING_KEY;
    
    public static String HISTORY_QUEUE;
    public static String HISTORY_EXCHANGE;
    public static String HISTORY_ROUTING_KEY;
    
    @Value("${rabbitmq.product.queue}")
    private String productQueue;
    @Value("${rabbitmq.product.exchange}")
    private String productExchange;
    @Value("${rabbitmq.product.routing-key}")
    private String productRoutingKey;
    
    @Value("${rabbitmq.history.queue}")
    private String historyQueue;
    @Value("${rabbitmq.history.exchange}")
    private String historyExchange;
    @Value("${rabbitmq.history.routing-key}")
    private String historyRoutingKey;
    
    @Bean
    public Queue queue() {
        return new Queue(productQueue);
    }
    
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(productExchange);
    }
    
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(productRoutingKey);
    }
    
    @Bean
    public Queue inventoryHistoryQueue() {
        return new Queue(historyQueue);
    }
    
    @Bean
    public TopicExchange inventoryHistoryExchange() {
        return new TopicExchange(historyExchange);
    }
    
    @Bean
    public Binding inventoryHistoryBinding(Queue inventoryHistoryQueue, TopicExchange inventoryHistoryExchange) {
        return BindingBuilder
                .bind(inventoryHistoryQueue)
                .to(inventoryHistoryExchange)
                .with(historyRoutingKey);
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
        // Product config
        PRODUCT_QUEUE = this.productQueue;
        PRODUCT_EXCHANGE = this.productExchange;
        PRODUCT_ROUTING_KEY = this.productRoutingKey;
        
        // History config
        HISTORY_QUEUE = this.historyQueue;
        HISTORY_EXCHANGE = this.historyExchange;
        HISTORY_ROUTING_KEY = this.historyRoutingKey;
    }
}
