package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.TO.*;
import paw.project.calendarapp.model.Reminder;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ReminderService reminderService;
    private final List<String> zones;

    //Wstrzykiwanie repozytorium
    @Autowired
    public UserController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          UserService userService,
                          ReminderService reminderService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.reminderService = reminderService;
        Set<String> zonesSet = ZoneId.getAvailableZoneIds();
        zones = new ArrayList<>(zonesSet);
        Collections.sort(zones);
    }

    //Wyświetlanie widoku ze szczegółami konta
    @GetMapping
    public String showUser(Model model,
                       @AuthenticationPrincipal User user){
        model.addAttribute("zones", zones);
        setUpUpdateEmail(model, user);
        setUpUpdatePassword(model, user);
        setUpUpdateTimezone(model, user);
        setUpUpdateReminderTime(model, user);
        return "user";
    }

    //Wyświetlanie formularza rejestracyjnego
    @GetMapping("/register")
    public String showRegister(Model model){
        model.addAttribute("register", new Register());
        model.addAttribute("zones", zones);
        return "register";
    }

    //Dodaj użytkownika
    @PostMapping("/add")
    public String addUser(@Valid @ModelAttribute("register") Register register,
                          Errors errors,
                          Model model){
        if(errors.hasErrors()){
            model.addAttribute("zones", zones);
            return "register";
        }
        User newUser = new User(register.getUsername(),passwordEncoder.encode(register.getPassword()),register.getEmail(),register.getTimezone(),30);
        userRepository.save(newUser);
        return "redirect:/login";
    }

    //Aktualizuj email użytkownika
    @PostMapping("/updateEmail")
    public String updateEmail(@Valid @ModelAttribute("updateEmail") UpdateEmail updateEmail,
                              Errors errors,
                              Model model,
                              @AuthenticationPrincipal User user){
        if(errors.hasErrors()){
            model.addAttribute("zones", zones);
            setUpUpdatePassword(model, user);
            setUpUpdateTimezone(model, user);
            setUpUpdateReminderTime(model, user);
            return "user";
        }
        User updateUser = userRepository.findById(updateEmail.getUserId()).get();
        updateUser.setEmail(updateEmail.getEmail());
        userRepository.save(updateUser);
        return "redirect:/user";
    }

    //Aktualizuj hasło użytkownika
    @PostMapping("/updatePassword")
    public String updatePassword(@Valid @ModelAttribute("updatePassword") UpdatePassword updatePassword,
                                 Errors errors,
                                 Model model,
                                 @AuthenticationPrincipal User user){
        if(errors.hasErrors()){
            model.addAttribute("zones", zones);
            setUpUpdateEmail(model, user);
            setUpUpdateTimezone(model, user);
            setUpUpdateReminderTime(model, user);
            return "user";
        }
        User updateUser = userRepository.findById(updatePassword.getUserId()).get();
        if(!userService.checkOldPassword(updateUser, updatePassword.getOldPassword())){
            setUpUpdateEmail(model, user);
            setUpUpdateTimezone(model, user);
            setUpUpdateReminderTime(model, user);
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
        List<Reminder> reminders = reminderService.getAllRemindersByUserId(updateUser.getId().intValue(),updateUser.getTimezone());
        for(Reminder reminder : reminders){
            reminder.setReminderTime(updateReminderTime.getReminderTime());
            reminderService.updateReminder(reminder);
        }
        return "redirect:/user";
    }

    //------------------------------------------------------------------------------------
    //Metody ustawiające atrybuty modelu

    //Ustaw atrybut updateEmail
    public void setUpUpdateEmail(Model model, User user){
        UpdateEmail updateEmail = new UpdateEmail();
        updateEmail.setEmail(user.getEmail());
        updateEmail.setUserId(user.getId());
        model.addAttribute("updateEmail", updateEmail);
    }

    //Ustaw atrybut updatePassword
    public void setUpUpdatePassword(Model model, User user){
        UpdatePassword updatePassword = new UpdatePassword();
        updatePassword.setUserId(user.getId());
        model.addAttribute("updatePassword", updatePassword);
    }

    //Ustaw atrybut updateTimezone
    public void setUpUpdateTimezone(Model model, User user){
        UpdateTimezone updateTimezone = new UpdateTimezone();
        updateTimezone.setUserId(user.getId());
        updateTimezone.setTimezone(user.getTimezone());
        model.addAttribute("updateTimezone", updateTimezone);
    }

    //Ustaw atrybut updateReminderTime
    public void setUpUpdateReminderTime(Model model, User user){
        UpdateReminderTime updateReminderTime = new UpdateReminderTime();
        updateReminderTime.setUserId(user.getId());
        updateReminderTime.setReminderTime(user.getReminderTime());
        model.addAttribute("updateReminderTime", updateReminderTime);
    }

}
