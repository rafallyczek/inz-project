package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.TO.*;
import paw.project.calendarapp.model.ReminderCheck;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.UserRepository;
import paw.project.calendarapp.service.ReminderService;
import paw.project.calendarapp.service.UserService;

import javax.validation.Valid;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private ReminderService reminderService;
    private List<String> zones;

    //Wstrzykiwanie repozytorium
    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserService userService, ReminderService reminderService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.reminderService = reminderService;
        Set<String> zonesSet = ZoneId.getAvailableZoneIds();
        zones = new ArrayList<>(zonesSet);
        Collections.sort(zones);
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

            UpdateTimezone updateTimezone = new UpdateTimezone();
            updateTimezone.setUserId(loggedUser.getId());
            updateTimezone.setTimezone(loggedUser.getTimezone());

            UpdateReminderTime updateReminderTime = new UpdateReminderTime();
            updateReminderTime.setUserId(loggedUser.getId());
            updateReminderTime.setReminderTime(loggedUser.getReminderTime());

            model.addAttribute("updateEmail", updateEmail);
            model.addAttribute("updatePassword", updatePassword);
            model.addAttribute("updateTimezone", updateTimezone);
            model.addAttribute("updateReminderTime", updateReminderTime);
        }
        model.addAttribute("register", new Register());
        model.addAttribute("zones", zones);
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
    public String addUser(@Valid @ModelAttribute("register") Register register, Errors errors){
        if(errors.hasErrors()){
            return "register";
        }
        User newUser = new User(register.getUsername(),passwordEncoder.encode(register.getPassword()),register.getEmail(),register.getTimezone(),30);
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

    //Aktualizuj strefę czasową użytkownika
    @PostMapping("/updateTimezone")
    public String updateTimezone(@ModelAttribute("updateTimezone") UpdateTimezone updateTimezone){
        User updateUser = userRepository.findById(updateTimezone.getUserId()).get();
        updateUser.setTimezone(updateTimezone.getTimezone());
        userRepository.save(updateUser);
        return "redirect:/user";
    }

    //Aktualizuj czas przypomnienia
    @PostMapping("/updateReminderTime")
    public String updateReminderTime(@ModelAttribute("updateReminderTime") UpdateReminderTime updateReminderTime){
        User updateUser = userRepository.findById(updateReminderTime.getUserId()).get();
        updateUser.setReminderTime(updateReminderTime.getReminderTime());
        userRepository.save(updateUser);
        List<ReminderCheck> reminderChecks = reminderService.getAllReminderChecksByUserId(updateUser.getId().intValue());
        for(ReminderCheck reminderCheck : reminderChecks){
            reminderCheck.setReminderTime(updateReminderTime.getReminderTime());
            reminderService.updateReminderCheck(reminderCheck);
        }
        return "redirect:/user";
    }

}
