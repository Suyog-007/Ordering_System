package com.orderingsystem.paymentservice.controller;



import com.orderingsystem.paymentservice.entities.Order;
import com.orderingsystem.paymentservice.entities.Payment;
import com.orderingsystem.paymentservice.entities.UserOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.orderingsystem.paymentservice.services.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @GetMapping("/{userId}")
    public Payment getPayment(@PathVariable String userId) {
        return paymentService.getAllPaymentsId(userId);
    }
    @GetMapping("/all")
    public List<Payment> getAllOrders() {
        return paymentService.getAllPayments();
    }

}