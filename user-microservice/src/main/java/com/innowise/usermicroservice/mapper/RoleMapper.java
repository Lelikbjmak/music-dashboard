package com.innowise.usermicroservice.mapper;


import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import com.innowise.usermicroservice.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class RoleMapper {

    @Autowired
    private RoleRepository roleRepository;

    public Role mapToEntity(RoleEnum roleName) {
        return roleRepository.findByName(roleName);
    }

    public RoleEnum mapToDto(Role role) {
        return role != null ? role.getName() : null;
    }
}
