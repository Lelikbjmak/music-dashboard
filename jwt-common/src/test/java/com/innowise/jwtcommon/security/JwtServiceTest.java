package com.innowise.jwtcommon.security;

import com.innowise.jwtcommon.config.TestUserDetails;
import com.innowise.jwtcommon.exception.JwtAuthenticationException;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.util.List;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class JwtServiceTest {

    private final String username = "mockUsername";

    private final JwtService jwtService = new JwtService(3600000, "2B4D6251655468576D5A7134743777217A25432A462D4A404E635266556A586E");

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(jwtService);
    }

    @Test
    void mustGenerateToken() {
        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));
        Assertions.assertNotNull(jwtToken);
    }

    @Test
    void isJwtTokenValid_mustReturnInvalid_NotValidUsername() throws JwtAuthenticationException {
        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));
        boolean isValid = jwtService.isJwtTokenValid(jwtToken, "invalidUsername");

        Assertions.assertFalse(isValid);
    }

    @Test
    void isJwtTokenValid_mustReturnValid() throws JwtAuthenticationException {
        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));
        boolean isValid = jwtService.isJwtTokenValid(jwtToken, username);

        Assertions.assertTrue(isValid);
    }

    @Test
    void isJwtTokenValid_mustReturnInValid_tokenExpired() throws NoSuchFieldException, IllegalAccessException, JwtAuthenticationException {
        Field validity = JwtService.class.getDeclaredField("tokenValidity");
        validity.setAccessible(true);
        validity.set(jwtService, 0);

        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));

        Exception e = Assertions.assertThrows(JwtAuthenticationException.class, () ->
                jwtService.isJwtTokenValid(jwtToken, username));

        Assertions.assertEquals("Session expired.", e.getMessage());

        validity.set(jwtService, 1000);
        validity.setAccessible(false);
    }

    @Test
    void mustReturnUsernameFromToken() throws JwtAuthenticationException {
        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));
        String extractedUsername = jwtService.extractUsername(jwtToken);

        Assertions.assertNotNull(extractedUsername);
        Assertions.assertEquals(username, extractedUsername);
    }

    @Test
    void mustReturnRoleListFromToken() throws JwtAuthenticationException {
        List<String> expectedRoleList = List.of("TEST");

        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));
        List<String> extractedRoles = jwtService.extractRoles(jwtToken);

        Assertions.assertNotNull(extractedRoles);
        Assertions.assertEquals(expectedRoleList, extractedRoles);
    }

    @Test
    void isTokenExpired_tokenExpired() throws NoSuchFieldException, IllegalAccessException, JwtAuthenticationException {
        Field validity = JwtService.class.getDeclaredField("tokenValidity");
        validity.setAccessible(true);
        validity.set(jwtService, 0);

        String jwtToken = jwtService.generateToken(new TestUserDetails(true, true, true, true));

        Exception e = Assertions.assertThrows(JwtAuthenticationException.class, () ->
                jwtService.isJwtTokenValid(jwtToken, username));

        Assertions.assertEquals("Session expired.", e.getMessage());
    }

}