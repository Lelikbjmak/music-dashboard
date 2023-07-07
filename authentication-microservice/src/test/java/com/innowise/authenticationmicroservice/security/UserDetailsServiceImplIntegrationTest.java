package com.innowise.authenticationmicroservice.security;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles(value = "integration")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserDetailsServiceImplIntegrationTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(userDetailsService);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustLoadUserByUsername(@Value("${user.username}") final String username) {
        UserDetails actualUserDetails = userDetailsService.loadUserByUsername(username);

        Assertions.assertNotNull(actualUserDetails);
        Assertions.assertEquals(username, actualUserDetails.getUsername());
    }

    @Test
    @Order(3)
    void mustThrowExceptionUsernameNotFound() {
        Assertions.assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("undefined"));
    }
}
