package kanbancalendar.project.app.validation;

import kanbancalendar.project.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserExistsValidator implements ConstraintValidator<UserExists, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UserExists constraintAnnotation) {}

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByUsername(s) == null;
    }

}
