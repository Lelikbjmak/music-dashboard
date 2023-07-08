package com.innowise.authenticationmicroservice.dto;

public record AuthenticationResponse(
        String username,
        String token
) {
}
