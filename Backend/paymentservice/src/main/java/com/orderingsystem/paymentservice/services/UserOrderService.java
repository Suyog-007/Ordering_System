package com.orderingsystem.paymentservice.services;

import com.orderingsystem.paymentservice.entities.Order;
import com.orderingsystem.paymentservice.entities.UserOrder;
import com.orderingsystem.paymentservice.repositories.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserOrderService {

    @Autowired
    private UserOrderRepository userOrderRepository;

    // Add an order for a specific user
    public void addOrder(String userId, Order order) {
        UserOrder userOrder = userOrderRepository.findByUserId(userId);

        if (userOrder == null) {
            // If no user exists, create a new user order document
            userOrder = new UserOrder(userId, List.of(order));
        } else {
            // If user exists, add the new order to their existing orders list
            userOrder.addOrder(order);
        }

        userOrderRepository.save(userOrder);
    }

    // Get all orders of a user
    public UserOrder getUserOrders(String userId) {
        return userOrderRepository.findByUserId(userId);
    }
    public List<UserOrder> getAllUserOrders() {
        return userOrderRepository.findAll();
    }
}
