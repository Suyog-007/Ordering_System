package com.orderingsystem.orderservice.services;




import com.orderingsystem.orderservice.entities.Transaction;
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

    public void sendTransaction(Transaction transaction) {
        rabbitTemplate.convertAndSend(exchange, routingKey, transaction);
        System.out.println("Message sent: " + transaction);
    }
}
