package com.orderingsystem.orderservice.controllers;






import com.orderingsystem.orderservice.dtos.OrderTransactionDto;
import com.orderingsystem.orderservice.entities.OrderRequest;
import com.orderingsystem.orderservice.entities.Transaction;
import com.orderingsystem.orderservice.services.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @PostMapping
    public String createOrder(@RequestBody OrderTransactionDto orderTransactionDto) throws Exception {
        OrderRequest orderRequest = orderTransactionDto.getOrderRequest();
        Transaction transaction = orderTransactionDto.getTransaction();
//        System.out.println(orderRequest);
//        System.out.println("Received transaction: " + transaction);
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction is null");
        }
        // Calculate total amount
        double totalAmount = transaction.getItemPrice() * orderRequest.getQuantity();

        transaction.setTransactionId(UUID.randomUUID().toString());

        transaction.setUserId(transaction.getUserId());
        transaction.setUserEmail(transaction.getUserEmail());
        transaction.setUserName(transaction.getUserName());

        transaction.setItemId(transaction.getItemId());
        transaction.setItemPrice(transaction.getItemPrice());
        transaction.setItemName(transaction.getItemName());
        transaction.setItemPhotoUrl(transaction.getItemPhotoUrl());

        transaction.setQuantity(orderRequest.getQuantity());
        transaction.setAddress(orderRequest.getAddress());
        transaction.setContactNumber(orderRequest.getContactNumber());
        transaction.setTotalAmount(totalAmount);

        // Send the transaction to RabbitMQ
        rabbitMQSender.sendTransaction(transaction);

        return transaction.getTransactionId();
    }
}
