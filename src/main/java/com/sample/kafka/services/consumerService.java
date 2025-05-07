package com.sample.kafka.services;

import com.sample.kafka.utils.kafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class consumerService {

    private static final Logger log = LoggerFactory.getLogger(consumerService.class);

    @KafkaListener(topics = kafkaTopics.KAFKA_TOPIC_NAME, groupId = "test-group")
    public void consumeMessage(String message) {
        log.info("Consumed message from the topic:  " + kafkaTopics.KAFKA_TOPIC_NAME + ": {}", message);
    }
}

