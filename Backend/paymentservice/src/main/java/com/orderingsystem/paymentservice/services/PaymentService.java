package com.orderingsystem.paymentservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderingsystem.paymentservice.entities.*;
import com.orderingsystem.paymentservice.repositories.PaymentRepository;
import com.orderingsystem.paymentservice.repositories.UserOrderRepository;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.stripe.param.PaymentIntentCreateParams;


@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepo;


    @Autowired
    private UserOrderRepository userOrderRepository;

    public void processTransaction(String transactionJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(transactionJson);
            System.out.println(json);

            String userId = json.get("userId").asText();
            String transactionId = json.get("transactionId").asText();
            double amount = json.get("totalAmount").asDouble();

            // 1. Create test card as PaymentMethod (Stripe test card)
//            PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
//                    .setType(PaymentMethodCreateParams.Type.CARD)
//                    .setCard(
//                            PaymentMethodCreateParams.CardDetails.builder()
//                                    .setNumber("4000056655665556")
//                                    .setExpMonth(12L)
//                                    .setExpYear(2026L)
//                                    .setCvc("123")
//                                    .build()
//                    )
//                    .build();
//
//            PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

            // 2. Create and confirm the PaymentIntent
            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100)) // Convert INR to paise
                    .setCurrency("inr")
                    .setPaymentMethod("pm_card_visa")
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
                userOrderRepository.save(userOrder);
            }

        } catch (Exception e) {
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
