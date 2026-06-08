package com.devsu.clients.event;

import java.time.Instant;

public record CustomerEvent(
        CustomerEventType eventType,
        String customerId,
        String name,
        String identification,
        boolean active,
        Instant occurredAt
) {

    public static CustomerEvent created(String customerId, String name, String identification) {
        return new CustomerEvent(CustomerEventType.CREATED, customerId, name, identification, true, Instant.now());
    }

    public static CustomerEvent updated(String customerId, String name, String identification, boolean active) {
        return new CustomerEvent(CustomerEventType.UPDATED, customerId, name, identification, active, Instant.now());
    }

    public static CustomerEvent deactivated(String customerId) {
        return new CustomerEvent(CustomerEventType.DEACTIVATED, customerId, null, null, false, Instant.now());
    }
}