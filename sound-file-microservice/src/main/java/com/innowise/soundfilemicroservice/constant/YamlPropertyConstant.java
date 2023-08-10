package com.innowise.soundfilemicroservice.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class YamlPropertyConstant {

    public static final String AWS_ENDPOINT_URL_PROPERTY = "${aws.endpoint-url}";
    public static final String AWS_ACCESS_KEY_PROPERTY = "${aws.credentials.access-key}";
    public static final String AWS_SECRET_KEY_PROPERTY = "${aws.credentials.secret-key}";
}
