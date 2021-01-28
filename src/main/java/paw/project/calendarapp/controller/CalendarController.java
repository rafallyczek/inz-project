package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import paw.project.calendarapp.TO.AddNote;
import paw.project.calendarapp.TO.UpdateNote;
import paw.project.calendarapp.component.Calendar;
import paw.project.calendarapp.model.*;
import paw.project.calendarapp.service.CalendarService;
import paw.project.calendarapp.service.InvitationService;
import paw.project.calendarapp.service.NoteService;
import paw.project.calendarapp.service.UserService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private Calendar calendar;
    private NoteService noteService;
    private CalendarService calendarService;
    private UserService userService;
    private InvitationService invitationService;
    private int calendarId;
    private int dayNumber;

    //Wstrzykiwanie zależności
    @Autowired
    public CalendarController(Calendar calendar,
                              NoteService noteService,
                              CalendarService calendarService,
                              UserService userService,
                              InvitationService invitationService){
        this.calendar = calendar;
        this.noteService = noteService;
        this.calendarService = calendarService;
        this.userService = userService;
        this.invitationService = invitationService;
        this.calendarId = -1;
        this.dayNumber = -1;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void setModelAttributes(Model model, @AuthenticationPrincipal User user){
        if(user!=null){
            loadCalendars(model, user);

            DbCalendar dbCalendar = new DbCalendar();
            AddNote addNote = new AddNote();
            String day = dayNumber<10 ? "0"+dayNumber : String.valueOf(dayNumber);
            String month = this.calendar.getMonth()<10 ? "0"+this.calendar.getMonth() : String.valueOf(this.calendar.getMonth());

            dbCalendar.setOwnerId(user.getId().intValue());
            addNote.setCalendarId(this.calendarId);
            addNote.setUserId(user.getId().intValue());
            addNote.setDate(this.calendar.getYear()+"-"+month+"-"+day);
            addNote.setTime("00:00");

            model.addAttribute("addNote", addNote);
            model.addAttribute("calendar", this.calendar);
            model.addAttribute("dbCalendar", dbCalendar);
            model.addAttribute("dayNumber", this.dayNumber);
        }
    }

    //Ustaw numer dnia
    @PostMapping("/setDayNumber")
    public String setDayNumber(@RequestParam int dayNumber){
        this.dayNumber = dayNumber;
        return "redirect:/calendar/allNotes";
    }

    //Ustaw id kalendarza
    @PostMapping("/setCalendarId")
    public String setCalendarId(@RequestParam int calendarId){
        this.calendarId = calendarId;
        return "redirect:/calendar";
    }

    //Ustaw numer dnia i id kalendarza
    @GetMapping("/setDayNumberAndCalendarId")
    public String setDayNumberAndCalendarId(@ModelAttribute("calendarId") Integer calendarId, @ModelAttribute("dayNum") int dayNum){
        this.calendarId = calendarId;
        this.dayNumber = dayNum;
        return "redirect:/calendar/allNotes";
    }

    //Wyświetl kalendarz
    @GetMapping
    public String showCalendar(@AuthenticationPrincipal User user){
        if(this.calendarId==-1){
            return "calendar-list";
        }
        loadNotes(this.calendarId, user.getTimezone());
        return "calendar";
    }

    //Wyświetl szczegóły dnia (zwykłe notki)
    @GetMapping("/allNotes")
    public String showNormalNotes(@AuthenticationPrincipal User user){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        loadNotes(this.calendarId, user.getTimezone());
        return "all-notes";
    }

    //Wyświetl szczegóły dnia (notki-zadania)
    @GetMapping("/taskNotes")
    public String showTaskNotes(@AuthenticationPrincipal User user){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        loadNotes(this.calendarId, user.getTimezone());
        return "task-notes";
    }

    //Wyświetl formularz dodający notkę
    @GetMapping("/addNote")
    public String showAddNoteForm(@AuthenticationPrincipal User user, Model model){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        setUpAddNoteViewAttributes(model, user);
        return "add-note";
    }

    //Walidacja formularza dodawania notki
    @PostMapping("/validateNote")
    public String validateAddNote(@Valid @ModelAttribute("addNote") AddNote addNote,
                                  Errors errors,
                                  @AuthenticationPrincipal User user,
                                  Model model,
                                  RedirectAttributes redirectAttributes){
        setUpAddNoteViewAttributes(model, user);
        if(errors.hasErrors()){
            return "add-note";
        }
        redirectAttributes.addFlashAttribute("addNote",addNote);
        return "forward:/notes/add";
    }

    //Wyświetł formularz edytujący notkę
    @GetMapping("/editNote/{id}")
    public String showEditNoteForm(@PathVariable Long id,
                                   Model model,
                                   @AuthenticationPrincipal User user,
                                   @RequestParam(required = false) boolean fromNoteList){
        Note note = noteService.getNote(id);
        if(!fromNoteList){
            if(this.dayNumber==-1){
                return "redirect:/calendar";
            }
        }else{
            this.calendarId = note.getCalendarId();
            this.dayNumber = note.getDateTime().getDayOfMonth();
            model.addAttribute("dayNumber", this.dayNumber);
        }
        User owner = userService.getUser((long) note.getUserId());
        note.setNoteDate(user.getTimezone(), owner.getTimezone());
        note.setNoteTime(user.getTimezone(), owner.getTimezone());
        UpdateNote updateNote = new UpdateNote();
        updateNote.setNoteId(note.getId());
        updateNote.setTitle(note.getTitle());
        updateNote.setContent(note.getContent());
        updateNote.setDate(note.getDate().toString());
        updateNote.setTime(note.getTime());
        updateNote.setIsTask(note.isTask());
        model.addAttribute("updateNote", updateNote);
        return "update-note";
    }

    //Walidacja formularza edycji notki
    @PostMapping("/validateUpdateNote")
    public String validateUpdateNote(@Valid @ModelAttribute("updateNote") UpdateNote updateNote,
                                     Errors errors,
                                     RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            return "update-note";
        }
        redirectAttributes.addFlashAttribute("updateNote",updateNote);
        return "forward:/notes/update";
    }

    //Wyświetl formularz dodający kalendarz
    @GetMapping("/addCalendar")
    public String showAddCalendarForm(){
        return "add-calendar";
    }

    //Wyświetl formularz edytujący kalendarz
    @GetMapping("/editCalendar")
    public String showEditCalendarForm(Model model, @AuthenticationPrincipal User user){
        if(this.calendarId==-1){
            return "calendar-list";
        }
        DbCalendar dbCalendar = calendarService.getCalendar((long) this.calendarId);
        if(dbCalendar.getOwnerId()==user.getId().intValue()){
            model.addAttribute("isOwner",true);
            model.addAttribute("updateCalendar", dbCalendar);
        }else{
            model.addAttribute("isOwner",false);
        }
        return "update-calendar";
    }

    //Wyświetl formularz dodający użytkownikow do kalendarza
    @GetMapping("/editCalendarUser")
    public String showAddUserForm(Model model, @AuthenticationPrincipal User user){
        if(this.calendarId==-1){
            return "redirect:/calendar";
        }
        List<User> calendarUsers = userService.getAllUsersByCalendarId(this.calendarId);
        List<User> invitedUsers = invitationService.getInvitedUsers(this.calendarId);
        List<User> searchResult = (List<User>) model.getAttribute("searchResult");
        if(searchResult!=null){
            model.addAttribute("searchedUsers", searchResult);
        }
        model.addAttribute("calendarUsers", calendarUsers);
        model.addAttribute("invitedUsers", invitedUsers);
        DbCalendar dbCalendar = calendarService.getCalendar((long) this.calendarId);
        if(dbCalendar.getOwnerId()==user.getId().intValue()){
            model.addAttribute("isOwner",true);
        }else{
            model.addAttribute("isOwner",false);
        }
        return "update-calendar-user";
    }

    //Znajdź użytkowników, których nazwa użytkownika zawiera podaną frazę
    @PostMapping("/findUsers")
    public String findUsers(@RequestParam String username, RedirectAttributes redirectAttributes){
        List<User> searchedUsers = userService.getAllUsersContainingUsername(username);
        List<User> calendarUsers = userService.getAllUsersByCalendarId(this.calendarId);
        List<User> invitedUsers = invitationService.getInvitedUsers(this.calendarId);
        searchedUsers.removeIf(invitedUsers::contains);
        searchedUsers.removeIf(calendarUsers::contains);
        redirectAttributes.addFlashAttribute("searchResult", searchedUsers);
        return "redirect:/calendar/editCalendarUser";
    }

    //Zaproś użytkownika do kalendarza
    @PostMapping("/inviteUser")
    public String inviteUser(@RequestParam int id, @AuthenticationPrincipal User user) throws MessagingException {
        Invitation invitation = new Invitation();
        invitation.setCalendarId(this.calendarId);
        invitation.setReceiverId(id);
        invitation.setSenderId(user.getId().intValue());
        invitationService.addInvitation(invitation);
        return "redirect:/calendar/editCalendarUser";
    }

    //Aktualizuj kalendarz
    @PostMapping("/update")
    public String updateCalendar(@ModelAttribute("updateCalendar") DbCalendar dbCalendar){
        calendarService.updateCalendar(dbCalendar);
        return "redirect:/calendar/editCalendar";
    }

    //Wyświetl listę kalendarzy
    @GetMapping("/list")
    public String showCalendarList(){
        return "calendar-list";
    }

    //Nastepny miesiąc
    @GetMapping("/next")
    public String nextMonth(Model model){
        this.calendar.incrementMonth();
        //model.addAttribute("calendar", this.calendar);
        return "redirect:/calendar";
    }

    //Poprzedni miesiąc
    @GetMapping("/previous")
    public String previousMonth(Model model){
        this.calendar.decrementMonth();
        //model.addAttribute("calendar", this.calendar);
        return "redirect:/calendar";
    }

    //Dodaj kalendarz
    @PostMapping("/add")
    public String addCalendar(@ModelAttribute("dbCalendar") DbCalendar dbCalendar){
        calendarService.addCalendar(dbCalendar);
        return "redirect:/calendar/list";
    }

    //Wczytaj notki użytkownika i zapisz je do kalendarza
    public void loadNotes(int id, String timezone){
        List<Note> notes = noteService.loadNotesByCalendarId(id, timezone);
        this.calendar.setNotes(notes);
    }

    //Wczytaj kalendarze użytkownika i zapisz jako atrybut modelu
    public void loadCalendars(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

    //Ustaw atrybuty isOwner i listę użytkowników kalendarza
    public void setUpAddNoteViewAttributes(Model model, User user){
        DbCalendar dbCalendar = calendarService.getCalendar((long) this.calendarId);
        if(dbCalendar.getOwnerId()==user.getId().intValue()){
            model.addAttribute("isOwner",true);
        }else{
            model.addAttribute("isOwner",false);
        }
        List<User> calendarUsers = userService.getAllUsersByCalendarId(this.calendarId);
        model.addAttribute("calendarUsers",calendarUsers);
    }

}
