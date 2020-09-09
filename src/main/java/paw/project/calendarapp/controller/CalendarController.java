package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import paw.project.calendarapp.model.Calendar;
import paw.project.calendarapp.model.DbCalendar;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.service.CalendarService;
import paw.project.calendarapp.service.NoteService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private Calendar calendar;
    private NoteService noteService;
    private CalendarService calendarService;

    //Wstrzykiwanie obiektu Calendar
    @Autowired
    public CalendarController(Calendar calendar, NoteService noteService, CalendarService calendarService){
        this.calendar = calendar;
        this.noteService = noteService;
        this.calendarService = calendarService;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void setModelAttributes(Model model, @AuthenticationPrincipal User user){
        loadNotes(user);
        loadCalendars(model, user);
        Note note = new Note();
        DbCalendar dbCalendar = new DbCalendar();
        note.setUserId(user.getId().intValue());
        model.addAttribute("calendar", this.calendar);
        model.addAttribute("note", note);
        model.addAttribute("dbCalendar", dbCalendar);
    }

    //Wyświetl kalendarz
    @GetMapping
    public String showCalendar(){
        return "calendar";
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
        model.addAttribute("calendar", this.calendar);
        return "redirect:/calendar";
    }

    //Poprzedni miesiąc
    @GetMapping("/previous")
    public String previousMonth(Model model){
        this.calendar.decrementMonth();
        model.addAttribute("calendar", this.calendar);
        return "redirect:/calendar";
    }

    //Dodaj kalendarz
    @PostMapping("/add")
    public String addCalendar(@ModelAttribute("dbCalendar") DbCalendar dbCalendar){
        calendarService.addCalendar(dbCalendar);
        return "redirect:/calendar/list";
    }

    //Wczytaj notki użytkownika i zapisz je do kalendarza
    public void loadNotes(User user){
        List<Note> notes = noteService.loadNotesByUserId(user.getId().intValue());
        this.calendar.setNotes(notes);
    }

    //Wczytaj kalendarze użytkownika i zapisz jako atrybut modelu
    public void loadCalendars(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

}
