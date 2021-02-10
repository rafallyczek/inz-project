package kanbancalendar.project.app.TO;

import kanbancalendar.project.app.validation.EmailUsed;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UpdateEmail {

    private Long userId;
    @NotBlank(message = "Adres email Nie może być pusty.")
    @Email(message = "Nieprawidłowy email.")
    @EmailUsed(message = "Podany email jest używany.")
    private String email;

}
