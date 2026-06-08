package com.devsu.clients.event.outbox;

public enum OutboxEventStatus {
    PENDING,
    PUBLISHED,
    FAILED
}