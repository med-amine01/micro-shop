package de.tekup.orderservice.config;

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
    
    public static String MAILING_QUEUE;
    public static String MAILING_EXCHANGE;
    public static String MAILING_ROUTING_KEY;
    
    public static String HISTORY_QUEUE;
    public static String HISTORY_EXCHANGE;
    public static String HISTORY_ROUTING_KEY;
    
    @Value("${rabbitmq.mailing.queue}")
    private String mailingQueue;
    @Value("${rabbitmq.mailing.exchange}")
    private String mailingExchange;
    @Value("${rabbitmq.mailing.routing-key}")
    private String mailingRoutingKey;
    
    @Value("${rabbitmq.history.queue}")
    private String historyQueue;
    @Value("${rabbitmq.history.exchange}")
    private String historyExchange;
    @Value("${rabbitmq.history.routing-key}")
    private String historyRoutingKey;
    
    private AmqpAdmin amqpAdmin;
    
    public RabbitMqConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
    
    @Bean
    public void initializeQueue() {
        amqpAdmin.declareQueue(queue());
    }
    
    @Bean
    public Queue queue() {
        return new Queue(mailingQueue);
    }
    
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(mailingExchange);
    }
    
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(mailingRoutingKey);
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
        // Mailing config
        MAILING_QUEUE = this.mailingQueue;
        MAILING_EXCHANGE = this.mailingExchange;
        MAILING_ROUTING_KEY = this.mailingRoutingKey;
        
        // History config
        HISTORY_QUEUE = this.historyQueue;
        HISTORY_EXCHANGE = this.historyExchange;
        HISTORY_ROUTING_KEY = this.historyRoutingKey;
    }
}
