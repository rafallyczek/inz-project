package paw.project.calendarapp.TO;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UpdatePassword {

    private Long userId;
    @NotNull
    @Size(min=4, message = "Hasło musi mieć co najmniej 4 znaki.")
    private String password;
    private String oldPassword;

}
