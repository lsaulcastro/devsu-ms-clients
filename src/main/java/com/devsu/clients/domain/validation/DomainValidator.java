package com.devsu.clients.domain.validation;

import com.devsu.clients.domain.exception.InvalidDataException;

public final class DomainValidator {

    private DomainValidator() {
    }


    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new InvalidDataException(fieldName + " is required");
        }
        return value;
    }


    public static String requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidDataException(fieldName + " is required");
        }
        return value;
    }

    public static String requireMaxLength(String value, int maxLength, String fieldName) {
        if (value != null && value.trim().length() > maxLength) {
            throw new InvalidDataException(
                    fieldName + " cannot exceed " + maxLength + " characters");
        }
        return value;
    }

    public static int requireInRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new InvalidDataException(
                    fieldName + " must be between " + min + " and " + max);
        }
        return value;
    }

    public static int requireNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new InvalidDataException(fieldName + " cannot be negative");
        }
        return value;
    }
}