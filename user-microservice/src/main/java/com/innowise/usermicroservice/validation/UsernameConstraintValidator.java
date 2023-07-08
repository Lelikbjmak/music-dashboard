package com.innowise.usermicroservice.validation;

import com.innowise.usercommon.repository.UserRepository;
import com.innowise.usermicroservice.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {

        if (userRepository.findByUsername(username).isPresent()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Username already in use.")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        return true;
    }

}
