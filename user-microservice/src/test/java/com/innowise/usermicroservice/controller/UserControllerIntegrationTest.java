package com.innowise.usermicroservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.jwtcommon.security.JwtService;
import com.innowise.jwtcommontest.security.TestUserDetails;
import com.innowise.jwtcommontest.util.TestJwtTokenUtil;
import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usercommon.repository.UserRepository;

import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Set;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenValidInput_thenRegisterNewUserReturn200() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("email@gmail.com")
                .password("testPassword1")
                .confirmedPassword("testPassword1")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<UserDto> response = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, UserDto.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserDto actualUserDto = response.getBody();

        Assertions.assertNotNull(actualUserDto);
        Optional<User> possibleRegisteredUser = userRepository.findById(actualUserDto.id());
        Assertions.assertTrue(possibleRegisteredUser.isPresent());
    }

    @Test
    void whenInvalidInput_blankUsername_thenRegisterNewUserMustReturn400() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username(null)
                .email("email@gmail.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Username is mandatory."));
    }

    @Test
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenInvalidInput_usernameInUse_thenRegisterNewUserMustReturn400(@Value("${user.username}") String username) {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username(username)
                .email("email@gmail.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Username already in use."));
    }

    @Test
    void whenInvalidInput_usernameNotValidFormat_thenRegisterNewUserMustReturn400() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("less")
                .email("email@gmail.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> responseLengthLessThan4 = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseLengthLessThan4.getStatusCode());
        String messageLessThan4 = responseLengthLessThan4.getBody();
        Assertions.assertNotNull(messageLessThan4);
        Assertions.assertTrue(messageLessThan4.contains("Not valid format."));

        registrationUserDto.setUsername("UsernameLengthMoreThan25Characters");
        ResponseEntity<String> responseLengthMoreThan25 = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseLengthMoreThan25.getStatusCode());
        String messageMoreThan25 = responseLengthMoreThan25.getBody();
        Assertions.assertNotNull(messageMoreThan25);
        Assertions.assertTrue(messageMoreThan25.contains("Not valid format."));

        registrationUserDto.setUsername("!$In&*valid%");
        ResponseEntity<String> responseNotValidUsername = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseNotValidUsername.getStatusCode());
        String messageInvalidUsername = responseNotValidUsername.getBody();
        Assertions.assertNotNull(messageInvalidUsername);
        Assertions.assertTrue(messageInvalidUsername.contains("Not valid format."));
    }

    @Test
    void whenInvalidInput_blankEmail_thenRegisterNewUserMustReturn400() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email(null)
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Email is mandatory."));
    }

    @Test
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenInvalidInput_emailInUse_thenRegisterNewUserMustReturn400(@Value("${user.email}") String email) {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email(email)
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Email already in use."));
    }

    @Test
    void whenInvalidInput_emailNotValidFormat_thenRegisterNewUserMustReturn400() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("%8324.com")
                .password("password")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> responseNotValidUsername = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseNotValidUsername.getStatusCode());
        String messageInvalidUsername = responseNotValidUsername.getBody();
        Assertions.assertNotNull(messageInvalidUsername);
        Assertions.assertTrue(messageInvalidUsername.contains("Not valid format."));
    }

    @Test
    void whenInvalidInput_passwordInvalidFormat_thenRegisterNewUserMustReturn400() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("test@gmail.com")
                .password("^51DFJDSB(732")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        RegistrationUserDto passwordMoreThan25CharactersRegDto = RegistrationUserDto.builder()
                .username("username")
                .email("test@gmail.com")
                .password("passwordMoreThan25Characters")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        RegistrationUserDto passwordLessThen8CharactersRegDto = RegistrationUserDto.builder()
                .username("username")
                .email("test@gmail.com")
                .password("pLess8")
                .confirmedPassword("password")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        String message = response.getBody();

        Assertions.assertNotNull(message);
        Assertions.assertTrue(message.contains("Password must contain at least 8 and no more than 25 chars"));

        ResponseEntity<String> responsePasswordMoreThan25 = testRestTemplate.postForEntity("/api/v1/user", passwordMoreThan25CharactersRegDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responsePasswordMoreThan25.getStatusCode());
        String passwordMoreThan25CharsMessage = responsePasswordMoreThan25.getBody();

        Assertions.assertNotNull(passwordMoreThan25CharsMessage);
        Assertions.assertTrue(passwordMoreThan25CharsMessage.contains("Password must contain at least 8 and no more than 25 chars"));

        ResponseEntity<String> responsePasswordLessThan8 = testRestTemplate.postForEntity("/api/v1/user", passwordLessThen8CharactersRegDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responsePasswordLessThan8.getStatusCode());
        String passwordMoreLessThan8CharsMessage = responsePasswordLessThan8.getBody();

        Assertions.assertNotNull(passwordMoreLessThan8CharsMessage);
        Assertions.assertTrue(passwordMoreLessThan8CharsMessage.contains("Password must contain at least 8 and no more than 25 chars"));
    }

    @Test
    void whenInvalidInput_confirmedPasswordNotMatch_thenRegisterNewUserMustReturn400() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("email@gmail.com")
                .password("testPassword1")
                .confirmedPassword("notMatchedPassword")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        ResponseEntity<String> responseNotValidUsername = testRestTemplate.postForEntity("/api/v1/user", registrationUserDto, String.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, responseNotValidUsername.getStatusCode());
        String messageInvalidUsername = responseNotValidUsername.getBody();
        Assertions.assertNotNull(messageInvalidUsername);
        Assertions.assertTrue(messageInvalidUsername.contains("Confirmed password doesn't match."));
    }

    @Test
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenValidInput_thenFindUserByUsernameReturn200(@Value("${user.username}") String username) {
        HttpHeaders headers = new HttpHeaders();
        final String jwt = TestJwtTokenUtil.generateToken(new TestUserDetails(Set.of("ROLE_USER")));
        headers.setBearerAuth(jwt);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<UserDto> response = testRestTemplate.exchange("/api/v1/user/{username}", HttpMethod.GET, requestEntity, UserDto.class, username);
        UserDto actualUserDto = response.getBody();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(actualUserDto);
        Assertions.assertEquals(username, actualUserDto.username());
    }

    @Test
    void whenNotAuthenticated_thenFindUserByUsernameReturn401(@Value("${user.username}") String username) {
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/user/{username}", HttpMethod.GET, HttpEntity.EMPTY, String.class, username);
        String message = response.getBody();
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertNotNull(message);
        Assertions.assertEquals("\"Full authentication is required to access this resource\"", message);
    }

    @Test
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void whenValidInput_thenDeleteUserByIdReturn200() {
        HttpHeaders headers = new HttpHeaders();
        final String jwt = TestJwtTokenUtil.generateToken(new TestUserDetails(Set.of("ROLE_ADMIN")));
        headers.setBearerAuth(jwt);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/user/{id}", HttpMethod.DELETE, requestEntity, Void.class, 1);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void whenNotAuthenticated_thenDeleteUserByIdReturn401() {
        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/user/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, String.class, 1);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        String message = response.getBody();
        Assertions.assertEquals("\"Full authentication is required to access this resource\"", message);
    }

    @Test
    void whenNotAuthorize_thenDeleteUserByIdReturn403() {
        HttpHeaders headers = new HttpHeaders();
        final String jwt = TestJwtTokenUtil.generateToken(new TestUserDetails(Set.of("ROLE_USER")));
        headers.setBearerAuth(jwt);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/user/{id}", HttpMethod.DELETE, requestEntity, String.class, 1);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        String message = response.getBody();
        Assertions.assertEquals("\"Access Denied\"", message);
    }
}
