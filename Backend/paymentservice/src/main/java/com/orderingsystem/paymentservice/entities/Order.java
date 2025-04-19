package com.orderingsystem.paymentservice.entities;

public class Order {

    private String id;
    private String transactionId;
    private OrderDetails orderDetails;
    private PaymentDetails paymentDetails;

    // Constructor
    public Order(String id, String transactionId, OrderDetails orderDetails, PaymentDetails paymentDetails) {
        this.id = id;
        this.transactionId = transactionId;
        this.orderDetails = orderDetails;
        this.paymentDetails = paymentDetails;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
}

