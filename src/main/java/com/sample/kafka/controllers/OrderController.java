package com.sample.kafka.controllers;

import com.sample.kafka.entity.Order;
import com.sample.kafka.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Order createOrder(@RequestBody Order order, @RequestHeader("message") String message) {
        return orderService.createOrder(order, message);
    }

    @PostMapping("/specific-create")
    public String sendPartitionSpecific(@RequestBody Order order){
        return orderService.sendPartitionSpecific(order);
    }

    @PostMapping("/test-streams")
    public String sendOrderStream(@RequestBody Order order, @RequestHeader("status") String status) {
        return orderService.sendToStreams(order, status);
    }

    @PostMapping("/external-orders")
    public ResponseEntity<String> receiveOrder(@RequestBody String order) {
        System.out.println("Received Order: " + order + ", in the external system (as demo).");
        return ResponseEntity.ok("Order received via 8080.");
    }
}
