package com.innowise.usermicroservice.service;

import com.innowise.usercommon.domain.User;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;


public interface UserService {

    UserDto register(RegistrationUserDto registrationUserDto);

    UserDto save(User user);

    UserDto findByUsername(String username);

    UserDto findByEmail(String username);

    void deleteById(long id);
}
