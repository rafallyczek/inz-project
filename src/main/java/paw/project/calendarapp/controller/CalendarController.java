package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.TO.AddNote;
import paw.project.calendarapp.TO.UpdateNote;
import paw.project.calendarapp.model.Calendar;
import paw.project.calendarapp.model.DbCalendar;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.service.CalendarService;
import paw.project.calendarapp.service.NoteService;

import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private Calendar calendar;
    private NoteService noteService;
    private CalendarService calendarService;
    private int calendarId;
    private int dayNumber;

    //Wstrzykiwanie obiektu Calendar
    @Autowired
    public CalendarController(Calendar calendar, NoteService noteService, CalendarService calendarService){
        this.calendar = calendar;
        this.noteService = noteService;
        this.calendarService = calendarService;
        this.calendarId = -1;
        this.dayNumber = -1;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void setModelAttributes(Model model, @AuthenticationPrincipal User user){
        loadCalendars(model, user);

        DbCalendar dbCalendar = new DbCalendar();
        AddNote addNote = new AddNote();
        UpdateNote updateNote = new UpdateNote();

        dbCalendar.setOwnerId(user.getId().intValue());
        addNote.setCalendarId(this.calendarId);
        addNote.setUserId(user.getId().intValue());

        model.addAttribute("addNote", addNote);
        model.addAttribute("updateNote", updateNote);
        model.addAttribute("calendar", this.calendar);
        model.addAttribute("dbCalendar", dbCalendar);
        model.addAttribute("dayNumber", this.dayNumber);
    }

    //Wyświetl kalendarz
    @GetMapping
    public String showCalendar(){
        if(this.calendarId==-1){
            return "calendar-list";
        }
        loadNotes(this.calendarId);
        return "calendar";
    }

    //Ustaw id kalendarza i załaduj notki
    @PostMapping("/setCalendarId")
    public String setCalendarId(@RequestParam int calendarId){
        this.calendarId = calendarId;
        return "redirect:/calendar";
    }

    //Wyświetl szczegóły dnia
    @GetMapping("/day")
    public String showDay(){
        if(this.dayNumber==-1){
            return "calendar";
        }
        return "day";
    }

    //Ustaw numer dnia
    @PostMapping("/setDayNumber")
    public String setDayNumber(@RequestParam int dayNumber){
        this.dayNumber = dayNumber;
        return "redirect:/calendar/day";
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
    public void loadNotes(int id){
        List<Note> notes = noteService.loadNotesByCalendarId(id);
        this.calendar.setNotes(notes);
    }

    //Wczytaj kalendarze użytkownika i zapisz jako atrybut modelu
    public void loadCalendars(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

}
