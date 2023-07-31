package com.innowise.musicenrichermicroservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

import static com.innowise.musicenrichermicroservice.constant.YamlPropertyConstant.REDIS_HOST_PROPERTY;
import static com.innowise.musicenrichermicroservice.constant.YamlPropertyConstant.REDIS_PORT_PROPERTY;

@Configuration
public class RedisConfig {

    @Value(value = REDIS_HOST_PROPERTY)
    private String redisHost;

    @Value(value = REDIS_PORT_PROPERTY)
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisHost, redisPort));
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        template.setValueSerializer(new StringRedisSerializer(StandardCharsets.UTF_8));
        template.afterPropertiesSet();
        return template;
    }
}
