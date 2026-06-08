package com.devsu.clients.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        Instant timestamp,
        int status,
        String path,
        T data,
        ApiError error
) {

    public static <T> ApiResponse<T> success(int status, String path, T data) {
        return new ApiResponse<>(Instant.now(), status, path, data, null);
    }

    public static <T> ApiResponse<T> error(int status, String path, ApiError error) {
        return new ApiResponse<>(Instant.now(), status, path, null, error);
    }
}