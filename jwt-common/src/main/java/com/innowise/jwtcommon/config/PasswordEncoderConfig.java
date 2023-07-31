package com.innowise.jwtcommon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import static com.innowise.jwtcommon.constant.JwtSecurityConstant.*;

@Configuration
public class PasswordEncoderConfig {

    @Value(PASSWORD_ENCODER_SALT_LENGTH)
    private int saltLength;

    @Value(PASSWORD_ENCODER_HASH_LENGTH)
    private int hashLength;

    @Value(PASSWORD_ENCODER_PARALLELISM)
    private int parallelism;

    @Value(PASSWORD_ENCODER_MEMORY)
    private int memory;

    @Value(PASSWORD_ENCODER_ITERATIONS)
    private int iterations;

    @Bean
    public Argon2PasswordEncoder argon2PasswordEncoder() {
        return new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

}
