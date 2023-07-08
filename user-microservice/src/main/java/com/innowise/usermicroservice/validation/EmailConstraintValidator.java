package com.innowise.usermicroservice.validation;

import com.innowise.usercommon.repository.UserRepository;
import com.innowise.usermicroservice.annotation.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        if (userRepository.findByEmail(email).isPresent()) {
            constraintValidatorContext.buildConstraintViolationWithTemplate("Email already in use.")
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }

        return true;
    }
}
