package com.innowise.usermicroservice.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usermicroservice.annotation.ValidConfirmedPassword;
import com.innowise.usermicroservice.annotation.ValidEmail;
import com.innowise.usermicroservice.annotation.ValidPassword;
import com.innowise.usermicroservice.annotation.ValidUsername;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ValidConfirmedPassword
@JsonRootName(value = "user")
public class RegistrationUserDto {

    @ValidUsername
    private String username;

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;

    private Set<RoleEnum> roleSet;

    private String confirmedPassword;
}