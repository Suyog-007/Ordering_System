package com.orderingsystem.paymentservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderingsystem.paymentservice.entities.*;
import com.orderingsystem.paymentservice.repositories.PaymentRepository;
import com.orderingsystem.paymentservice.repositories.UserOrderRepository;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.stripe.param.PaymentIntentCreateParams;

import com.orderingsystem.paymentservice.services.RabbitMQSender;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;


    @Autowired
    private UserOrderRepository userOrderRepository;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    public void processTransaction(String transactionJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(transactionJson);
            System.out.println(json);

            String userId = json.get("userId").asText();
            String transactionId = json.get("transactionId").asText();
            double amount = json.get("totalAmount").asDouble();


            // 2. Create and confirm the PaymentIntent
            Customer customer = Customer.create(
                    CustomerCreateParams.builder()
                            .setEmail(json.get("userEmail").asText())
                            .build()
            );
            PaymentMethod paymentMethod = PaymentMethod.retrieve(json.get("cardNumber").asText());
            paymentMethod.attach(
                    PaymentMethodAttachParams.builder()
                            .setCustomer(customer.getId())
                            .build()
            );
            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100)) // Convert INR to paise
                    .setCurrency("inr")
                    .setCustomer(customer.getId())
                    .setPaymentMethod(json.get("cardNumber").asText())
                    .setConfirm(true) // Confirm immediately
                    .build();

            PaymentIntent intent = PaymentIntent.create(createParams);

            String paymentId = intent.getId();
            String status = intent.getStatus();

            // 3. Save to RDB
            Payment payment = new Payment();
            payment.setPaymentId(paymentId);
            payment.setTransactionId(transactionId);
            payment.setUserId(userId);
            payment.setAmount(amount);
            payment.setStatus(status);
            payment.setTimestamp(LocalDateTime.now());
            paymentRepo.save(payment);

            if ("succeeded".equals(status)) {
                PaymentDetails paymentDetails = new PaymentDetails(paymentId,LocalDateTime.now().toString());
                OrderDetails orderDetails = new OrderDetails(
                        json.get("itemName").asText(),
                        json.get("quantity").asInt(),
                        amount,
                        json.get("address").asText(),
                        json.get("contactNumber").asText(),
                        json.get("itemPhotoUrl").asText()
                );
                Order order = new Order(transactionId, transactionId, orderDetails, paymentDetails);
                UserOrder userOrder = userOrderRepository.findByUserId(userId);

                if(userOrder == null){
                    List<Order> orders = new ArrayList<>();
                    orders.add(order);
                    userOrder = new UserOrder(userId, orders);
                } else {
                    // If the user exists, add the order to their list
                    userOrder.addOrder(order);
                }
                OrderNotification orderNotification = new OrderNotification();
                orderNotification.setTransactionId(transactionId);
                orderNotification.setUserId(userId);
                orderNotification.setUserEmail(json.get("userEmail").asText());
                orderNotification.setType("Order Notification");


                userOrderRepository.save(userOrder);
                rabbitMQSender.sendRegisterNotification(orderNotification);
            }

        } catch (Exception e) {
//            System.out.println(e);
            e.printStackTrace();
        }
    }
    public Payment getAllPaymentsId(String userId) {
        return paymentRepo.findByUserId(userId);
    }
    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

}
