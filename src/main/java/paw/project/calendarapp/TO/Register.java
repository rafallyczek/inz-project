package paw.project.calendarapp.TO;

import lombok.Data;
import paw.project.calendarapp.validation.PasswordMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@PasswordMatch(message = "Hasła muszą się zgadzać.")
public class Register {

    private String username;
    @NotNull
    @Size(min=4, message = "Hasło musi mieć co najmniej 4 znaki.")
    private String password;
    private String matchPassword;
    private String email;

}