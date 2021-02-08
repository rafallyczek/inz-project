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

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final InvitationService invitationService;
    private final ReminderService reminderService;
    private final NoteService noteService;

    //Wstrzykiwanie zależności
    @Autowired
    public MessageController(InvitationService invitationService,
                             ReminderService reminderService,
                             NoteService noteService){
        this.invitationService = invitationService;
        this.reminderService = reminderService;
        this.noteService = noteService;
    }

    //Wyświetl wiadomości
    @GetMapping
    public String messages(Model model,
                           @AuthenticationPrincipal User user){
        loadInvitations(model, user);
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
    public String goToNote(@PathVariable Long id,
                           RedirectAttributes redirectAttributes){
        Reminder reminder = reminderService.getReminderById(id);
        reminder.setReminded(true);
        reminderService.updateReminder(reminder);
        Note note = noteService.getNote((long) reminder.getObjectId());
        redirectAttributes.addFlashAttribute("noteId",note.getId().intValue());
        return "redirect:/calendar/id/"+note.getCalendarId()+"/date/"+note.getYear()+"/"+note.getMonth()+"/"+note.getDay();
    }

    //Przejdź do zadania z przypomnienia
    @PostMapping("/goTo/task/{id}")
    public String goToTask(@PathVariable Long id,
                           RedirectAttributes redirectAttributes){
        Reminder reminder = reminderService.getReminderById(id);
        reminder.setReminded(true);
        reminderService.updateReminder(reminder);
        Note note = noteService.getNote((long) reminder.getObjectId());
        redirectAttributes.addFlashAttribute("noteId",note.getId().intValue());
        return "redirect:/calendar/id/"+note.getCalendarId()+"/date/"+note.getYear()+"/"+note.getMonth()+"/"+note.getDay()+"/tasks";
    }

    //Przejdź do wiadomości z przypomnienia
    @PostMapping("/goTo/messages/{id}")
    public String goToMessages(@PathVariable Long id,
                               RedirectAttributes redirectAttributes){
        Reminder reminder = reminderService.getReminderById(id);
        reminder.setReminded(true);
        reminderService.updateReminder(reminder);
        Invitation invitation = invitationService.getInvitation((long) reminder.getObjectId());
        redirectAttributes.addFlashAttribute("invitationId",invitation.getId().intValue());
        return "redirect:/messages";
    }

    //Wczytaj zaproszenia użytkownika i zapisz jako atrybut modelu
    public void loadInvitations(Model model, User user){
        List<Invitation> invitations = invitationService.getInvitationsByUserId(user.getId().intValue());
        model.addAttribute("invitations", invitations);
    }

}
