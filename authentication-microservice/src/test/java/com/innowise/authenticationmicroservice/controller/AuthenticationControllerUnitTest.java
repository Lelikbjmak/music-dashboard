package com.innowise.authenticationmicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authenticationmicroservice.dto.AuthenticationRequest;
import com.innowise.authenticationmicroservice.dto.AuthenticationResponse;
import com.innowise.authenticationmicroservice.service.AuthenticationService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "unit")
@WebMvcTest(controllers = AuthenticationController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(authenticationService);
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void whenValidInput_thenAuthenticateUserShouldReturn200() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("admin", "adminadmin");
        AuthenticationResponse expectedResponse =
                new AuthenticationResponse("user", UUID.randomUUID().toString());

        Mockito.when(authenticationService.authenticateUser(authenticationRequest)).thenReturn(expectedResponse);

        String jsonResponseBody = mockMvc.perform(post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        AuthenticationResponse actualResponse = objectMapper
                .readValue(jsonResponseBody, AuthenticationResponse.class);

        Assertions.assertEquals(expectedResponse, actualResponse);
        Mockito.verify(authenticationService, Mockito.times(1))
                .authenticateUser(authenticationRequest);
    }

    @Test
    @Order(2)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void whenValidInput_thenAuthenticateUserShouldCallBusinessLogicReturn200() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("admin", "adminadmin");

        Mockito.when(authenticationService.authenticateUser(authenticationRequest)).thenReturn(Mockito.any(AuthenticationResponse.class));

        mockMvc.perform(post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ArgumentCaptor<AuthenticationRequest> authenticationRequestArgumentCaptor = ArgumentCaptor.forClass(AuthenticationRequest.class);
        Mockito.verify(authenticationService, Mockito.times(1))
                .authenticateUser(authenticationRequestArgumentCaptor.capture());
        assertThat(authenticationRequestArgumentCaptor.getValue().username()).isEqualTo("admin");
        assertThat(authenticationRequestArgumentCaptor.getValue().password()).isEqualTo("adminadmin");

    }

    @Test
    @Order(3)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void whenInvalidInput_blankUsername_thenAuthenticateUserShouldReturn400() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("", "password");

        mockMvc.perform(post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.times(0))
                .authenticateUser(authenticationRequest);
    }

    @Order(4)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void whenInvalidInput_blankPassword_thenAuthenticateUserShouldReturn400() throws Exception {
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest("username", "");

        mockMvc.perform(post("/api/v1/auth")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Mockito.verify(authenticationService, Mockito.times(0))
                .authenticateUser(authenticationRequest);
    }
}