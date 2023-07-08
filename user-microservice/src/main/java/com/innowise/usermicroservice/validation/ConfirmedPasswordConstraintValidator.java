package com.innowise.usermicroservice.validation;


import com.innowise.usermicroservice.annotation.ValidConfirmedPassword;
import com.innowise.usermicroservice.dto.RegistrationUserDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ConfirmedPasswordConstraintValidator implements ConstraintValidator<ValidConfirmedPassword, RegistrationUserDto> {

    public boolean isValid(RegistrationUserDto user, ConstraintValidatorContext constraintValidatorContext) {
        return user.getConfirmedPassword().equals(user.getPassword());
    }
}
