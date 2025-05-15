package com.sample.kafka.config;
import org.apache.kafka.clients.admin.NewTopic;
import com.sample.kafka.utils.kafkaTopics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class kafkaTopicConfig {

    @Bean
    public NewTopic basicTopic(){
        return new NewTopic(kafkaTopics.KAFKA_TOPIC_NAME, 1, (short) 1);
    }

    @Bean
    public NewTopic orderCreatedTopic(){
        return new NewTopic(kafkaTopics.ORDER_CREATED, 3, (short) 1);
    }

    @Bean
    public NewTopic paymentDoneTopic(){
        return new NewTopic(kafkaTopics.PAYMENT_DONE, 1, (short) 1);
    }

    @Bean
    public NewTopic paymentFailTopic(){
        return new NewTopic(kafkaTopics.PAYMENT_FAIL, 1, (short) 1);
    }

    @Bean
    public NewTopic orderPartitionTopic(){
        return new NewTopic(kafkaTopics.ORDER_PARTITION, 7, (short) 1);
    }

    @Bean
    public NewTopic orderCreatedDLTTopic(){
        return new NewTopic(kafkaTopics.ORDER_CREATED_DLT, 3, (short) 1);
    }

    @Bean
    public NewTopic OrderStreamTopic(){
        return new NewTopic(kafkaTopics.ORDERS, 3, (short) 1);
    }

    @Bean
    public NewTopic ShipmentDoneTopic(){
        return new NewTopic(kafkaTopics.SHIPMENT_DONE, 1, (short) 1);
    }
}
