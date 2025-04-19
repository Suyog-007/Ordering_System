package com.orderingsystem.paymentservice.entities;

public class OrderDetails {

    private String itemName;
    private int quantity;
    private double totalAmount;
    private String address;
    private String contactNumber;
    private String itemPhotoUrl;

    // Constructor
    public OrderDetails(String itemName, int quantity, double totalAmount, String address, String contactNumber, String itemPhotoUrl) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.address = address;
        this.contactNumber = contactNumber;
        this.itemPhotoUrl = itemPhotoUrl;
    }

    // Getters and Setters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getItemPhotoUrl() {
        return itemPhotoUrl;
    }

    public void setItemPhotoUrl(String itemPhotoUrl) {
        this.itemPhotoUrl = itemPhotoUrl;
    }
}

