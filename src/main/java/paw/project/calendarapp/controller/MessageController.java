package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import paw.project.calendarapp.model.Invitation;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.DbReminder;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.service.InvitationService;
import paw.project.calendarapp.service.NoteService;
import paw.project.calendarapp.service.ReminderService;
import paw.project.calendarapp.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private InvitationService invitationService;
    private ReminderService reminderService;
    private NoteService noteService;
    private UserService userService;

    //Wstrzykiwanie zależności
    @Autowired
    public MessageController(InvitationService invitationService, ReminderService reminderService, NoteService noteService, UserService userService){
        this.invitationService = invitationService;
        this.reminderService = reminderService;
        this.noteService = noteService;
        this.userService = userService;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void setModelAttributes(Model model, @AuthenticationPrincipal User user){
        if(user!=null){
            loadInvitations(model, user);
            loadReminders(model, user);
        }
    }

    //Wyświetl wiadomości
    @GetMapping
    public String messages(){
        return "messages";
    }

    //Akceptuj zaproszenie
    @GetMapping("/accept/{id}")
    public String acceptInvitation(@PathVariable Long id){
        invitationService.acceptInvitation(id);
        return "redirect:/messages";
    }

    //Odrzuć zaproszenie
    @GetMapping("/reject/{id}")
    public String rejectInvitation(@PathVariable Long id){
        invitationService.deleteInvitation(id);
        return "redirect:/messages";
    }

    //Ustaw czas przypomnienia na 30 minut (późniejsze przypomnienie)
    @PostMapping("/remindLater/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void remindLater(@PathVariable Long id){
        DbReminder dbReminder = reminderService.getReminderById(id);
        dbReminder.setReminderTime(30);
        reminderService.updateReminder(dbReminder);
    }

    //Przejdź do notki z przypomnienia
    @PostMapping("/goToNote/{id}")
    public String goToNote(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user){
        DbReminder dbReminder = reminderService.getReminderById(id);
        dbReminder.setReminded(true);
        reminderService.updateReminder(dbReminder);
        Note note = noteService.getNote((long) dbReminder.getNoteId());
        User owner = userService.getUser((long) note.getUserId());
        note.setNoteDate(user.getTimezone(),owner.getTimezone());
        redirectAttributes.addFlashAttribute("calendarId",note.getCalendarId());
        redirectAttributes.addFlashAttribute("dayNum",note.getDate().getDayOfMonth());
        return "redirect:/calendar/setDayNumberAndCalendarId";
    }

    //Wczytaj zaproszenia użytkownika i zapisz jako atrybut modelu
    public void loadInvitations(Model model, User user){
        List<Invitation> invitations = invitationService.getInvitationsByUserId(user.getId().intValue());
        model.addAttribute("invitations", invitations);
    }

    //Wczytaj przypomnienia użytkownika i zapisz jako atrybut modelu
    public void loadReminders(Model model, User user){
        List<DbReminder> reminders = reminderService.getAllRemindersByUserId(user.getId().intValue(),user.getTimezone());
        reminders.removeIf(DbReminder::isReminded);
        model.addAttribute("reminders", reminders);
    }

}
