package com.innowise.usermicroservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.innowise.usercommon.domain.domainenum.RoleEnum;

import java.time.LocalDateTime;
import java.util.Set;

@JsonRootName(value = "user")
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public record UserDto(
        long id,
        String username,
        String email,
        Set<RoleEnum> roleSet,
        LocalDateTime registrationDate,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        boolean enabled
) {
}
