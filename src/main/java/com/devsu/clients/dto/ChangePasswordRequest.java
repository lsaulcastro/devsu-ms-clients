package com.devsu.clients.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "New password is required")
        @Size(min = 4, max = 100, message = "Password must be between 4 and 100 characters")
        String newPassword
) {
}