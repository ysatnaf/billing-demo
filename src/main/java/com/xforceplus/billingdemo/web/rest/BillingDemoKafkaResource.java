package com.xforceplus.billingdemo.web.rest;

import com.xforceplus.billingdemo.service.BillingDemoKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/billing-demo-kafka")
public class BillingDemoKafkaResource {

    private final Logger log = LoggerFactory.getLogger(BillingDemoKafkaResource.class);

    private BillingDemoKafkaProducer kafkaProducer;

    public BillingDemoKafkaResource(BillingDemoKafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping(value = "/publish")
    public void sendMessageToKafkaTopic(@RequestParam("message") String message) {
        log.debug("REST request to send to Kafka topic the message : {}", message);
        this.kafkaProducer.sendMessage(message);
    }
}
