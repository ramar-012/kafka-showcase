package com.sample.kafka.services;

import com.sample.kafka.entity.Inventory;
import com.sample.kafka.entity.Order;
import com.sample.kafka.entity.Payment;
import com.sample.kafka.repository.InventoryRepo;
import com.sample.kafka.repository.OrderRepo;
import com.sample.kafka.repository.PaymentRepo;
import com.sample.kafka.utils.kafkaTopics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = kafkaTopics.ORDER_CREATED, groupId = "payment-service-group")
    public void processPayment(ConsumerRecord<String, String> record) {
        String message = record.value();

        // simulate failed payment for kafka reprocessing message
        if (record.value().toLowerCase().contains("failpayment")) {
            throw new RuntimeException("Simulated failure");
        }


        log.info("Received order event for payment: {}", message);
        Long orderId = extractOrderId(message);
        if (orderId == null) {
            log.error("Invalid order message format: {}", message);
            System.out.println("Invalid order message format: " + message);
            return;
        }

        // wait for 5 seconds to simulate payment processing, since test database is slow to save orders
        try {
            log.info("Simulating payment process for Order ID: {}", orderId);
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            log.error("Payment processing interrupted: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
        Optional<Order> optionalOrder = orderRepo.findById(orderId);

        if (optionalOrder.isEmpty()) {
            log.error("Order not found for ID: {}", orderId);
            return;
        }

        Order order = optionalOrder.get();
        String paymentStatus = new Random().nextBoolean() ? "COMPLETED" : "FAILED";

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentStatus(paymentStatus);
        payment.setPaymentDate(new Date());
        paymentRepo.save(payment);
        String resultMessage = "Payment " + paymentStatus + " for Order ID: " + orderId;
        String topicToSend = paymentStatus.equals("COMPLETED") ? kafkaTopics.PAYMENT_DONE : kafkaTopics.PAYMENT_FAIL;

        kafkaTemplate.send(topicToSend, resultMessage);
        if(topicToSend.equals(kafkaTopics.PAYMENT_DONE)){
            log.info("Payment for the ID: {} is completed, and sending Order to stream.", orderId);
            String info = "Payment completed for order: " + orderId + ", with status as paid.";
            kafkaTemplate.send(kafkaTopics.ORDERS, order.getCategory(), info);
        }

        log.info("Payment status sent to {}: {}", topicToSend, resultMessage);
    }

    @KafkaListener(topics = kafkaTopics.ORDER_CREATED_DLT, groupId = "payment-service-group")
    public void catchDLT(ConsumerRecord<String, String> record) {
        String message = record.value();
        log.info("Received message from Dead Letter Topic: {}", message);
        // Handle the DLT message as needed

        // for now fix the message and send it back to order-created topic
        Long orderId = extractOrderId(message);
        String category = record.key();
        String value = "Retry payment for order: " + orderId;
        log.info("Fixed the message of DLT order: {} and resending the order with message: {}", orderId, value);
        kafkaTemplate.send(kafkaTopics.ORDER_CREATED, category, value);
    }

    private Long extractOrderId(String message) {
        try {
            String[] parts = message.split(":");
            return Long.parseLong(parts[1].trim());
        } catch (Exception e) {
            return null;
        }
    }

    @KafkaListener(topics = kafkaTopics.PAYMENT_DONE, groupId = "payment-service-group")
    public void handleCompletedPayment(ConsumerRecord<String, String> record) {
        String message = record.value();
        String category = record.key();
        String topic = record.topic();
        log.info("Received payment COMPLETED event for webhook: {}", message);
        Long orderId = extractOrderId(message);

        if (orderId == null) {
            log.error("Invalid payment COMPLETED message format: {}", message);
            return;
        }

        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if (optionalOrder.isEmpty()) {
            log.error("Order not found for ID: {}", orderId);
            return;
        }

        Order order = optionalOrder.get();
        Optional<Inventory> optionalInventory = inventoryRepo.findById(orderId);
        if (optionalInventory.isEmpty()) {
            // Handle the case where inventory is not found
            log.error("Inventory not found for Order ID: {}", orderId);
            return;
        }
        Inventory inventory = optionalInventory.get();
        sendToExternalWebhook(order, inventory);
    }

    private void sendToExternalWebhook(Order order, Inventory inventory) {
        String externalApiUrl = kafkaTopics.NOW_STREAM_API;

        String payload = "{"
                + "\"order_id\": \"" + order.getId() + "\","
                + "\"customer_name\": \"" + order.getCustomerName() + "\","
                + "\"category\": \"" + order.getCategory() + "\","
                + "\"product_name\": \"" + inventory.getProductName() + "\","
                + "\"stock_quantity\": " + inventory.getStockQuantity() + ","
                + "\"status\": \"Ready for Shipping\""
                + "}";

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForObject(externalApiUrl, payload, String.class);
            log.info("Sent order and inventory details to external system: {}", payload);
        } catch (Exception e){
            log.error("Failed to send order details to delivery agent: {}", e.getMessage());
        }
    }
}
