package com.orderingsystem.paymentservice.entities;

public class PaymentDetails {

    private String paymentId;
    private String timestamp;

    // Constructor
    public PaymentDetails(String paymentId, String timestamp) {
        this.paymentId = paymentId;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

