package kanbancalendar.project.app.TO;

import kanbancalendar.project.app.validation.EmailUsed;
import lombok.Data;
import kanbancalendar.project.app.validation.PasswordMatch;
import kanbancalendar.project.app.validation.UserExists;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@PasswordMatch(message = "Hasła muszą się zgadzać.")
public class Register {

    @NotNull
    @Size(min=4, message = "Nazwa użytkownika musi mieć co najmniej 4 znaki.")
    @UserExists(message = "Podana nazwa użytkownika jest używana.")
    private String username;
    @NotNull
    @Size(min=4, message = "Hasło musi mieć co najmniej 4 znaki.")
    private String password;
    private String matchPassword;
    @NotBlank(message = "Adres email jest obowiązkowy.")
    @Email(message = "Nieprawidłowy email.")
    @EmailUsed(message = "Podany email jest używany.")
    private String email;
    private String timezone;

}
