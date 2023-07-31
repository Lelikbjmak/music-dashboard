package com.innowise.jwtcommon.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtSecurityConstant {

    public static final String BEARER_TOKEN_HEADER = "Bearer ";
    public static final int BEARER_TOKEN_START_INDEX = 7;

    public static final String JWT_TOKEN_VALIDITY = "${jwt.validity}";
    public static final String JWT_TOKEN_SECRET = "${jwt.secret}";


    public static final String PASSWORD_ENCODER_SALT_LENGTH = "${password-encoder.salt-length}";
    public static final String PASSWORD_ENCODER_HASH_LENGTH = "${password-encoder.hash-length}";
    public static final String PASSWORD_ENCODER_PARALLELISM = "${password-encoder.parallelism}";
    public static final String PASSWORD_ENCODER_MEMORY = "${password-encoder.memory}";
    public static final String PASSWORD_ENCODER_ITERATIONS = "${password-encoder.iterations}";
}
