package com.orderingsystem.paymentservice.services;




import com.orderingsystem.paymentservice.entities.OrderNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    private String exchange = "notification-exchange";

    private String routingKey ="order-notification-routing-key";

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    public void sendRegisterNotification(OrderNotification orderNotification) {
        rabbitTemplate.convertAndSend(exchange, routingKey, orderNotification);
        System.out.println("Message sent: " + orderNotification);
    }
}
