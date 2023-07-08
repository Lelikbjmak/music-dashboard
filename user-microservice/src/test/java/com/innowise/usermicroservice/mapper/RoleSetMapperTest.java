package com.innowise.usermicroservice.mapper;


import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

@SpringBootTest
@ActiveProfiles(value = "integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RoleSetMapperTest {

    @Autowired
    private RoleSetMapper roleSetMapper;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(roleSetMapper);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/06-create-roles.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnEntitySet() {
        Set<Role> roleSet = roleSetMapper.mapToEntitySet(Set.of(RoleEnum.ROLE_USER, RoleEnum.ROLE_ADMIN));
        Assertions.assertNotNull(roleSet);
        Assertions.assertEquals(RoleEnum.values().length, roleSet.size());
    }

    @Test
    @Order(3)
    void mustReturnEmptyEntitySet() {
        Set<Role> roleSet = roleSetMapper.mapToEntitySet(null);
        Assertions.assertNull(roleSet);
    }

    @Test
    @Order(4)
    void mustReturnDtoSet() {
        Set<Role> roleSet = Set.of(
                new Role(1, RoleEnum.ROLE_USER),
                new Role(2, RoleEnum.ROLE_ADMIN)
        );

        Set<RoleEnum> dtoSet = roleSetMapper.mapToDtoSet(roleSet);
        Assertions.assertNotNull(dtoSet);
    }

    @Test
    @Order(5)
    void mustReturnEmptyDtoSet() {
        Set<RoleEnum> dtoSet = roleSetMapper.mapToDtoSet(null);
        Assertions.assertNull(dtoSet);
    }
}