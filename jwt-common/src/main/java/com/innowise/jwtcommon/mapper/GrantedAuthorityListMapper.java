package com.innowise.jwtcommon.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class GrantedAuthorityListMapper {

    @Autowired
    private GrantedAuthorityMapper grantedAuthorityMapper;

    public List<GrantedAuthority> mapToEntityList(List<String> roleList) {
        return roleList.stream()
                .map(grantedAuthorityMapper::mapToEntity)
                .toList();
    }
}
