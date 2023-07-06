package com.innowise.usercommon.repository;

import com.innowise.usercommon.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = "fetchUserWithRoles")
    @Query(value = "SELECT user from User user where user.username = :username")
    Optional<User> findByUsernameWithEagerRoles(@Param(value = "username") String username);

    @Query(value = "SELECT user from User user where user.username = :username")
    Optional<User> findByUsername(@Param(value = "username") String username);

    @Query(value = "SELECT user FROM User user WHERE user.email = :email")
    Optional<User> findByEmail(@Param(value = "email") String email);
}
