package com.devsu.clients.event.outbox;

import com.devsu.clients.event.CustomerEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventRecorder {

    private static final String AGGREGATE_TYPE = "Customer";

    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public void record(CustomerEvent event) {
        String payload = serialize(event);
        OutboxEvent outboxEvent = new OutboxEvent(
                AGGREGATE_TYPE,
                event.customerId(),
                event.eventType().name(),
                payload
        );
        outboxRepository.save(outboxEvent);
        log.debug("Recorded outbox event: type={}, customerId={}",
                event.eventType(), event.customerId());
    }

    private String serialize(CustomerEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException(
                    "Failed to serialize CustomerEvent for customerId=" + event.customerId(), ex);
        }
    }
}