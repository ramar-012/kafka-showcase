package com.sample.kafka.stream;

import com.sample.kafka.utils.kafkaTopics;
import com.sample.kafka.services.ExternalOrderService;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.StreamsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderStreamProcessor {

    private final ExternalOrderService externalOrderService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderStreamProcessor.class);


    public OrderStreamProcessor(ExternalOrderService externalOrderService) {
        this.externalOrderService = externalOrderService;
    }

    @Bean
    public KStream<String, String> orderStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream(kafkaTopics.ORDERS);

        stream.filter((key, value) -> value != null && value.contains("paid"))
                .peek((key, value) -> {
                    System.out.println("Filtered paid order to stream: " + key + " with value: " + value);
                    externalOrderService.sendOrderToExternalService(value);
                });

        return stream;
    }
}
