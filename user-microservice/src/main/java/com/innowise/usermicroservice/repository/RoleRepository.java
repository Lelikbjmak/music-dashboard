package com.innowise.usermicroservice.repository;

import com.innowise.usercommon.domain.Role;
import com.innowise.usercommon.domain.domainenum.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query(value = "SELECT role FROM Role role WHERE role.name = :name")
    Role findByName(@Param(value = "name") RoleEnum roleName);

}
