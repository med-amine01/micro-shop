package de.tekup.message;

import de.tekup.dto.response.OrderResponse;
import de.tekup.dto.response.UserResponse;
import de.tekup.enums.OrderStatus;
import de.tekup.service.MailSenderService;
import de.tekup.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;

@Component
@Slf4j
public class RabbitMqListener {
    
    private static final String QUEUE = "mailing.queue";
    private static final String PENDING_SUBJECT = "Micro-Shop pending order";
    private static final String PLACED_SUBJECT = "Micro-Shop placed order";
    private static final String CANCELED_SUBJECT = "Micro-Shop canceled order";
    
    private final UserService userService;
    private final MailSenderService mailSenderService;
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public RabbitMqListener(UserService userService, MailSenderService mailSenderService) {
        this.userService = userService;
        this.mailSenderService = mailSenderService;
    }
    
    @RabbitListener(queues = QUEUE)
    public void orderListener(OrderResponse order) throws MessagingException {
        try {
            if (order != null) {
                log.info("order fetched from queue id : "+ order.getOrderNumber());
                UserResponse user = userService.getUser(order.getCreatedBy()).block();
                
                if (user != null) {
                    String userEmail = user.getEmail();
                    OrderStatus orderStatus = OrderStatus.valueOf(order.getOrderStatus());
                    
                    String emailSubject;
                    String emailContent;
                    
                    switch (orderStatus) {
                        case PENDING:
                            emailSubject = PENDING_SUBJECT;
                            emailContent = buildPendingOrderEmailContent(order);
                            break;
                        case PLACED:
                            emailSubject = PLACED_SUBJECT;
                            emailContent = buildPlacedOrderEmailContent(order);
                            break;
                        case CANCELED:
                            emailSubject = CANCELED_SUBJECT;
                            emailContent = buildCanceledOrderEmailContent(order);
                            break;
                        default:
                            log.warn("Unknown order status: {}", orderStatus);
                            return;
                    }
                    
                    mailSenderService.sendEmail(userEmail, emailSubject, emailContent);
                    log.info("mail sent to : " + userEmail +"\norder id : " +order.getOrderNumber() + "\norder status : " + orderStatus);
                }
            }
        } catch (Exception exception) {
            log.error("Exception occurred : " + exception.getMessage());
            throw exception;
        }
    }
    
    private String buildPendingOrderEmailContent(OrderResponse order) {
        return processTemplate("pending-order", order);
    }
    
    private String buildPlacedOrderEmailContent(OrderResponse order) {
        return processTemplate("placed-order", order);
    }
    
    private String buildCanceledOrderEmailContent(OrderResponse order) {
        return processTemplate("canceled-order", order);
    }
    
    private String processTemplate(String templateName, OrderResponse order) {
        Context context = new Context();
        context.setVariable("order", order);
        
        return templateEngine.process(templateName, context);
    }

}
