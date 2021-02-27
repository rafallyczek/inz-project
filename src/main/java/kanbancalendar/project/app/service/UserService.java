package kanbancalendar.project.app.service;

import kanbancalendar.project.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import kanbancalendar.project.app.model.CalendarRole;
import kanbancalendar.project.app.model.User;
import kanbancalendar.project.app.repository.CalendarRoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    //Pola
    private final UserRepository userRepository;
    private final CalendarRoleRepository calendarRoleRepository;
    private final PasswordEncoder passwordEncoder;

    //Wstrzykiwanie repozytorium użytkowników
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CalendarRoleRepository calendarRoleRepository){
        this.userRepository = userRepository;
        this.calendarRoleRepository = calendarRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Wyszukiwanie użytkownika po nazwie użytkownika
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user != null){
            return user;
        }
        throw new UsernameNotFoundException("Nie znaleziono użytkownika: "+username);
    }

    //Wyszukiwanie użytkownika po id
    public User getUser(Long id){
        return userRepository.findById(id).get();
    }

    //Znajdź wszystkich użytkowników kalendarza
    public List<User> getAllUsersByCalendarId(int id){
        List<CalendarRole> calendarRoles = calendarRoleRepository.findAllByCalendarId((long)id);
        List<User> users = new ArrayList<>();
        for(CalendarRole calendarRole : calendarRoles){
            User user = userRepository.findById(calendarRole.getUserId()).get();
            users.add(user);
        }
        return users;
    }

    //Usuń użytkownika
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    //Znajdź użytkowników, których nazwa użytkownika zawiera podaną frazę
    public List<User> getAllUsersContainingUsername(String username){
        return userRepository.findByUsernameContaining(username);
    }

    //Sprawdzanie czy hasło się zgadza
    public boolean checkPassword(User user, String password){
        return passwordEncoder.matches(password,user.getPassword());
    }

}
