package com.orderingsystem.paymentservice.dtos;



public class CheckoutResponse {
    private String sessionId;
    private String redirectUrl;

    public CheckoutResponse(String sessionId, String redirectUrl) {
        this.sessionId = sessionId;
        this.redirectUrl = redirectUrl;
    }

    // Getters and setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
