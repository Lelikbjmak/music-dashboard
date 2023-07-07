package com.innowise.authenticationmicroservice.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "Username is mandatory.")
        String username,

        @NotBlank(message = "Password is mandatory.")
        String password
) {
}
