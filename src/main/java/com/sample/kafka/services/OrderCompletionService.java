package com.sample.kafka.services;

import com.sample.kafka.entity.Order;
import com.sample.kafka.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OrderCompletionService {
    private final OrderRepo orderRepo;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderCompletionService.class);

    public void markOrderCompleted(Long orderId, String combinedValue){
        if (!combinedValue.contains(String.valueOf(orderId))) {
            log.error("Mismatch in orderId within joined message for key {}", orderId);
            return;
        }

        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setOrderStatus("COMPLETED");
            orderRepo.save(order);
            log.info("Order status updated to COMPLETED for ID {}", orderId);
        } else {
            log.warn("Order not found for ID {} to update it as COMPLETED.", orderId);
        }
    }

    public void markOrderFailed(Long orderId, String combinedValue){
        if (!combinedValue.contains(String.valueOf(orderId))){
            log.error("Mismatch in orderId within joined message for key {}", orderId);
            return;
        }

        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if(optionalOrder.isPresent()){
            Order order = optionalOrder.get();
            order.setOrderStatus("FAILED");
            orderRepo.save(order);
            log.info("Order status updated to FAILED for ID: {}", orderId);
        } else{
            log.warn("Order not found for ID {} to update it as COMPLETED.", orderId);
        }
    }
}
