package com.sample.kafka.services;

import com.sample.kafka.entity.Inventory;
import com.sample.kafka.entity.Order;
import com.sample.kafka.repository.InventoryRepo;
import com.sample.kafka.repository.OrderRepo;
import com.sample.kafka.utils.kafkaTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class ExternalOrderService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private InventoryRepo inventoryRepo;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExternalOrderService.class);

    public ExternalOrderService(){
        WebClient webClient = WebClient.builder().baseUrl(kafkaTopics.STREAM_URI).build();
    }

    // TODO: As kafka streams are asynchronous, webflux reactive is needed to stream to external system
//    public void sendOrderToExternalService(String order){
//        webClient.post()
//                .uri("/external-orders")
//                .bodyValue(order)
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnSuccess(aVoid -> log.info("Order sent to external service successfully"))
//                .doOnError(throwable -> log.error("Failed to send order to external service: {}", throwable.getMessage()))
//                .subscribe();
//    }
    public void sendOrderToExternalService(String message) {
        Long orderId = kafkaTopics.extractOrderId(message);
        RestTemplate restTemplate = new RestTemplate();
        // send the stream data to the external system
        List<String> urls = List.of(
                kafkaTopics.STREAM_URI,
                kafkaTopics.NOW_STREAM_API
        );

        Map<String, Object> result = extractOrderAndInventory(message);
        Order order = (Order) result.get("order");
        Inventory inventory = (Inventory) result.get("inventory");
        String externalApiUrl = kafkaTopics.NOW_STREAM_API;

        String payload = "{"
                + "\"order_id\": \"" + order.getId() + "\","
                + "\"customer_name\": \"" + order.getCustomerName() + "\","
                + "\"category\": \"" + order.getCategory() + "\","
                + "\"product_name\": \"" + inventory.getProductName() + "\","
                + "\"stock_quantity\": " + inventory.getStockQuantity() + ","
                + "\"status\": \"Ready for Shipping\""
                + "}";

        ResponseEntity<String> res = restTemplate.postForEntity(externalApiUrl, payload, String.class);
        if(res.getStatusCode() == HttpStatus.OK){
            log.info("Order successfully sent to delivery system.");
            kafkaTemplate.send(kafkaTopics.PAYMENT_DONE, String.valueOf(orderId), message);
        } else {
            System.out.println("Order failed to send to external system.");
        }

    }

    public Map<String, Object> extractOrderAndInventory(String message) {
        Long orderId = kafkaTopics.extractOrderId(message);
        if (orderId == null) {
            log.error("Invalid message to extract the Order ID: {}", message);
            return null;
        }

        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if (optionalOrder.isEmpty()) {
            log.error("Order not found for ID: {}", orderId);
            return null;
        }

        Order order = optionalOrder.get();
        Optional<Inventory> optionalInventory = inventoryRepo.findById(orderId);
        if (optionalInventory.isEmpty()) {
            log.error("Inventory not found for Order ID: {}", orderId);
            return null;
        }

        Inventory inventory = optionalInventory.get();
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("inventory", inventory);
        return result;
    }

}
