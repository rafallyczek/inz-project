package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.CalendarUser;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.CalendarUserRepository;
import paw.project.calendarapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    //Pola
    private UserRepository userRepository;
    private CalendarUserRepository calendarUserRepository;
    private PasswordEncoder passwordEncoder;

    //Wstrzykiwanie repozytorium użytkowników
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CalendarUserRepository calendarUserRepository){
        this.userRepository = userRepository;
        this.calendarUserRepository = calendarUserRepository;
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
        List<CalendarUser> calendarUsers = calendarUserRepository.findAllByCalendarId(id);
        List<User> users = new ArrayList<>();
        for(CalendarUser calendarUser : calendarUsers){
            User user = userRepository.findById(calendarUser.getUserId().longValue()).get();
            users.add(user);
        }
        return users;
    }

    //Znajdź użytkowników, których nazwa użytkownika zawiera podaną frazę
    public List<User> getAllUsersContainingUsername(String username){
        return userRepository.findByUsernameContaining(username);
    }

    //Sprawdzanie czy stare hasło się zgadza
    public boolean checkOldPassword(User user, String oldPassword){
        return passwordEncoder.matches(oldPassword,user.getPassword());
    }

}
