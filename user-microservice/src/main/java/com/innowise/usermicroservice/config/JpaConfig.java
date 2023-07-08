package com.innowise.usermicroservice.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "com.innowise.usercommon.domain")
@EnableJpaRepositories(basePackages = "com/innowise/usercommon/repository")
public class JpaConfig {

}
