package kanbancalendar.project.app.validation;

import kanbancalendar.project.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailUsedValidator implements ConstraintValidator<EmailUsed, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(EmailUsed constraintAnnotation) {}

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.findByEmail(s) == null;
    }

}
