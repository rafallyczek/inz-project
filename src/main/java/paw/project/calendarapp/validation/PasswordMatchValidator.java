package paw.project.calendarapp.validation;

import paw.project.calendarapp.TO.UpdatePassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;

        //Sprawdzenie czy hasła się zgadzają
        if(o instanceof UpdatePassword){
            UpdatePassword updatePassword = (UpdatePassword) o;
            result = updatePassword.getPassword().equals(updatePassword.getMatchPassword());
        }

        if(!result){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(constraintValidatorContext.getDefaultConstraintMessageTemplate()).addPropertyNode("matchPassword").addConstraintViolation();
        }
        return result;
    }

}
