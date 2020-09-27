package paw.project.calendarapp.validation;

import paw.project.calendarapp.TO.UpdatePassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UpdatePassword> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(UpdatePassword updatePassword, ConstraintValidatorContext constraintValidatorContext) {
        //Sprawdzenie czy hasła się zgadzają
        boolean result = updatePassword.getPassword().equals(updatePassword.getMatchPassword());
        if(!result){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate()).addPropertyNode("matchPassword").addConstraintViolation();
        }
        return result;
    }

}
