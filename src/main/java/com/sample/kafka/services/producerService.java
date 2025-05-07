package com.sample.kafka.services;

import com.sample.kafka.utils.kafkaTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class producerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(producerService.class);

    @Autowired
    public producerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String sendMessage(String message) {
        log.info("Sending the message to the topic: " + kafkaTopics.KAFKA_TOPIC_NAME);
        kafkaTemplate.send(kafkaTopics.KAFKA_TOPIC_NAME, message);
        return "Message sent to the topic: " + kafkaTopics.KAFKA_TOPIC_NAME;
    }
}
