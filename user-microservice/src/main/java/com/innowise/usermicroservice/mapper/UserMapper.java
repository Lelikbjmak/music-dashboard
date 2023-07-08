package com.innowise.usermicroservice.mapper;

import com.innowise.usercommon.domain.User;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = RoleSetMapper.class)
public interface UserMapper {

    @Mapping(target = "accountNonExpired", expression = "java(true)")
    @Mapping(target = "accountNonLocked", expression = "java(true)")
    @Mapping(target = "credentialsNonExpired", expression = "java(true)")
    @Mapping(target = "enabled", expression = "java(true)")
    User mapToEntity(RegistrationUserDto registrationUserDto);

    User mapToEntity(UserDto userDto);

    UserDto mapToDto(User user);
}
