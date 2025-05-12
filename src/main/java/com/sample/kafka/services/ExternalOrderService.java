package com.sample.kafka.services;

import com.sample.kafka.utils.kafkaTopics;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;


@Service
public class ExternalOrderService {
    private final WebClient webClient;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ExternalOrderService.class);

    public ExternalOrderService(){
        this.webClient = WebClient.builder().baseUrl(kafkaTopics.STREAM_URI).build();
    }
//    public void sendOrderToExternalService(String order){
//        webClient.post()
//                .uri("/external-orders")
//                .bodyValue(order)
//                .retrieve()
//                .bodyToMono(String.class)
//                .doOnSuccess(aVoid -> log.info("Order sent to external service successfully"))
//                .doOnError(throwable -> log.error("Failed to send order to external service: {}", throwable.getMessage()))
//                .subscribe();
//    }
    public void sendOrderToExternalService(String order) {
        RestTemplate restTemplate = new RestTemplate();
        // send the stream data to the external system
        List<String> urls = List.of(
                kafkaTopics.STREAM_URI,
                kafkaTopics.NOW_STREAM_API
        );

        for (String url : urls) {
            try {
                String response = restTemplate.postForObject(url, order, String.class);
                log.info("Order sent to {} successfully. Response: {}", url, response);
            } catch (Exception e) {
                log.error("Failed to send order to {}: {}", url, e.getMessage());
            }
        }
    }

}
