package com.sample.kafka.services;

import com.sample.kafka.entity.Inventory;
import com.sample.kafka.repository.InventoryRepo;
import com.sample.kafka.utils.kafkaTopics;
import com.sample.kafka.utils.Products;
import com.sample.kafka.utils.Categories;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
//@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final Random RANDOM = new Random();
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(InventoryService.class);


    @KafkaListener(topics = kafkaTopics.ORDER_CREATED, groupId = "inventory-service-group")
    public void consumeOrderEventPartition1(ConsumerRecord<String, String> record) {
        log.info("Consumer-1 | Consumed message: {}", record.value());
        processInventory(record);
    }

    @KafkaListener(topics = kafkaTopics.ORDER_CREATED, groupId = "inventory-service-group")
    public void consumeOrderEventPartition2(ConsumerRecord<String, String> record) {
        log.info("Consumer-2 | Consumed message: {}", record.value());
        processInventory(record);
    }

    @KafkaListener(topics = kafkaTopics.ORDER_PARTITION, groupId = "inventory-service-group")
    public void consumeOrderEventPartition3(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String category = record.key();
        String message = record.value();
        int partition = record.partition();
        log.info("Consumer-3 | Consumed message: {} | Partition: {} | Category: {}", record.value(), partition, record.key());
        //processInventory(record);
    }

    public void processInventory(ConsumerRecord<String, String> record) {
        String category = record.key();
        String product;

        switch (category) {
            case Categories.ELECTRONICS -> product = getRandomProduct(Products.ELECTRONICS); //processing logic
            case Categories.CLOTHING -> product = getRandomProduct(Products.CLOTHING);
            case Categories.TRADING -> product = getRandomProduct(Products.TRADING);
            case Categories.FURNITURE -> product = getRandomProduct(Products.FURNITURE);
            case Categories.FOOD -> product = getRandomProduct(Products.FOOD);
            case Categories.BOOKS -> product = getRandomProduct(Products.BOOKS);
            default -> {
                log.warn("Unknown category: {} — using a generic product", category);
                product = getRandomProduct(Products.PRODUCT_NAMES);
            }
        }

        int randomQuantity = RANDOM.nextInt(401) + 100;

        Inventory dummyInventory = new Inventory();
        dummyInventory.setProductName(product);
        dummyInventory.setStockQuantity(randomQuantity);
        inventoryRepo.save(dummyInventory);

        log.info("Inventory saved for Product: {} | Quantity: {}", product, randomQuantity);
        log.info("Processing {} category order...", category);
    }

    private String getRandomProduct(List<String> products) {
        return products.get(RANDOM.nextInt(products.size()));
    }
}
