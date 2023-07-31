package com.innowise.jwtcommon.mapper;

import org.junit.jupiter.api.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class GrantedAuthorityMapperUnitTest {

    private final GrantedAuthorityMapper grantedAuthorityMapper = new GrantedAuthorityMapper();

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(grantedAuthorityMapper);
    }

    @Test
    void mustMapToNotNullEntity() {
        String role = "ROLE_USER";

        GrantedAuthority grantedAuthority = grantedAuthorityMapper.mapToEntity(role);
        Assertions.assertNotNull(grantedAuthority);
        Assertions.assertEquals(role, grantedAuthority.getAuthority());
    }

    @Test
    void mustMapToNotNullDto() {
        String expectedRole = "ROLE_USER";

        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(expectedRole);
        String actualDto = grantedAuthorityMapper.mapToDto(grantedAuthority);

        Assertions.assertNotNull(actualDto);
        Assertions.assertEquals(expectedRole, actualDto);
    }

    @Test
    void mustMapToNullEntity() {
        GrantedAuthority grantedAuthority = grantedAuthorityMapper.mapToEntity(null);
        Assertions.assertNull(grantedAuthority);
    }

    @Test
    void mustMapToNullDto() {
        String actualDto = grantedAuthorityMapper.mapToDto(null);
        Assertions.assertNull(actualDto);
    }
}