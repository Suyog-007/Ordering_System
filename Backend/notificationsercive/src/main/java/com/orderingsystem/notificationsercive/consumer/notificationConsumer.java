package com.orderingsystem.notificationsercive.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class notificationConsumer {

    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    @Autowired
    public notificationConsumer(JavaMailSender mailSender, RestTemplate restTemplate) {
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
    }


    @RabbitListener(queues ="notification-queue")
    public void consumeNotification(String notificationJson) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(notificationJson);
        String type = json.get("type").asText();
        switch (type) {
            case "Register Notification":
                sendWelcomeEmail(json.get("email").asText(), json.get("fullName").asText());
                break;
            case "Order Notification":
                processOrderNotification(json.get("userId").asText(), json.get("transactionId").asText(), json.get("userEmail").asText());
                break;
        }
    }

    private void sendWelcomeEmail(String email, String fullName) {
        String subject = "Welcome to Our Store!";
        String body = "Hi " + fullName + ",\n\nThanks for registering. We're glad to have you!\n\n– The Team";

        sendEmail(email, subject, body);
    }
private void processOrderNotification(String userId,String transactionId,String email){
    try {
        String url = "http://localhost:8080/api/transaction/" + userId;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("❌ Failed to fetch transaction data");
            return;
        }

        String jsonBody = response.getBody();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(jsonBody);

        JsonNode orders = root.path("orders");
        if (orders.isMissingNode() || !orders.isArray()) {
            System.out.println("❌ 'orders' not found or is not an array.");
            return;
        }

        for (JsonNode orderNode : orders) {
            String currentTxnId = orderNode.path("transactionId").asText();
            if (transactionId.equals(currentTxnId)) {
                JsonNode orderDetails = orderNode.path("orderDetails");
                JsonNode paymentDetails = orderNode.path("paymentDetails");

                String subject = "✅ Order Confirmation – " + transactionId;
                String body = String.format(
                        "Hi,\n\nThanks for your order! Here are the details:\n\n" +
                                "🛒 Item: %s\n📦 Quantity: %d\n💰 Total: ₹%.2f\n" +
                                "🏠 Address: %s\n📞 Contact: %s\n\n" +
                                "💳 Payment ID: %s\n🕒 Time: %s\n\n" +
                                "– The Team",
                        orderDetails.path("itemName").asText(),
                        orderDetails.path("quantity").asInt(),
                        orderDetails.path("totalAmount").asDouble(),
                        orderDetails.path("address").asText(),
                        orderDetails.path("contactNumber").asText(),
                        paymentDetails.path("paymentId").asText(),
                        paymentDetails.path("timestamp").asText()
                );

                sendEmail(email, subject, body);
                return; // Stop after first matching order
            }
        }

        System.out.println("❌ Order with transactionId not found.");

    } catch (Exception e) {
        System.out.println("🚨 Error while processing order notification:");
        e.printStackTrace();
    }
}
    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false); // set true for HTML

            mailSender.send(message);
            System.out.println("Email sent to " + to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
