package com.innowise.usermicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usercommon.repository.UserRepository;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import com.innowise.usermicroservice.service.UserService;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "unit")
@WebMvcTest(controllers = UserController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(mockMvc);
        Assertions.assertNotNull(objectMapper);
        Assertions.assertNotNull(userService);
    }

    @Test
    @WithMockUser
    void whenValidHttp_thenRegisterNewUserMustSerializeDeserializeAndReturn200() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("email@gmail.com")
                .password("testPassword1")
                .confirmedPassword("testPassword1")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        UserDto expectedRegisteredUser = new UserDto(
                1,
                "username",
                "email@gmail.com",
                Set.of(RoleEnum.ROLE_USER),
                LocalDateTime.of(2020, 10, 10, 1, 1, 1),
                true,
                true,
                true,
                true
        );

        Mockito.when(userService.register(registrationUserDto)).thenReturn(expectedRegisteredUser);

        String json = mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        UserDto actualRegisteredUserDto = objectMapper.readValue(json, UserDto.class);
        Assertions.assertEquals(expectedRegisteredUser, actualRegisteredUserDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenRegisterNewUserMustCallBusinessLogicAndReturn200() throws Exception {

        final String username = "username";
        final String email = "email@gmail.com";
        final String password = "testPassword1";
        final Set<RoleEnum> roleSet = Set.of(RoleEnum.ROLE_USER);

        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .confirmedPassword(password)
                .roleSet(roleSet)
                .build();

        Mockito.when(userService.register(registrationUserDto)).thenReturn(Mockito.any(UserDto.class));

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        ArgumentCaptor<RegistrationUserDto> registrationDtoCaptor = ArgumentCaptor.forClass(RegistrationUserDto.class);
        Mockito.verify(userService, Mockito.times(1)).register(registrationDtoCaptor.capture());
        assertThat(registrationDtoCaptor.getValue().getUsername()).isEqualTo(username);
        assertThat(registrationDtoCaptor.getValue().getEmail()).isEqualTo(email);
        assertThat(registrationDtoCaptor.getValue().getPassword()).isEqualTo(password);
        assertThat(registrationDtoCaptor.getValue().getConfirmedPassword()).isEqualTo(password);
        assertThat(registrationDtoCaptor.getValue().getRoleSet()).isEqualTo(roleSet);
    }

    @Test
    @WithMockUser
    void whenInvalidInput_blankUsername_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("")
                .email("email@gmail.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_UsernameInUse_thenRegisterNewUserMustReturn400() throws Exception {
        final String usedUsername = "username";

        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username(usedUsername)
                .email("email@gmail.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        Mockito.when(userRepository.findByUsername(usedUsername)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_blankEmail_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_emailInUse_thenRegisterNewUserMustReturn400() throws Exception {
        final String emailInUse = "usedEmail";

        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email(emailInUse)
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        Mockito.when(userRepository.findByEmail(emailInUse)).thenReturn(Optional.of(new User()));

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_notValidEmail_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("notValidEmail.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_blankPassword_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("notValidEmail.com")
                .password("")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_NotValidPasswordSize_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("notValidEmail.com")
                .password("less8")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        registrationUserDto.setPassword("passwordLargerThan25Characters");

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_NotValidPasswordFormat_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("notValidEmail.com")
                .password("%&#@!@#@^##^@")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenInvalidInput_ConfirmedPasswordNotMatches_thenRegisterNewUserMustReturn400() throws Exception {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("notValidEmail.com")
                .password("password")
                .confirmedPassword("notMatchedPassword")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        mockMvc.perform(post("/api/v1/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void whenValidHttp_mustFindUserByUsernameDeserializeResponseAndReturn200() throws Exception {
        final String username = "username";
        final UserDto expectedDto = new UserDto(
                1,
                username,
                "email",
                Set.of(RoleEnum.ROLE_USER),
                LocalDateTime.now(),
                true,
                true,
                true,
                true
        );

        Mockito.when(userService.findByUsername(username)).thenReturn(expectedDto);

        String json = mockMvc.perform(get("/api/v1/user/{username}", username)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        UserDto actualUserDto = objectMapper.readValue(json, UserDto.class);
        Assertions.assertNotNull(actualUserDto);
        Assertions.assertEquals(expectedDto, actualUserDto);
    }

    @Test
    @WithMockUser
    void whenValidInput_thenMustFindUserByUsernameEmptyUserAndReturn200() throws Exception {
        final String username = "username";

        Mockito.when(userService.findByUsername(username)).thenReturn(Mockito.any(UserDto.class));

        String json = mockMvc.perform(get("/api/v1/user/{username}", username)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue(json.isEmpty());
    }

    @Test
    @WithMockUser
    void whenValidAuthenticationRole_thenMustDeleteUserByIdAndReturn200() throws Exception {
        final long id = 1;

        mockMvc.perform(delete("/api/v1/user/{id}", id)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}