package com.innowise.musicenrichermicroservice.config;

import com.innowise.musicenrichermicroservice.security.InMemoryAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import static com.innowise.musicenrichermicroservice.constant.YamlPropertyConstant.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value(value = IN_MEMORY_USER_USERNAME_PROPERTY)
    private String inMemoryUserUsername;

    @Value(value = IN_MEMORY_USER_PASSWORD_PROPERTY)
    private String inMemoryUserPassword;

    @Value(value = IN_MEMORY_USER_ROLE_PROPERTY)
    private String inMemoryUserRole;

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new InMemoryAuthenticationEntryPoint();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails system = User.builder()
                .username(inMemoryUserUsername)
                .password(inMemoryUserPassword)
                .roles(inMemoryUserRole)
                .passwordEncoder(rawPassword -> bCryptPasswordEncoder().encode(rawPassword))
                .build();
        return new InMemoryUserDetailsManager(system);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()

                .httpBasic()
                .and()

                .authorizeHttpRequests()
                .requestMatchers("/actuator/**")
                .permitAll()
                .anyRequest()
                .authenticated()

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())

                .and()
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
