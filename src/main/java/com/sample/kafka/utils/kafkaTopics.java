package com.sample.kafka.utils;

public class kafkaTopics {
    public static final String KAFKA_TOPIC_NAME = "ramar";
    public static final String ORDER_CREATED = "order-created";
    public static final String PAYMENT_DONE = "payment-completed";
    public static final String PAYMENT_FAIL = "payment-failed";
    public static final String ORDER_PARTITION = "order-partition";
    public static final String ORDER_CREATED_DLT = "order-created-dlt";
    public static final String ORDERS = "orders";
    public static final String EXTERNAL_URI = "https://webhook.site/8deb6381-d7a8-4919-ba25-bdbd0f9e5d71"; //TODO: to be removed as this link may expire soon
    public static final String STREAM_URI = "http://localhost:8080/api/kafka/orders/external-orders";
    public static final String NOW_STREAM_API = "https://kafka-stream.free.beeceptor.com";
}