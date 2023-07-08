package com.innowise.usermicroservice.mapper;



import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = RoleMapper.class)
public interface RoleSetMapper {

    Set<Role> mapToEntitySet(Set<RoleEnum> roleNameSet);

    Set<RoleEnum> mapToDtoSet(Set<Role> roleSet);
}
