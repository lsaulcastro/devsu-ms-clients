package com.devsu.clients.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerEventPublisher {

    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    @Value("${app.kafka.topic.customer-events}")
    private String topic;

    public void publish(CustomerEvent event) {
        kafkaTemplate.send(topic, event.customerId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish customer event: type={}, customerId={}",
                                event.eventType(), event.customerId(), ex);
                    } else {
                        log.info("Published customer event: type={}, customerId={}, offset={}",
                                event.eventType(), event.customerId(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}