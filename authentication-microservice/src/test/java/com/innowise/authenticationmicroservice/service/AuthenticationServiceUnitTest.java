package com.innowise.authenticationmicroservice.service;

import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;
import com.innowise.authenticationmicroservice.security.ApplicationUserDetails;
import com.innowise.authenticationmicroservice.service.impl.AuthenticationServiceImpl;
import com.innowise.jwtcommon.security.JwtService;
import com.innowise.usercommon.domain.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class AuthenticationServiceUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(jwtService);
        Assertions.assertNotNull(authenticationService);
        Assertions.assertNotNull(authenticationManager);
    }

    @Test
    @Order(2)
    void mustAuthenticateUser() {
        final String jwt = "generatedJwt";
        final String username = "user";
        final AuthenticationRequest authenticationRequest = new AuthenticationRequest(username, "");
        final User user = User.builder()
                .username(username)
                .build();
        final ApplicationUserDetails authenticatedUser = new ApplicationUserDetails(user);

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticatedUser, null);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationToken);
        Mockito.when(jwtService.generateToken(Mockito.any(UserDetails.class))).thenReturn("generatedJwt");

        AuthenticationResponse authenticationResponse = authenticationService.authenticateUser(authenticationRequest);

        Mockito.verify(jwtService, Mockito.times(1)).generateToken(Mockito.any());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());

        Assertions.assertNotNull(authenticationResponse);
        Assertions.assertEquals(username, authenticationResponse.username());
        Assertions.assertEquals(jwt, authenticationResponse.token());
    }

    @Test
    @Order(3)
    void mustThrowBadCredentialsExceptionWhileAuthenticationUser() {
        final AuthenticationRequest authenticationRequestLocked = new AuthenticationRequest("badCredentials", "badCredentials");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(BadCredentialsException.class);

        Assertions.assertThrows(BadCredentialsException.class, () ->
                authenticationService.authenticateUser(authenticationRequestLocked));

        Mockito.verify(jwtService, Mockito.times(0)).generateToken(Mockito.any());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
    }

    @Test
    @Order(4)
    void mustThrowAccountExpiredExceptionWhileAuthenticationUser() {
        final AuthenticationRequest authenticationRequestExpired = new AuthenticationRequest("expired", "expired");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(AccountExpiredException.class);

        Assertions.assertThrows(AccountExpiredException.class, () ->
                authenticationService.authenticateUser(authenticationRequestExpired));

        Mockito.verify(jwtService, Mockito.times(0)).generateToken(Mockito.any());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
    }

    @Test
    @Order(5)
    void mustThrowDisabledExceptionWhileAuthenticationUser() {
        final AuthenticationRequest authenticationRequestDisabled = new AuthenticationRequest("disabled", "disabled");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(DisabledException.class);

        Assertions.assertThrows(DisabledException.class, () ->
                authenticationService.authenticateUser(authenticationRequestDisabled));

        Mockito.verify(jwtService, Mockito.times(0)).generateToken(Mockito.any());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any());
    }

}