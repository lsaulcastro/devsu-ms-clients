package com.devsu.clients.dto;

import com.devsu.clients.domain.model.Gender;

public record CustomerResponse(
        Long id,
        String customerId,
        String name,
        Gender gender,
        int age,
        String identification,
        String address,
        String phone,
        boolean active
) {
}