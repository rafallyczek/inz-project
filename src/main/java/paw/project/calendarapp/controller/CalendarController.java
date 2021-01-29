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

    //------------------------------------------------------------------------------------
    //Metody poprawione

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void setModelAttributes(Model model){
        model.addAttribute("calendar", this.calendar);
    }

    //Wyświetl listę kalendarzy
    @GetMapping("/list")
    public String showCalendarList(Model model,
                                   @AuthenticationPrincipal User user){
        setUpCalendarListViewAttributes(model, user);
        return "calendar-list";
    }

    //Wyświetl kalendarz o danym id
    @GetMapping("/{id}")
    public String showCalendarNEW(@PathVariable int id,
                                  @AuthenticationPrincipal User user,
                                  Model model){
        loadNotes(id, user.getTimezone());
        model.addAttribute("calendarId",id);
        return "calendar";
    }

    //Wyświetl podgląd dnia
    @GetMapping("/{calendarId}/day/{day}")
    public String showDay(@PathVariable int calendarId,
                          @PathVariable int day,
                          @AuthenticationPrincipal User user,
                          Model model){
        loadNotes(calendarId, user.getTimezone());
        model.addAttribute("calendarId",calendarId);
        model.addAttribute("dayNumber",day);
        return "all-notes";
    }

    //Wyświetl podgląd zadań dnia
    @GetMapping("/{calendarId}/day/{day}/tasks")
    public String showDayTasks(@PathVariable int calendarId,
                               @PathVariable int day,
                               @AuthenticationPrincipal User user,
                               Model model){
        loadNotes(calendarId, user.getTimezone());
        model.addAttribute("calendarId",calendarId);
        model.addAttribute("dayNumber",day);
        return "task-notes";
    }

    //Wyświetl formularz dodający notkę
    @GetMapping("/{calendarId}/day/{day}/addNote")
    public String showAddNoteFormNEW(@PathVariable int calendarId,
                                     @PathVariable int day,
                                     @AuthenticationPrincipal User user,
                                     Model model){
        setUpAddNoteViewAttributes(model, user, (long) calendarId, day);
        setUpIsOwnerAndCalendarUsers(model, user, (long) calendarId);
        model.addAttribute("calendarId",calendarId);
        model.addAttribute("dayNumber",day);
        return "add-note";
    }

    //Wyświetl formularz edutujący notkę
    @GetMapping("/{calendarId}/day/{day}/updateNote/{noteId}")
    public String showUpdateNoteForm(@PathVariable int calendarId,
                                     @PathVariable int day,
                                     @PathVariable int noteId,
                                     @AuthenticationPrincipal User user,
                                     Model model){
        setUpUpdateNoteViewAttributes(model, user, (long) noteId);
        setUpIsOwnerAndCalendarUsers(model, user, (long) calendarId);
        model.addAttribute("calendarId",calendarId);
        model.addAttribute("dayNumber",day);
        return "update-note";
    }

    //Walidacja formularza dodawania notki
    @PostMapping("/{calendarId}/day/{day}/addNote/validate")
    public String validateAddNote(@Valid @ModelAttribute("addNote") AddNote addNote,
                                  Errors errors,
                                  @PathVariable int calendarId,
                                  @PathVariable int day,
                                  @AuthenticationPrincipal User user,
                                  Model model,
                                  RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("dayNumber",day);
            setUpIsOwnerAndCalendarUsers(model, user, (long) calendarId);
            return "add-note";
        }
        redirectAttributes.addFlashAttribute("addNote",addNote);
        return "forward:/notes/add";
    }

    //Walidacja formularza edycji notki
    @PostMapping("/{calendarId}/day/{day}/updateNote/validate")
    public String validateUpdateNote(@Valid @ModelAttribute("updateNote") UpdateNote updateNote,
                                     Errors errors,
                                     @PathVariable int calendarId,
                                     @PathVariable int day,
                                     @AuthenticationPrincipal User user,
                                     Model model,
                                     RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("dayNumber",day);
            setUpIsOwnerAndCalendarUsers(model, user, (long) calendarId);
            return "update-note";
        }
        redirectAttributes.addFlashAttribute("updateNote",updateNote);
        return "forward:/notes/update";
    }

    //Wyświetl formularz dodający kalendarz
    @GetMapping("/addCalendar")
    public String showAddCalendarForm(@AuthenticationPrincipal User user,
                                      Model model){
        DbCalendar dbCalendar = new DbCalendar();
        dbCalendar.setOwnerId(user.getId().intValue());
        model.addAttribute("dbCalendar", dbCalendar);
        return "add-calendar";
    }

    //Dodaj kalendarz
    @PostMapping("/add")
    public String addCalendar(@Valid @ModelAttribute("dbCalendar") DbCalendar dbCalendar,
                              Errors errors){
        if(errors.hasErrors()){
            return "add-calendar";
        }
        calendarService.addCalendar(dbCalendar);
        return "redirect:/calendar/list";
    }

    //Nastepny miesiąc
    @GetMapping("/{id}/next")
    public String nextMonth(@PathVariable int id){
        this.calendar.incrementMonth();
        return "redirect:/calendar/"+id;
    }

    //Poprzedni miesiąc
    @GetMapping("/{id}/previous")
    public String previousMonth(@PathVariable int id){
        this.calendar.decrementMonth();
        return "redirect:/calendar/"+id;
    }

    //------------------------------------------------------------------------------------
    //Metody Do zmiany

    //DO ZMIANY
    //Ustaw numer dnia i id kalendarza
    @GetMapping("/setDayNumberAndCalendarId")
    public String setDayNumberAndCalendarId(@ModelAttribute("calendarId") Integer calendarId, @ModelAttribute("dayNum") int dayNum){
        this.calendarId = calendarId;
        this.dayNumber = dayNum;
        return "redirect:/calendar/allNotes";
    }

    //DO ZMIANY
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

    //DO ZMIANY
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

    //DO ZMIANY
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

    //DO ZMIANY
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

    //DO ZMIANY
    //Aktualizuj kalendarz
    @PostMapping("/update")
    public String updateCalendar(@ModelAttribute("updateCalendar") DbCalendar dbCalendar){
        calendarService.updateCalendar(dbCalendar);
        return "redirect:/calendar/editCalendar";
    }

    //------------------------------------------------------------------------------------
    //Metody ustawiające atrybuty modelu

    //Wczytaj notki użytkownika i zapisz je do kalendarza
    public void loadNotes(int id, String timezone){
        List<Note> notes = noteService.loadNotesByCalendarId(id, timezone);
        this.calendar.setNotes(notes);
    }

    //Ustaw atrybuty modelu używane przez widok calendar-list
    public void setUpCalendarListViewAttributes(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

    //Ustaw atrybuty modelu używane przez widok add-note
    public void setUpAddNoteViewAttributes(Model model, User user, Long calendarId, int dayNumber){
        AddNote addNote = new AddNote();
        String day = dayNumber<10 ? "0"+dayNumber : String.valueOf(dayNumber);
        String month = this.calendar.getMonth()<10 ? "0"+this.calendar.getMonth() : String.valueOf(this.calendar.getMonth());
        addNote.setCalendarId(calendarId.intValue());
        addNote.setUserId(user.getId().intValue());
        addNote.setDate(this.calendar.getYear()+"-"+month+"-"+day);
        addNote.setTime("00:00");
        model.addAttribute("addNote", addNote);
    }

    //Ustaw atrybuty modelu używane przez widok update-note
    public void setUpUpdateNoteViewAttributes(Model model, User user, Long noteId){
        Note note = noteService.getNote(noteId);
        User owner = userService.getUser((long) note.getUserId());
        note.setNoteDate(user.getTimezone(), owner.getTimezone());
        note.setNoteTime(user.getTimezone(), owner.getTimezone());
        UpdateNote updateNote = new UpdateNote();
        updateNote.setNoteId(note.getId());
        updateNote.setTitle(note.getTitle());
        updateNote.setContent(note.getContent());
        updateNote.setUserId(note.getUserId());
        updateNote.setDate(note.getDate().toString());
        updateNote.setTime(note.getTime());
        updateNote.setIsTask(note.isTask());
        model.addAttribute("updateNote", updateNote);
    }

    //Ustaw atrybuty isOwner oraz calendarUsers
    public void setUpIsOwnerAndCalendarUsers(Model model, User user, Long calendarId){
        DbCalendar dbCalendar = calendarService.getCalendar(calendarId);
        if(dbCalendar.getOwnerId()==user.getId().intValue()){
            model.addAttribute("isOwner",true);
        }else{
            model.addAttribute("isOwner",false);
        }
        List<User> calendarUsers = userService.getAllUsersByCalendarId(calendarId.intValue());
        model.addAttribute("calendarUsers",calendarUsers);
    }

    //------------------------------------------------------------------------------------
    //Metody nieużywane

    //DEPRECATED
    //Wyświetl kalendarz
    @GetMapping
    public String showCalendar(@AuthenticationPrincipal User user){
        if(this.calendarId==-1){
            return "calendar-list";
        }
        loadNotes(this.calendarId, user.getTimezone());
        return "calendar";
    }

    //DEPRECATED
    //Ustaw numer dnia
    @PostMapping("/setDayNumber")
    public String setDayNumber(@RequestParam int dayNumber){
        this.dayNumber = dayNumber;
        return "redirect:/calendar/allNotes";
    }

    //DEPRECATED
    //Ustaw id kalendarza
    @PostMapping("/setCalendarId")
    public String setCalendarId(@RequestParam int calendarId){
        this.calendarId = calendarId;
        return "redirect:/calendar";
    }

    //DEPRECATED
    //Wyświetl szczegóły dnia (zwykłe notki)
    @GetMapping("/allNotes")
    public String showNormalNotes(@AuthenticationPrincipal User user){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        loadNotes(this.calendarId, user.getTimezone());
        return "all-notes";
    }

    //DEPRECATED
    //Wyświetl szczegóły dnia (notki-zadania)
    @GetMapping("/taskNotes")
    public String showTaskNotes(@AuthenticationPrincipal User user){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        loadNotes(this.calendarId, user.getTimezone());
        return "task-notes";
    }

    //DEPRECATED
    //Wyświetl formularz dodający notkę
    @GetMapping("/addNote")
    public String showAddNoteForm(@AuthenticationPrincipal User user, Model model){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        //setUpAddNoteViewAttributes(model, user);
        return "add-note";
    }

    //DEPRECATED
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

}
