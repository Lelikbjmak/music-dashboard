package com.innowise.usermicroservice.service;

import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usercommon.repository.UserRepository;

import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import com.innowise.usermicroservice.mapper.UserMapper;
import com.innowise.usermicroservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Argon2PasswordEncoder argon2PasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(userMapper);
        Assertions.assertNotNull(argon2PasswordEncoder);
        Assertions.assertNotNull(userService);
    }

    @Test
    @Order(2)
    @Transactional
    void mustSaveUser() {
        User user = User.builder()
                .username("testUsername")
                .email("testMail")
                .build();

        User savedUser = User.builder()
                .id(1)
                .username("testUsername")
                .email("testMail")
                .build();

        UserDto expectedDto = new UserDto(
                1,
                "testUsername",
                "testMail",
                null,
                null,
                true,
                true,
                true,
                true
        );

        Mockito.when(userRepository.save(user)).thenReturn(savedUser);
        Mockito.when(userMapper.mapToDto(savedUser)).thenReturn(expectedDto);

        UserDto actualDto = userService.save(user);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
        Mockito.verify(userMapper, Mockito.times(1)).mapToDto(savedUser);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @Order(3)
    @Transactional
    void mustRegisterNewUser() {

        final String username = "testUsername";
        final String password = "testPassword";
        final String email = "testEmail";

        String encodedPassword = UUID.randomUUID().toString();

        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username(username)
                .email(email)
                .password(password)
                .confirmedPassword(password)
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        User mappedEntity = User.builder()
                .username(username)
                .email(password)
                .roleSet(Set.of(new Role(1, RoleEnum.ROLE_USER)))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        User savedEntity = User.builder()
                .id(1)
                .username(username)
                .email(encodedPassword)
                .roleSet(Set.of(new Role(1, RoleEnum.ROLE_USER)))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .registrationDate(LocalDateTime.of(2020, 1, 1, 1, 10, 10, 10))
                .build();

        UserDto expectedDto = new UserDto(
                1,
                username,
                email,
                Set.of(RoleEnum.ROLE_USER),
                LocalDateTime.of(2020, 1, 1, 1, 10, 10, 10),
                true,
                true,
                true,
                true
        );

        Mockito.when(userMapper.mapToEntity(registrationUserDto)).thenReturn(mappedEntity);
        Mockito.when(argon2PasswordEncoder.encode(password)).thenReturn(UUID.randomUUID().toString());
        Mockito.when(userRepository.save(mappedEntity)).thenReturn(savedEntity);
        Mockito.when(userMapper.mapToDto(savedEntity)).thenReturn(expectedDto);

        UserDto actualDto = userService.register(registrationUserDto);

        Mockito.verify(argon2PasswordEncoder, Mockito.times(1)).encode(password);
        Mockito.verify(userRepository, Mockito.times(1)).save(mappedEntity);
        Mockito.verify(userMapper, Mockito.times(1)).mapToDto(savedEntity);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @Order(4)
    @Transactional(readOnly = true)
    void mustFindUserByUsername() {
        final String username = "testUsername";

        User user = User.builder()
                .id(1)
                .username(username)
                .build();

        UserDto expectedDto = new UserDto(
                1,
                username,
                null,
                null,
                null,
                true,
                true,
                true,
                true
        );

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.mapToDto(user)).thenReturn(expectedDto);

        UserDto actualDto = userService.findByUsername(username);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @Order(5)
    @Transactional(readOnly = true)
    void mustReturnNullByUsername() {
        final String username = "nonExistingUsername";

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        Mockito.when(userMapper.mapToDto(null)).thenReturn(null);

        UserDto actualDto = userService.findByUsername(username);

        Assertions.assertNull(actualDto);
    }

    @Test
    @Order(6)
    @Transactional(readOnly = true)
    void mustFindUserByEmail() {
        final String email = "testEmail";

        User user = User.builder()
                .id(1)
                .email(email)
                .build();

        UserDto expectedDto = new UserDto(
                1,
                null,
                email,
                null,
                null,
                true,
                true,
                true,
                true
        );

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(userMapper.mapToDto(user)).thenReturn(expectedDto);

        UserDto actualDto = userService.findByEmail(email);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    @Order(7)
    @Transactional(readOnly = true)
    void mustReturnNullByEmail() {
        final String email = "nonExistingEmail";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(userMapper.mapToDto(null)).thenReturn(null);

        UserDto actualDto = userService.findByEmail(email);

        Assertions.assertNull(actualDto);
    }
}