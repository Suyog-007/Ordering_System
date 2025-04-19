package com.orderingsystem.paymentservice.controller;

import com.orderingsystem.paymentservice.entities.Order;
import com.orderingsystem.paymentservice.entities.UserOrder;
import com.orderingsystem.paymentservice.services.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    @PostMapping("/{userId}")
    public String addOrder(@PathVariable String userId, @RequestBody Order order) {
        userOrderService.addOrder(userId, order);
        return "Order added successfully!";
    }

    @GetMapping("/{userId}")
    public UserOrder getOrders(@PathVariable String userId) {
        return userOrderService.getUserOrders(userId);
    }
    @GetMapping("/all")
    public List<UserOrder> getAllOrders() {
        return userOrderService.getAllUserOrders();
    }
}

