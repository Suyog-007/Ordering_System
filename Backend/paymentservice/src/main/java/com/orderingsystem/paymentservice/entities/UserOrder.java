package com.orderingsystem.paymentservice.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "user_orders")
public class UserOrder {

    @Id
    private String userId;
    private List<Order> orders;

    // Constructor
    public UserOrder(String userId, List<Order> orders) {
        this.userId = userId;
        this.orders = orders;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    // Adding order to the list
    public void addOrder(Order order) {
        this.orders.add(order);
    }
}

