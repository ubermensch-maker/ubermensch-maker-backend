package com.example.todo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DevLoginRequest(
    @NotBlank(message = "Email is required") @Email(message = "Invalid email format")
        String email) {}
