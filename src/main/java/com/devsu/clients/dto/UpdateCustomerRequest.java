package com.devsu.clients.dto;

import com.devsu.clients.domain.model.Gender;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        @NotNull(message = "Gender is required")
        Gender gender,

        @Min(value = 0, message = "Age cannot be negative")
        @Max(value = 150, message = "Age is not reasonable")
        int age,

        @NotBlank(message = "Address is required")
        @Size(max = 200, message = "Address cannot exceed 200 characters")
        String address,

        @NotBlank(message = "Phone is required")
        @Size(max = 20, message = "Phone cannot exceed 20 characters")
        String phone
) {
}