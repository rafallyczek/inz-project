package kanbancalendar.project.app.controller;

import kanbancalendar.project.app.model.*;
import kanbancalendar.project.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import kanbancalendar.project.app.TO.AddNote;
import kanbancalendar.project.app.TO.UpdateNote;
import kanbancalendar.project.app.component.Calendar;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private final Calendar calendar;
    private final NoteService noteService;
    private final CalendarService calendarService;
    private final UserService userService;
    private final InvitationService invitationService;
    private final CalendarRoleService calendarRoleService;

    //Wstrzykiwanie zależności
    @Autowired
    public CalendarController(Calendar calendar,
                              NoteService noteService,
                              CalendarService calendarService,
                              UserService userService,
                              InvitationService invitationService,
                              CalendarRoleService calendarRoleService){
        this.calendar = calendar;
        this.noteService = noteService;
        this.calendarService = calendarService;
        this.userService = userService;
        this.invitationService = invitationService;
        this.calendarRoleService = calendarRoleService;
    }

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
    @GetMapping("/id/{id}")
    public String showCalendar(@PathVariable int id,
                                  @AuthenticationPrincipal User user,
                                  Model model){
        loadNotes(id, user.getTimezone());
        model.addAttribute("calendarId",id);
        model.addAttribute("month",calendar.getCurrentDate().getMonthValue());
        model.addAttribute("year",calendar.getCurrentDate().getYear());
        return "calendar";
    }

    //Nastepny miesiąc
    @GetMapping("/id/{id}/next")
    public String nextMonth(@PathVariable int id){
        this.calendar.incrementMonth();
        return "redirect:/calendar/id/"+id;
    }

    //Poprzedni miesiąc
    @GetMapping("/id/{id}/previous")
    public String previousMonth(@PathVariable int id){
        this.calendar.decrementMonth();
        return "redirect:/calendar/id/"+id;
    }

    //Wyświetl podgląd dnia
    @GetMapping("/id/{id}/date/{year}/{month}/{day}")
    public String showDay(@PathVariable int id,
                              @PathVariable int year,
                              @PathVariable int month,
                              @PathVariable int day,
                              @AuthenticationPrincipal User user,
                              Model model){
        loadNotes(id, user.getTimezone());
        this.calendar.setMonth(month);
        this.calendar.setYear(year);
        model.addAttribute("calendarId",id);
        model.addAttribute("dayNumber",day);
        model.addAttribute("month",month);
        model.addAttribute("year",year);
        return "day";
    }

    //Wyświetl podgląd zadań dnia
    @GetMapping("/id/{id}/date/{year}/{month}/{day}/tasks")
    public String showDayTasks(@PathVariable int id,
                              @PathVariable int year,
                              @PathVariable int month,
                              @PathVariable int day,
                              @AuthenticationPrincipal User user,
                              Model model){
        loadNotes(id, user.getTimezone());
        this.calendar.setMonth(month);
        this.calendar.setYear(year);
        model.addAttribute("calendarId",id);
        model.addAttribute("dayNumber",day);
        model.addAttribute("month",month);
        model.addAttribute("year",year);
        if(model.containsAttribute("ajax")){
            return "day-tasks :: tasks";
        }
        return "day-tasks";
    }

    //Wyświetl formularz dodający notkę
    @GetMapping("/id/{calendarId}/date/{year}/{month}/{day}/addNote")
    public String showAddNoteForm(@PathVariable int calendarId,
                                      @PathVariable int year,
                                      @PathVariable int month,
                                      @PathVariable int day,
                                      @AuthenticationPrincipal User user,
                                      Model model){
        setUpAddNoteViewAttributes(model, user, (long) calendarId, day);
        setUpPrivilege(model, user, (long) calendarId);
        List<User> calendarUsers = userService.getAllUsersByCalendarId(calendarId);
        model.addAttribute("calendarUsers",calendarUsers);
        model.addAttribute("calendarId",calendarId);
        model.addAttribute("dayNumber",day);
        model.addAttribute("month",month);
        model.addAttribute("year",year);
        return "add-note";
    }

    //Walidacja formularza dodawania notki
    @PostMapping("/id/{calendarId}/date/{year}/{month}/{day}/addNote/validate")
    public String validateAddNote(@Valid @ModelAttribute("addNote") AddNote addNote,
                                      Errors errors,
                                      @PathVariable int calendarId,
                                      @PathVariable int year,
                                      @PathVariable int month,
                                      @PathVariable int day,
                                      @AuthenticationPrincipal User user,
                                      Model model,
                                      RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("dayNumber",day);
            model.addAttribute("month",month);
            model.addAttribute("year",year);
            setUpPrivilege(model, user, (long) calendarId);
            List<User> calendarUsers = userService.getAllUsersByCalendarId(calendarId);
            model.addAttribute("calendarUsers",calendarUsers);
            return "add-note";
        }
        redirectAttributes.addFlashAttribute("addNote",addNote);
        return "forward:/notes/add";
    }

    //Wyświetl formularz edutujący notkę
    @GetMapping("/id/{calendarId}/date/{year}/{month}/{day}/updateNote/{noteId}")
    public String showUpdateNoteForm(@PathVariable int calendarId,
                                         @PathVariable int year,
                                         @PathVariable int month,
                                         @PathVariable int day,
                                         @PathVariable int noteId,
                                         @AuthenticationPrincipal User user,
                                         Model model){
        setUpUpdateNoteViewAttributes(model, user, (long) noteId);
        setUpPrivilege(model, user, (long) calendarId);
        List<User> calendarUsers = userService.getAllUsersByCalendarId(calendarId);
        model.addAttribute("calendarUsers",calendarUsers);
        model.addAttribute("calendarId",calendarId);
        model.addAttribute("dayNumber",day);
        model.addAttribute("month",month);
        model.addAttribute("year",year);
        return "update-note";
    }

    //Walidacja formularza edycji notki
    @PostMapping("/id/{calendarId}/date/{year}/{month}/{day}/updateNote/validate")
    public String validateUpdateNote(@Valid @ModelAttribute("updateNote") UpdateNote updateNote,
                                         Errors errors,
                                         @PathVariable int calendarId,
                                         @PathVariable int year,
                                         @PathVariable int month,
                                         @PathVariable int day,
                                         @AuthenticationPrincipal User user,
                                         Model model,
                                         RedirectAttributes redirectAttributes){
        if(errors.hasErrors()){
            model.addAttribute("dayNumber",day);
            model.addAttribute("month",month);
            model.addAttribute("year",year);
            setUpPrivilege(model, user, (long) calendarId);
            List<User> calendarUsers = userService.getAllUsersByCalendarId(calendarId);
            model.addAttribute("calendarUsers",calendarUsers);
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

    //Wyświetl formularz edytujący kalendarz
    @GetMapping("/id/{id}/updateCalendar")
    public String showUpdateCalendarForm(@PathVariable int id,
                                          @AuthenticationPrincipal User user,
                                          Model model){
        DbCalendar dbCalendar = calendarService.getCalendar((long) id);
        model.addAttribute("calendarId", id);
        model.addAttribute("updateCalendar", dbCalendar);
        setUpPrivilege(model, user, (long) id);
        return "update-calendar";
    }

    //Aktualizuj kalendarz
    @PostMapping("/id/{id}/update")
    public String updateCalendar(@Valid @ModelAttribute("updateCalendar") DbCalendar dbCalendar,
                                 Errors errors,
                                 @PathVariable int id,
                                 @AuthenticationPrincipal User user,
                                 Model model){
        if(errors.hasErrors()){
            model.addAttribute("calendarId", id);
            setUpPrivilege(model, user, (long) id);
            return "update-calendar";
        }
        calendarService.updateCalendar(dbCalendar);
        return "redirect:/calendar/id/"+id;
    }

    //Usuń kalendarz
    @RequestMapping("/id/{id}/delete")
    public String deleteCalendar(@PathVariable Long id){
        calendarService.deleteCalendar(id);
        return "redirect:/calendar/list";
    }

    //Wyświetl widok użytkowników kalendarza
    @GetMapping("/id/{id}/calendarUsers")
    public String showCalendarUsersForm(@PathVariable int id,
                                        @AuthenticationPrincipal User user,
                                        Model model,
                                        @ModelAttribute("searchResult") ArrayList<User> searchResult){
        if(searchResult!=null){
            model.addAttribute("searchedUsers", searchResult);
        }
        model.addAttribute("calendarId",id);
        setUpCalendarUsersViewAttributes(model, (long) id);
        setUpPrivilege(model, user, (long) id);
        return "calendar-users";
    }

    //Znajdź użytkowników, których nazwa użytkownika zawiera podaną frazę
    @PostMapping("/id/{id}/calendarUsers/findUsers")
    public String findUsers(@PathVariable int id,
                            @RequestParam String username,
                            RedirectAttributes redirectAttributes){
        List<User> searchedUsers = userService.getAllUsersContainingUsername(username);
        List<User> calendarUsers = userService.getAllUsersByCalendarId(id);
        List<User> invitedUsers = invitationService.getInvitedUsers(id);
        searchedUsers.removeIf(invitedUsers::contains);
        searchedUsers.removeIf(calendarUsers::contains);
        redirectAttributes.addFlashAttribute("searchResult", searchedUsers);
        return "redirect:/calendar/id/"+id+"/calendarUsers";
    }

    //Zaproś użytkownika do kalendarza
    @PostMapping("/id/{id}/calendarUsers/inviteUser")
    public String inviteUser(@PathVariable int id,
                             @RequestParam int userId,
                             @AuthenticationPrincipal User user) throws MessagingException {
        Invitation invitation = new Invitation();
        invitation.setCalendarId(id);
        invitation.setReceiverId(userId);
        invitation.setSenderId(user.getId().intValue());
        invitationService.addInvitation(invitation);
        return "redirect:/calendar/id/"+id+"/calendarUsers";
    }

    //Zaktualizuj rolę
    @PostMapping("/id/{id}/calendarUsers/changeRole/{roleId}")
    public String changeRole(@PathVariable int id,
                             @PathVariable int roleId,
                             @RequestParam String role,
                             @AuthenticationPrincipal User user){
        CalendarRole currentUserRole = calendarRoleService.getUserCalendarRole(user.getId(),(long) id);
        CalendarRole calendarRole = calendarRoleService.getCalendarRole((long) roleId);
        //Jeśli ADMIN chce zmienić rolę OWNERa nie zezwól
        if(currentUserRole.getName().equals("ADMIN")&&calendarRole.getName().equals("OWNER")){
            return "redirect:/calendar/id/"+id+"/calendarUsers";
        //Jeśli ktoś chce sam sobie przyznać rolę OWNER nie zezwól
        }else if(currentUserRole.getId().equals(calendarRole.getId())&&role.equals("OWNER")){
            return "redirect:/calendar/id/"+id+"/calendarUsers";
        //Jeśli jedyny właściciel chce zmienić swoją rolę nie zezwól
        }else if(currentUserRole.getName().equals("OWNER")&&calendarRole.getName().equals("OWNER")&&!multipleOwners((long) id)){
            return "redirect:/calendar/id/"+id+"/calendarUsers";
        }
        calendarRole.setName(role);
        calendarRoleService.updateRole(calendarRole);
        return "redirect:/calendar/id/"+id+"/calendarUsers";
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
        for(DbCalendar dbCalendar : dbCalendars){
            User owner = userService.getUser((long) dbCalendar.getOwnerId());
            dbCalendar.setOwnerName(owner.getUsername());
        }
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
    public void setUpPrivilege(Model model, User user, Long calendarId){
        CalendarRole calendarRole = calendarRoleService.getUserCalendarRole(user.getId(),calendarId);
        model.addAttribute("calendarRole", calendarRole.getName());
    }

    //Ustaw atrybuty modelu używane przez widok calendar-users
    public void setUpCalendarUsersViewAttributes(Model model, Long calendarId){
        List<User> calendarUsers = userService.getAllUsersByCalendarId(calendarId.intValue());
        List<CalendarRole> userRoles = calendarRoleService.getAllRolesByCalendarId(calendarId);
        List<User> invitedUsers = invitationService.getInvitedUsers(calendarId.intValue());
        model.addAttribute("calendarUsers", calendarUsers);
        model.addAttribute("userRoles", userRoles);
        model.addAttribute("invitedUsers", invitedUsers);
    }

    //------------------------------------------------------------------------------------
    //Inne metody

    //Sprawdź czy kalendarz ma więcej niż jednego właściciela
    public boolean multipleOwners(Long calendarId){
        List<CalendarRole> calendarRoles = calendarRoleService.getAllRolesByCalendarId(calendarId);
        int count = 0;
        for(CalendarRole calendarRole : calendarRoles){
            if(calendarRole.getName().equals("OWNER")){
                count++;
            }
            if(count>1){
                return true;
            }
        }
        return false;
    }

}
