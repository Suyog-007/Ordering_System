package com.orderingsystem.paymentservice.consumer;

import com.orderingsystem.paymentservice.services.PaymentService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentConsumer {

    @Autowired
    private PaymentService paymentService;

    @RabbitListener(queues = "order-queue")
    public void consumeTransaction(String transactionJson) {
        paymentService.processTransaction(transactionJson);
    }
}
