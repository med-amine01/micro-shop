package de.tekup.couponservice.config;

import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RabbitMqConfig {
    
    @Value("${rabbitmq.queue}")
    private String queue;
    
    @Value("${rabbitmq.exchange}")
    private String exchange;
    
    @Value("${rabbitmq.routing-key}")
    private String routingKey;
    
    public static String QUEUE;
    public static String EXCHANGE;
    public static String ROUTING_KEY;
    
    private AmqpAdmin amqpAdmin;

    public RabbitMqConfig(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
    
    @Value("${rabbitmq.queue}")
    public void setQueue(String queue) {
        RabbitMqConfig.QUEUE = queue;
    }
    
    @Value("${rabbitmq.exchange}")
    public void setExchange(String exchange) {
        RabbitMqConfig.EXCHANGE = exchange;
    }
    
    @Value("${rabbitmq.routing-key}")
    public void setRoutingKey(String routingKey) {
        RabbitMqConfig.ROUTING_KEY = routingKey;
    }
    
    @Bean
    // amqpAdmin.declareQueue(new Queue(getQueue(), true));
    public void initializeQueue() {
        amqpAdmin.declareQueue(queue());
    }

    @Bean
    public Queue queue() {
        return new Queue(queue);
    }
    
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }
    
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(routingKey);
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
}
