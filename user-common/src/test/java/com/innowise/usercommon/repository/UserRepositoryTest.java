package com.innowise.usercommon.repository;

import com.innowise.usercommon.domain.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles(value = "jpa")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // turn off embedded database
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(userRepository);
    }

    @Test
    @Order(1)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustSaveUser() {
        User user = User.builder()
                .username("test")
                .build();

        User savedUser = userRepository.save(user);
        Assertions.assertNotNull(savedUser);

        Optional<User> optionalFoundSavedUser = userRepository.findById(savedUser.getId());
        Assertions.assertTrue(optionalFoundSavedUser.isPresent());
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowErrorAboutNotUniqueUsernameOnSave(@Value(value = "${user.username}") final String username) {
        User user = User.builder()
                .username(username)
                .build();

        Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.save(user));

        Assertions.assertTrue(exception.getMessage().contains("users_username_uk"));
    }

    @Test
    @Order(3)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowErrorAboutNotUniqueEmailOnSave(@Value(value = "${user.email}") final String email) {
        User user = User.builder()
                .email(email)
                .build();

        Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.save(user));

        Assertions.assertTrue(exception.getMessage().contains("users_mail_uk"));
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void mustDeleteExistingUserById() {
        final long id = 1;

        Optional<User> optionalUser = userRepository.findById(id);
        Assertions.assertTrue(optionalUser.isPresent());

        userRepository.deleteById(id);
        Optional<User> optionalDeletedUser = userRepository.findById(id);
        Assertions.assertTrue(optionalDeletedUser.isEmpty());
    }

    @Test
    @Order(5)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnUserByUsername(@Value(value = "${user.username}") final String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Assertions.assertTrue(optionalUser.isPresent());
    }

    @Test
    @Order(6)
    void mustReturnEmptyUserByUsername(@Value(value = "${user.username}") final String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        Assertions.assertTrue(optionalUser.isEmpty());
    }

    @Test
    @Order(7)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnUserByEmail(@Value(value = "${user.email}") final String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        Assertions.assertTrue(optionalUser.isPresent());
    }

    @Test
    @Order(8)
    void mustReturnEmptyUserByEmail(@Value(value = "${user.email}") final String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        Assertions.assertTrue(optionalUser.isEmpty());
    }

    @Test
    @Order(9)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnUserByUsernameWithEagerRoles(@Value(value = "${user.username}") final String username) {
        Optional<User> optionalUser = userRepository.findByUsernameWithEagerRoles(username);
        Assertions.assertTrue(optionalUser.isPresent());

        User foundUser = optionalUser.get();
        Assertions.assertEquals(username, foundUser.getUsername());
    }

    @Test
    @Order(10)
    void mustReturnEmptyUserByUsernameWithEagerRoles(@Value(value = "${user.username}") final String username) {
        Optional<User> optionalUser = userRepository.findByUsernameWithEagerRoles(username);
        Assertions.assertTrue(optionalUser.isEmpty());
    }

    @Test
    @Order(11)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnUserById() {
        Optional<User> optionalUser = userRepository.findById(1L);
        Assertions.assertTrue(optionalUser.isPresent());
    }

    @Test
    @Order(12)
    void mustReturnEmptyUserById() {
        Optional<User> optionalUser = userRepository.findById(1L);
        Assertions.assertTrue(optionalUser.isEmpty());
    }

}