package com.devsu.clients.event.outbox;

import com.devsu.clients.event.CustomerEvent;
import com.devsu.clients.event.CustomerEventPublisher;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventProcessor {

    private final OutboxEventRepository outboxRepository;
    private final CustomerEventPublisher kafkaPublisher;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(OutboxEvent outboxEvent) {
        try {
            CustomerEvent event = objectMapper.readValue(outboxEvent.getPayload(), CustomerEvent.class);
            kafkaPublisher.publish(event);
            outboxEvent.markAsPublished();
            outboxRepository.save(outboxEvent);
            log.info("Outbox event published: id={}, type={}, customerId={}",
                    outboxEvent.getId(), outboxEvent.getEventType(), outboxEvent.getAggregateId());
        } catch (JsonProcessingException ex) {
            handleFailure(outboxEvent, "Failed to deserialize payload: " + ex.getMessage());
        } catch (Exception ex) {
            handleFailure(outboxEvent, ex.getMessage());
        }
    }

    private void handleFailure(OutboxEvent outboxEvent, String errorMessage) {
        outboxEvent.markAsFailed(errorMessage);
        outboxRepository.save(outboxEvent);
        log.warn("Outbox event failed: id={}, retryCount={}, status={}, error={}",
                outboxEvent.getId(), outboxEvent.getRetryCount(),
                outboxEvent.getStatus(), errorMessage);
    }
}