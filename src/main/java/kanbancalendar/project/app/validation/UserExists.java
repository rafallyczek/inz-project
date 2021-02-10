package kanbancalendar.project.app.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserExistsValidator.class)
@Documented
public @interface UserExists {
    //Wiadomość w przypadku błędu
    String message() default "User with given username already exists.";
    //Umożliwienie tworzenia grup walidacji
    Class<?>[] groups() default {};
    //Umożliwienie przypisywania obiektów payload do ograniczeń (np. do określenia stopnia błędu: ERROR, INFO)
    Class<? extends Payload>[] payload() default {};
}
