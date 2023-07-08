package com.innowise.usermicroservice.mapper;


import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;


@SpringBootTest
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(roleMapper);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/06-create-roles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustMapDtoToEntity() {
        Role role = roleMapper.mapToEntity(RoleEnum.ROLE_USER);
        Assertions.assertNotNull(role);
        Assertions.assertEquals(RoleEnum.ROLE_USER, role.getName());
    }

    @Test
    @Order(3)
    void mustMapDtoToNull() {
        Role role = roleMapper.mapToEntity(RoleEnum.ROLE_USER);
        Assertions.assertNull(role);
    }

    @Test
    @Order(4)
    void mustMapEntityToDto() {
        Role role = new Role(1, RoleEnum.ROLE_USER);
        RoleEnum dto = roleMapper.mapToDto(role);
        Assertions.assertNotNull(dto);
    }

    @Test
    @Order(5)
    void mustMapEntityToNull() {
        RoleEnum dto = roleMapper.mapToDto(null);
        Assertions.assertNull(dto);
    }
}