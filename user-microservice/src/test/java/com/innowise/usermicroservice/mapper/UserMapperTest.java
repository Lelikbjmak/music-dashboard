package com.innowise.usermicroservice.mapper;


import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Set;

@SpringBootTest
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(userMapper);
    }

    @Test
    @Order(2)
    void mustMapToEntityFromRegistrationUserDto() {
        RegistrationUserDto registrationUserDto = RegistrationUserDto.builder()
                .username("username")
                .email("test")
                .confirmedPassword("pass")
                .password("pass")
                .roleSet(Set.of(RoleEnum.ROLE_USER))
                .build();

        User user = userMapper.mapToEntity(registrationUserDto);
        Assertions.assertNotNull(user);
    }

    @Test
    @Order(3)
    void mustMapToEmptyEntityFromRegistrationUserDto() {
        RegistrationUserDto registrationUserDto = null;
        User user = userMapper.mapToEntity(registrationUserDto);
        Assertions.assertNull(user);
    }

    @Test
    @Order(4)
    void mustMapToEntityFromDto() {
        UserDto dto = new UserDto(
                1,
                "username",
                "email",
                null,
                null,
                true,
                true,
                true,
                true
        );

        User user = userMapper.mapToEntity(dto);
        Assertions.assertNotNull(user);
    }

    @Test
    @Order(5)
    void mustMapToEmptyEntityFromDto() {
        UserDto dto = null;
        User user = userMapper.mapToEntity(dto);
        Assertions.assertNull(user);
    }

    @Test
    @Order(6)
    void mustMapEntityToDto() {
        User user = User.builder()
                .id(1)
                .username("username")
                .email("email")
                .roleSet(Set.of(new Role(1, RoleEnum.ROLE_USER)))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .registrationDate(LocalDateTime.of(2020, 1, 1, 10, 10, 10))
                .build();

        UserDto dto = userMapper.mapToDto(user);
        Assertions.assertNotNull(user);
    }

    @Test
    @Order(7)
    void mustReturnEmptyDto() {
        UserDto dto = userMapper.mapToDto(null);
        Assertions.assertNull(dto);
    }
}