package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.UserRepository;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    //Wstrzykiwanie repozytorium
    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Dodaj do modelu obiekt User
    @ModelAttribute
    public void calendar(Model model, @AuthenticationPrincipal User user){
        if(user!=null) {
            User loggedUser = userRepository.findByUsername(user.getUsername());
            model.addAttribute("loggedUser", loggedUser);
        }
        model.addAttribute("user", new User());
    }

    //Wyświetlanie widoku ze szczegółami konta
    @GetMapping
    public String user(){
        return "user";
    }

    //Wyświetlanie formularza rejestracyjnego
    @GetMapping("/register")
    public String register(){
        return "register";
    }

    //Dodaj użytkownika
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("user") User user, Errors errors){
        if(errors.hasErrors()){
            return "register";
        }
        User newUser = new User(user.getUsername(),passwordEncoder.encode(user.getPassword()),user.getEmail());
        userRepository.save(newUser);
        return "redirect:/login";
    }

    //Aktualizuj dane użytkownika
    @PostMapping("/update")
    public String updateNote(@Valid @ModelAttribute("loggedUser") User user, Errors errors){
        if(errors.hasErrors()){
            return "user";
        }
        User newUser = userRepository.findById(user.getId()).get();
        if(user.getEmail()!=null){
            newUser.setEmail(user.getEmail());
        }
        if(!user.getPassword().isEmpty()){
            newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(newUser);
        return "user";
    }

}
