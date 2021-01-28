package paw.project.calendarapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailUsedValidator.class)
@Documented
public @interface EmailUsed {
    //Wiadomość w przypadku błędu
    String message() default "This email is already in use.";
    //Umożliwienie tworzenia grup walidacji
    Class<?>[] groups() default {};
    //Umożliwienie przypisywania obiektów payload do ograniczeń (np. do określenia stopnia błędu: ERROR, INFO)
    Class<? extends Payload>[] payload() default {};
}
