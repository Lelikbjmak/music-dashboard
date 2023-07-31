package com.innowise.authenticationmicroservice.security;


import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Load user by username: {}", username);

        User user = userRepository.findByUsernameWithEagerRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        return new ApplicationUserDetails(user);
    }
}
