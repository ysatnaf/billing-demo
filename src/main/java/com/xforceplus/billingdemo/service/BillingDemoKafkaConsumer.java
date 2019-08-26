package com.xforceplus.billingdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BillingDemoKafkaConsumer {

    private final Logger log = LoggerFactory.getLogger(BillingDemoKafkaConsumer.class);
    private static final String TOPIC = "topic_billingdemo";

    @KafkaListener(topics = "topic_billingdemo", groupId = "group_id")
    public void consume(String message) throws IOException {
        log.info("Consumed message in {} : {}", TOPIC, message);
    }
}
