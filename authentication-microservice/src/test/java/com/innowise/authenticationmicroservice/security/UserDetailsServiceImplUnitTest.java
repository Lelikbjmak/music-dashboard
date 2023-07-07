package com.innowise.authenticationmicroservice.security;

import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@ExtendWith(value = MockitoExtension.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserDetailsServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @Order(1)
    void contextLoads() {
        Assertions.assertNotNull(userRepository);
        Assertions.assertNotNull(userDetailsService);
    }

    @Test
    @Order(2)
    void mustLoadUserByUsername() {
        final String username = "username";
        User foundUser = User.builder()
                .username(username)
                .build();

        Mockito.when(userRepository.findByUsernameWithEagerRoles(username))
                .thenReturn(Optional.of(foundUser));

        UserDetails actualUserDetails = userDetailsService.loadUserByUsername(username);

        Mockito.verify(userRepository, Mockito.times(1)).findByUsernameWithEagerRoles(username);

        Assertions.assertNotNull(actualUserDetails);
        Assertions.assertEquals(username, actualUserDetails.getUsername());
    }

    @Test
    @Order(3)
    void mustThrowExceptionUsernameNotFound() {
        String username = "nonExistingUsername";

        Mockito.when(userRepository.findByUsernameWithEagerRoles(username))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(username));

        Mockito.verify(userRepository, Mockito.times(1)).findByUsernameWithEagerRoles(username);
    }
}