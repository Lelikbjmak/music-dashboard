package com.innowise.usermicroservice.service;

import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usercommon.repository.RoleRepository;
import com.innowise.usercommon.repository.UserRepository;

import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.Set;

@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(userService);
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(roleRepository);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/06-create-roles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustSaveUser() {

        User user = User.builder()
                .username("username")
                .email("email")
                .password("password")
                .roleSet(Set.of(roleRepository.findByName(RoleEnum.ROLE_USER)))
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .credentialsNonExpired(true)
                .build();

        UserDto actualDto = userService.save(user);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(user.getUsername(), actualDto.username());
        Assertions.assertEquals(user.getEmail(), actualDto.email());
        Assertions.assertNotNull(actualDto.registrationDate());

        Optional<User> possibleSavedUser = userRepository.findByUsername("username");
        Assertions.assertTrue(possibleSavedUser.isPresent());
    }

    @Test
    @Order(3)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowExceptionDuplicatedUsernameOrEmail(@Value("${user.username}") String username, @Value("${user.email}") String email) {

        User userDuplicatedUsername = User.builder()
                .username(username)
                .email("email")
                .password("password")
                .roleSet(Set.of(new Role(1, RoleEnum.ROLE_USER)))
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .credentialsNonExpired(true)
                .build();

        User userDuplicatedEmail = User.builder()
                .username(username)
                .email("email")
                .password("password")
                .roleSet(Set.of(new Role(1, RoleEnum.ROLE_USER)))
                .accountNonLocked(true)
                .accountNonExpired(true)
                .enabled(true)
                .credentialsNonExpired(true)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                userService.save(userDuplicatedUsername));

        Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                userService.save(userDuplicatedEmail));
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustFindUserByUsername(@Value("${user.username}") String username) {
        UserDto foundUser = userService.findByUsername(username);
        Assertions.assertNotNull(foundUser);
    }

    @Test
    @Order(5)
    void mustReturnEmptyUserByUsername(@Value("${user.username}") String username) {
        UserDto foundUser = userService.findByUsername(username);
        Assertions.assertNull(foundUser);
    }

    @Test
    @Order(6)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustFindUserByEmail(@Value("${user.email}") String email) {
        UserDto foundUser = userService.findByEmail(email);
        Assertions.assertNotNull(foundUser);
    }

    @Test
    @Order(7)
    void mustReturnEmptyUserByEmail(@Value("${user.email}") String email) {
        UserDto foundUser = userService.findByEmail(email);
        Assertions.assertNull(foundUser);
    }

    @Test
    @Order(8)
    void mustRegisterNewUser(@Value("${user.username}") String username, @Value("${user.email}") String email) {
        final String password = "1111";
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .confirmedPassword(password)
                .build();

        UserDto registeredUserDto = userService.register(registrationUserDto);
        Assertions.assertNotNull(registeredUserDto);

        Optional<User> possibleRegisteredUser = userRepository.findByUsername(username);
        Assertions.assertTrue(possibleRegisteredUser.isPresent());

        User registeredUser = possibleRegisteredUser.get();
        Assertions.assertNotEquals(password, registeredUser.getPassword());
        Assertions.assertNotNull(registeredUser.getLastTimeUpdated());
        Assertions.assertNotNull(registeredUser.getRegistrationDate());
    }
}
