package com.innowise.usermicroservice.controller;

import com.innowise.usermicroservice.dto.RegistrationUserDto;
import com.innowise.usermicroservice.dto.UserDto;
import com.innowise.usermicroservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto registerUser(@RequestBody @Valid RegistrationUserDto registrationUserDto) {
        return userService.register(registrationUserDto);
    }

    @GetMapping("{username}")
    @ResponseStatus(value = HttpStatus.OK)
    public UserDto findUserByUsername(@PathVariable(name = "username") String username) {
        return userService.findByUsername(username);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUserById(@PathVariable(name = "id") long id) {
        userService.deleteById(id);
    }

}
