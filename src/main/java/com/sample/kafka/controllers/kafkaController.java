package com.sample.kafka.controllers;

import com.sample.kafka.services.producerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kafka")
public class kafkaController {

    @Autowired
    private producerService kafkaProducerService;

    @PostMapping("/send")
    public String sendMessageToKafka(@RequestBody String message) {
        return kafkaProducerService.sendMessage(message);
//        return "Message sent to Kafka: " + message;
    }
}
