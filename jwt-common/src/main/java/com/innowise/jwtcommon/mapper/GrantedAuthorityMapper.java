package com.innowise.jwtcommon.mapper;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class GrantedAuthorityMapper {

    GrantedAuthority mapToEntity(String role) {
        if (role == null)
            return null;

        return new SimpleGrantedAuthority(role);
    }

    String mapToDto(GrantedAuthority authority) {
        if (authority == null)
            return null;

        return authority.getAuthority();
    }
}
