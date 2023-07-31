package com.innowise.jwtcommon.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationException extends Exception {

    public JwtAuthenticationException(String message) {
        super(message);
    }

    public JwtAuthenticationException(Throwable cause) {
        super(cause);
        log.error("Throw JwtException...");
    }

    public JwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
