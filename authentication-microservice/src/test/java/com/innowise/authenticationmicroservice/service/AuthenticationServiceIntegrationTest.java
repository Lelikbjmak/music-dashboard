package com.innowise.authenticationmicroservice.service;

import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles(value = "integration")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationServiceIntegrationTest {

    @Autowired
    private AuthenticationService authenticationService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(authenticationService);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustAuthenticateUserSuccessfully(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        AuthenticationResponse authenticationResponse = authenticationService.authenticateUser(authenticationRequest);
        Assertions.assertNotNull(authenticationResponse);
        Assertions.assertNotNull(authenticationResponse.token());
        Assertions.assertEquals(username, authenticationResponse.username());
    }

    @Test
    @Order(3)
    void mustThrowBadCredentialsExceptionWhileAuthenticationUser() {
        final AuthenticationRequest authenticationRequestLocked = new AuthenticationRequest("undefined", "undefined");
        Assertions.assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticateUser(authenticationRequestLocked));
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/02-create-disabled-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowDisabledExceptionWhileAuthenticationUser(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        Assertions.assertThrows(DisabledException.class, () ->
                authenticationService.authenticateUser(authenticationRequest));
    }

    @Test
    @Order(5)
    @Sql(value = "/sql/03-create-expired-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowAccountExpiredExceptionWhileAuthenticationUser(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        Assertions.assertThrows(AccountExpiredException.class, () ->
                authenticationService.authenticateUser(authenticationRequest));

    }

    @Test
    @Order(6)
    @Sql(value = "/sql/04-create-locked-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowLockedExceptionWhileAuthenticationUser(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);
        Assertions.assertThrows(LockedException.class, () ->
                authenticationService.authenticateUser(authenticationRequest));
    }
}
