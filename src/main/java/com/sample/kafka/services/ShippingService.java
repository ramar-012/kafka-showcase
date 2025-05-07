package com.sample.kafka.services;

import com.sample.kafka.entity.Order;
import com.sample.kafka.entity.Shipment;
import com.sample.kafka.repository.OrderRepo;
import com.sample.kafka.repository.ShipmentRepo;
import com.sample.kafka.utils.kafkaTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ShippingService {

    @Autowired
    private ShipmentRepo shipmentRepo;

    @Autowired
    private OrderRepo orderRepo;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ShippingService.class);

    @KafkaListener(topics = kafkaTopics.PAYMENT_DONE, groupId = "shipping-service-group")
    public void processShipmentOnPaymentSuccess(String message) {
        log.info("ShippingService received payment success: {}", message);

        Long orderId = extractOrderId(message);
        if (orderId != null) {
            Optional<Order> orderOptional = orderRepo.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                Shipment shipment = new Shipment();
                shipment.setOrder(order);
                shipment.setShipmentStatus("SHIPPED");
                shipment.setShipmentDate(new Date());
                shipmentRepo.save(shipment);

                log.info("Shipment created for Order ID: {}", orderId);
            } else {
                log.error("Order not found for ID: {}", orderId);
            }
        }
    }

    @KafkaListener(topics = kafkaTopics.PAYMENT_FAIL, groupId = "shipping-service-group")
    public void handleFailedPayment(String message) {
        log.info("ShippingService received payment failure: " + message);

        Long orderId = extractOrderId(message);
        if (orderId != null) {
            Optional<Order> orderOptional = orderRepo.findById(orderId);
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                Shipment shipment = new Shipment();
                shipment.setOrder(order);
                shipment.setShipmentStatus("FAILED");
                shipment.setShipmentDate(new Date());
                shipmentRepo.save(shipment);

                log.info("Shipment marked as FAILED for Order ID: {}", orderId);
            } else {
                log.error("Order not found for ID: {}", orderId);
            }
        }
    }

    private Long extractOrderId(String message) {
        try {
            String[] parts = message.split(":");
            return Long.parseLong(parts[1].trim());
        } catch (Exception e) {
            log.error("Invalid message format: {}", message);
            return null;
        }
    }
}

