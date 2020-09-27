package paw.project.calendarapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {
    //Wiadomość w przypadku błędu
    String message() default "Passwords don't match.";
    //Umożliwienie tworzenia grup walidacji
    Class<?>[] groups() default {};
    //Umożliwienie przypisywania obiektów payload do ograniczeń (np. do określenia stopnia błędu: ERROR, INFO)
    Class<? extends Payload>[] payload() default {};
}
