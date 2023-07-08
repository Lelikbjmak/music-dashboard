package com.innowise.usercommon.repository;

import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles(value = "jpa")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(roleRepository);
    }

    @Test
    @Order(2)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustSaveRole() {
        Role role = new Role();
        role.setName(RoleEnum.ROLE_USER);

        Role savedRole = roleRepository.save(role);

        Optional<Role> optionalRole = roleRepository.findById(savedRole.getId());
        Assertions.assertTrue(optionalRole.isPresent());
    }

    @Test
    @Order(3)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustThrowErrorAboutNotUniqueRoleNameOnSave() {

        Role duplicatedRole = new Role();
        duplicatedRole.setName(RoleEnum.ROLE_ADMIN);

        Exception exception = Assertions.assertThrows(DataIntegrityViolationException.class, () ->
                roleRepository.save(duplicatedRole));

        Assertions.assertTrue(exception.getMessage().contains("roles_name_uk"));
    }

    @Test
    @Order(4)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void mustDeleteExistingRoleById() {
        final long id = 1;

        Optional<Role> optionalRole = roleRepository.findById(id);
        Assertions.assertTrue(optionalRole.isPresent());

        roleRepository.deleteById(id);
        Optional<Role> optionalDeletedRole = roleRepository.findById(id);
        Assertions.assertTrue(optionalDeletedRole.isEmpty());
    }

    @Test
    @Order(5)
    @Sql(value = "/sql/01-create-user.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/05-truncate-tables.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void mustReturnRoleByName() {
        RoleEnum roleName = RoleEnum.ROLE_ADMIN;

        Role foundRole = roleRepository.findByName(roleName);
        Assertions.assertNotNull(foundRole);
        Assertions.assertEquals(roleName, foundRole.getName());
    }

    @Test
    @Order(6)
    void mustReturnEmptyRoleByName() {
        RoleEnum roleName = RoleEnum.ROLE_ADMIN;
        Role foundRole = roleRepository.findByName(roleName);
        Assertions.assertNull(foundRole);
    }
}