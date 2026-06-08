package com.devsu.clients.event.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private static final int BATCH_SIZE = 10;

    private final OutboxEventRepository outboxRepository;
    private final OutboxEventProcessor outboxEventProcessor;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:2000}")
    public void relay() {
        List<OutboxEvent> pending = outboxRepository.findPending(PageRequest.of(0, BATCH_SIZE));
        if (pending.isEmpty()) {
            return;
        }
        log.debug("Processing {} pending outbox events", pending.size());
        pending.forEach(outboxEventProcessor::process);
    }
}