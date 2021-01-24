package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import paw.project.calendarapp.model.*;
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
    public MessageController(InvitationService invitationService,
                             ReminderService reminderService,
                             NoteService noteService,
                             UserService userService){
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
        Reminder reminder = reminderService.getReminderById(id);
        reminder.setReminderTime(30);
        reminderService.updateReminder(reminder);
    }

    //Przejdź do notki z przypomnienia
    @PostMapping("/goTo/note/{id}")
    public String goToNote(@PathVariable Long id, RedirectAttributes redirectAttributes, @AuthenticationPrincipal User user){
        Reminder reminder = reminderService.getReminderById(id);
        reminder.setReminded(true);
        reminderService.updateReminder(reminder);
        Note note = noteService.getNote((long) reminder.getObjectId());
        User owner = userService.getUser((long) note.getUserId());
        note.setNoteDate(user.getTimezone(),owner.getTimezone());
        redirectAttributes.addFlashAttribute("calendarId",note.getCalendarId());
        redirectAttributes.addFlashAttribute("dayNum",note.getDate().getDayOfMonth());
        return "redirect:/calendar/setDayNumberAndCalendarId";
    }

    //Przejdź do wiadomości z przypomnienia
    @PostMapping("/goTo/messages/{id}")
    public String goToMessages(@PathVariable Long id){
        Reminder reminder = reminderService.getReminderById(id);
        reminder.setReminded(true);
        reminderService.updateReminder(reminder);
        return "redirect:/messages";
    }

    //Wczytaj zaproszenia użytkownika i zapisz jako atrybut modelu
    public void loadInvitations(Model model, User user){
        List<Invitation> invitations = invitationService.getInvitationsByUserId(user.getId().intValue());
        model.addAttribute("invitations", invitations);
    }

}
