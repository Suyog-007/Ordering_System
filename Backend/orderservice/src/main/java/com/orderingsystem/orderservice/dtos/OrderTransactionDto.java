package com.orderingsystem.orderservice.dtos;

import com.orderingsystem.orderservice.entities.OrderRequest;
import com.orderingsystem.orderservice.entities.Transaction;

public class OrderTransactionDto {
    private OrderRequest orderRequest;
    private Transaction transaction;

    public  OrderRequest getOrderRequest() {
        return orderRequest;
    }

    public void setOrderRequest(OrderRequest orderRequest) {
        this.orderRequest = orderRequest;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
