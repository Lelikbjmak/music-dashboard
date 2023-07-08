package com.innowise.usermicroservice.service.impl;

import com.innowise.usercommon.domain.User;
import com.innowise.usercommon.repository.UserRepository;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import com.innowise.usermicroservice.mapper.UserMapper;
import com.innowise.usermicroservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final Argon2PasswordEncoder argon2PasswordEncoder;

    @Override
    @Transactional
    public UserDto save(User user) {
        User savedUser = userRepository.save(user);
        return userMapper.mapToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto register(RegistrationUserDto registrationUserDto) {
        User user = userMapper.mapToEntity(registrationUserDto);
        String hashPassword = argon2PasswordEncoder.encode(registrationUserDto.getPassword());
        user.setPassword(hashPassword);
        User registeredUser = userRepository.save(user);
        return userMapper.mapToDto(registeredUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User foundUser = userRepository.findByUsername(username).orElse(null);
        return userMapper.mapToDto(foundUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User foundUser = userRepository.findByEmail(email).orElse(null);
        return userMapper.mapToDto(foundUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }
}
