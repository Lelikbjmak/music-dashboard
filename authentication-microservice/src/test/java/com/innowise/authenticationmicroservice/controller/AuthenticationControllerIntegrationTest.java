package com.innowise.authenticationmicroservice.controller;

import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles(value = "integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(testRestTemplate);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenValidInput_theAuthenticateUserMustReturn200(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        ResponseEntity<AuthenticationResponse> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, AuthenticationResponse.class);
        AuthenticationResponse actualAuthenticationResponse = response.getBody();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(actualAuthenticationResponse);
        Assertions.assertEquals(username, actualAuthenticationResponse.username());
    }

    @Test
    @Order(3)
    @Sql(value = "/sql/02-create-disabled-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenUserDisabled_thenAuthenticateUserMustReturn401(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, String.class);
        String responseBody = response.getBody();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("User is disabled"));
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/03-create-expired-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenUserExpired_thenAuthenticateUserMustReturn401UserExpired(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, String.class);
        String responseBody = response.getBody();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        System.out.println(responseBody);
        Assertions.assertTrue(responseBody.contains("User account has expired"));
    }

    @Test
    @Order(5)
    @Sql(value = "/sql/04-create-locked-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenUserLocked_thenAuthenticateUserMustReturn401(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, String.class);
        String responseBody = response.getBody();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("User account is locked"));
    }

    @Test
    @Order(6)
    void whenBadCredentials_theAuthenticateUserMustReturn401BadCredentials(
            @Value("${user.username}") final String username, @Value("${user.password}") final String password) {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, password);

        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, String.class);
        String responseBody = response.getBody();

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("Bad credentials"));
    }

    @Test
    @Order(7)
    void whenInvalidInput_blankUsername_thenAuthenticateUserMustReturn400() {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("", "password");

        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, String.class);
        String responseBody = response.getBody();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("username"));
    }

    @Test
    @Order(8)
    void whenInvalidInput_blankPassword_thenAuthenticateUserMustReturn400() {

        AuthenticationRequest authenticationRequest = new AuthenticationRequest("username", "");

        ResponseEntity<String> response = testRestTemplate
                .postForEntity("/api/v1/auth", authenticationRequest, String.class);
        String responseBody = response.getBody();

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertNotNull(responseBody);
        Assertions.assertTrue(responseBody.contains("password"));
    }
}
