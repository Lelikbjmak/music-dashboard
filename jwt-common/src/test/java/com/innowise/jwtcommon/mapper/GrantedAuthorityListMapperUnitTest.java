package com.innowise.jwtcommon.mapper;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class GrantedAuthorityListMapperUnitTest {

    @Mock
    private GrantedAuthorityMapper grantedAuthorityMapper;

    @InjectMocks
    private final GrantedAuthorityListMapper grantedAuthorityListMapper = new GrantedAuthorityListMapper();

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(grantedAuthorityMapper);
        Assertions.assertNotNull(grantedAuthorityListMapper);
    }

    @Test
    void mustMapToEntityList() {
        final List<String> mockGrantedAuthorityDtoList = List.of("ROLE_USER", "ROLE_ADMIN");
        final List<? extends GrantedAuthority> expectedGrantedAuthorityList = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        Mockito.when(grantedAuthorityMapper.mapToEntity("ROLE_USER")).thenReturn(new SimpleGrantedAuthority("ROLE_USER"));
        Mockito.when(grantedAuthorityMapper.mapToEntity("ROLE_ADMIN")).thenReturn(new SimpleGrantedAuthority("ROLE_ADMIN"));

        List<? extends GrantedAuthority> actualGrantedAuthorityList = grantedAuthorityListMapper.mapToEntityList(mockGrantedAuthorityDtoList);

        Mockito.verify(grantedAuthorityMapper, Mockito.times(2)).mapToEntity(Mockito.anyString());

        Assertions.assertNotNull(actualGrantedAuthorityList);
        Assertions.assertEquals(2, actualGrantedAuthorityList.size());
        Assertions.assertEquals(expectedGrantedAuthorityList, actualGrantedAuthorityList);
    }

}