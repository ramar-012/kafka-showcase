package com.sample.kafka.stream;

import com.sample.kafka.services.OrderCompletionService;
import com.sample.kafka.utils.kafkaTopics;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OrderCompletionStreamProcessor {

    private final OrderCompletionService orderCompletionService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderCompletionStreamProcessor.class);

    public OrderCompletionStreamProcessor(OrderCompletionService service) {
        this.orderCompletionService = service;
    }

    @Bean
    public KStream<String, String> orderCompletionStream(StreamsBuilder builder) {

        KStream<String, String> paymentStream = builder.stream(kafkaTopics.PAYMENT_DONE,
                Consumed.with(Serdes.String(), Serdes.String()));
        KStream<String, String> shipmentStream = builder.stream(kafkaTopics.SHIPMENT_DONE,
                Consumed.with(Serdes.String(), Serdes.String()));

        // Join both streams on key (orderId) within a 5-minute window
        KStream<String, String> joinedStream = paymentStream.join(
                shipmentStream,
                (paymentMessage, shipmentMessage) -> paymentMessage + " | " + shipmentMessage,
                JoinWindows.of(Duration.ofMinutes(5)),
                StreamJoined.with(Serdes.String(), Serdes.String(), Serdes.String())
        );

        // Process the joined result
        joinedStream.peek((orderId, combinedValue) -> {
            log.info("Order Completion Join Event for Order ID {}: {}", orderId, combinedValue);
            orderCompletionService.markOrderCompleted(Long.valueOf(orderId), combinedValue);
        });

        return joinedStream;
    }
}

