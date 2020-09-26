package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.TO.UpdateEmail;
import paw.project.calendarapp.TO.UpdatePassword;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.UserRepository;
import paw.project.calendarapp.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    //Wstrzykiwanie repozytorium
    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    //Dodaj do modelu obiekt User
    @ModelAttribute
    public void calendar(Model model, @AuthenticationPrincipal User user){
        if(user!=null) {
            User loggedUser = userRepository.findByUsername(user.getUsername());

            UpdateEmail updateEmail = new UpdateEmail();
            updateEmail.setEmail(loggedUser.getEmail());
            updateEmail.setUserId(loggedUser.getId());

            UpdatePassword updatePassword = new UpdatePassword();
            updatePassword.setUserId(loggedUser.getId());

            model.addAttribute("updateEmail", updateEmail);
            model.addAttribute("updatePassword", updatePassword);
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

    //Aktualizuj email użytkownika
    @PostMapping("/updateEmail")
    public String updateEmail(@Valid @ModelAttribute("updateEmail") UpdateEmail updateEmail, Errors errors){
        if(errors.hasErrors()){
            return "user";
        }
        User updateUser = userRepository.findById(updateEmail.getUserId()).get();
        updateUser.setEmail(updateEmail.getEmail());
        userRepository.save(updateUser);
        return "redirect:/user";
    }

    //Aktualizuj hasło użytkownika
    @PostMapping("/updatePassword")
    public String updatePassword(@Valid @ModelAttribute("updatePassword") UpdatePassword updatePassword, Errors errors){
        if(errors.hasErrors()){
            return "user";
        }
        User updateUser = userRepository.findById(updatePassword.getUserId()).get();
        if(!userService.checkOldPassword(updateUser, updatePassword.getOldPassword())){
            return "user";
        }
        updateUser.setPassword(passwordEncoder.encode(updatePassword.getPassword()));
        userRepository.save(updateUser);
        return "redirect:/user";
    }

}
