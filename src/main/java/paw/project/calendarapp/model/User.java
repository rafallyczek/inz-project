package paw.project.calendarapp.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
@Table(name = "app_users")
public class User implements UserDetails {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String timezone;
    private Integer reminderTime;

    //Konstruktor parametrowy
    public User(String username, String password, String email, String timezone, Integer reminderTime) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.timezone = timezone;
        this.reminderTime = reminderTime;
    }

    //Metody interfejsu UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
