package com.orderingsystem.orderservice.entities;

import lombok.Data;

@Data
public class Transaction {
    private String transactionId;
    private String userId;
    private String userEmail;
    private String userName;
    private String itemId;
    private String itemName;
    private double itemPrice;
    private String itemPhotoUrl;
    private int quantity;
    private String address;
    private String contactNumber;
    private double totalAmount;

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getTransactionId() {
        return transactionId;
    }



    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPhotoUrl(String itemPhotoUrl) {
        this.itemPhotoUrl = itemPhotoUrl;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPhotoUrl() {
        return itemPhotoUrl;
    }
}
