package com.innowise.musicenrichermicroservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class YamlPropertyConstant {

    public static final String REDIS_HOST_PROPERTY = "${spring.redis.host}";
    public static final String REDIS_PORT_PROPERTY = "${spring.redis.port}";

    public static final String IN_MEMORY_USER_USERNAME_PROPERTY = "${authentication.basic.credentials.username}";
    public static final String IN_MEMORY_USER_PASSWORD_PROPERTY = "${authentication.basic.credentials.password}";
    public static final String IN_MEMORY_USER_ROLE_PROPERTY = "${authentication.basic.credentials.role}";

    public static final String AWS_ENDPOINT_URL_PROPERTY = "${aws.endpoint-url}";
    public static final String AWS_ACCESS_KEY_PROPERTY = "${aws.credentials.access-key}";
    public static final String AWS_SECRET_KEY_PROPERTY = "${aws.credentials.secret-key}";
}
