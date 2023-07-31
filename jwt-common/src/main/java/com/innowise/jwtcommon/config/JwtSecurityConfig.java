package com.innowise.jwtcommon.config;

import com.innowise.jwtcommon.mapper.GrantedAuthorityListMapper;
import com.innowise.jwtcommon.mapper.GrantedAuthorityMapper;
import com.innowise.jwtcommon.security.JwtAccessDeniedHandler;
import com.innowise.jwtcommon.security.JwtAuthenticationEntryPoint;
import com.innowise.jwtcommon.security.JwtAuthenticationFilter;
import com.innowise.jwtcommon.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import static com.innowise.jwtcommon.constant.JwtSecurityConstant.JWT_TOKEN_SECRET;
import static com.innowise.jwtcommon.constant.JwtSecurityConstant.JWT_TOKEN_VALIDITY;

@Configuration
public class JwtSecurityConfig {

    @Value(JWT_TOKEN_SECRET)
    private String jwtSecret;

    @Value(JWT_TOKEN_VALIDITY)
    private long jwtTokenValidity;

    @Bean
    public GrantedAuthorityMapper grantedAuthorityMapper() {
        return new GrantedAuthorityMapper();
    }

    @Bean
    public GrantedAuthorityListMapper grantedAuthorityListMapper() {
        return new GrantedAuthorityListMapper();
    }

    @Bean
    public JwtService jwtService() {
        return new JwtService(jwtTokenValidity, jwtSecret);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new JwtAccessDeniedHandler();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }
}
