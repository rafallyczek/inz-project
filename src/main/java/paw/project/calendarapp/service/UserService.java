package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    //Pola
    private UserRepository userRepository;

    //Wstrzykiwanie repozytorium użytkowników
    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    //Wyszukiwanie użytkownika
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if(user != null){
            return user;
        }
        throw new UsernameNotFoundException("Nie znaleziono użytkownika: "+username);
    }

}
