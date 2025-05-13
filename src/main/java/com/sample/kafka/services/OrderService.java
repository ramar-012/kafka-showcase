package com.sample.kafka.services;

import com.sample.kafka.entity.Order;
import com.sample.kafka.repository.OrderRepo;
import com.sample.kafka.utils.kafkaTopics;
import com.sample.kafka.utils.PartitionCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Order createOrder(Order newOrder, String message) {

        Order order = new Order();
        order.setCustomerName(newOrder.getCustomerName());
        order.setTotalAmount(newOrder.getTotalAmount());
        order.setStatus("CREATED");
        order.setOrderDate(new Date());
        order.setCategory(newOrder.getCategory());

        Order savedOrder = orderRepo.save(order);

        String value = message + ": " + savedOrder.getId();
//        kafkaTemplate.send(kafkaTopics.ORDER_CREATED, value);
        kafkaTemplate.send(kafkaTopics.ORDER_CREATED, order.getCategory(), value);
        log.info("Order created and message sent to the topic: " + kafkaTopics.ORDER_CREATED + ": {}", value);

        return savedOrder;
    }

    public String sendPartitionSpecific(Order newOrder){
        // send category and hard partitioning to message
        String category = newOrder.getCategory();
        String newMessage = "Order created for " + category + " category: " + newOrder.getId();
        int partition = PartitionCategory.getPartitionForCategory(category);
        kafkaTemplate.send(kafkaTopics.ORDER_PARTITION, partition, category, newMessage);

        return "Order successfully sent to the category: " + category + " and it's partition: " + partition;
    }

    public String sendToStreams(Order newOrder, String status){
        String message = "Created order: " + newOrder.getId() + ", with status: " + status;

        kafkaTemplate.send(kafkaTopics.ORDERS, newOrder.getCategory(), message);
        log.info("Message sent to the {} topic:  {}",kafkaTopics.ORDERS, message);
        return "Order successfully sent to the topic: " + kafkaTopics.ORDERS;
    }
}
