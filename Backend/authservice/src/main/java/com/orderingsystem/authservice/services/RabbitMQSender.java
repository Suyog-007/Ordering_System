package com.orderingsystem.authservice.services;

import com.orderingsystem.authservice.entities.RegisterNotification;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQSender {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendRegisterNotification(RegisterNotification registerNotification) {
        rabbitTemplate.convertAndSend(exchange, routingKey, registerNotification);
        System.out.println("Message sent: " + registerNotification);
    }
}
