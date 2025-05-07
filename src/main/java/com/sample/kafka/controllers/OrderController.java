package com.sample.kafka.controllers;

import com.sample.kafka.entity.Order;
import com.sample.kafka.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/kafka/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PostMapping("/specific-create")
    public String sendPartitionSpecific(@RequestBody Order order){
        return orderService.sendPartitionSpecific(order);
    }
}
