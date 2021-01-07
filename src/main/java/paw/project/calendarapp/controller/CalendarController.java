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
import paw.project.calendarapp.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private Calendar calendar;
    private NoteService noteService;
    private CalendarService calendarService;
    private UserService userService;
    private int calendarId;
    private int dayNumber;

    //Wstrzykiwanie obiektu Calendar
    @Autowired
    public CalendarController(Calendar calendar, NoteService noteService, CalendarService calendarService, UserService userService){
        this.calendar = calendar;
        this.noteService = noteService;
        this.calendarService = calendarService;
        this.userService = userService;
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

            model.addAttribute("addNote", addNote);
            model.addAttribute("calendar", this.calendar);
            model.addAttribute("dbCalendar", dbCalendar);
            model.addAttribute("dayNumber", this.dayNumber);
        }
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

    //Ustaw id kalendarza i załaduj notki
    @PostMapping("/setCalendarId")
    public String setCalendarId(@RequestParam int calendarId){
        this.calendarId = calendarId;
        return "redirect:/calendar";
    }

    //Wyświetl szczegóły dnia (zwykłe notki)
    @GetMapping("/normalNotes")
    public String showNormalNotes(){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        return "normal-notes";
    }

    //Wyświetl szczegóły dnia (notki-zadania)
    @GetMapping("/taskNotes")
    public String showTaskNotes(){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        return "task-notes";
    }

    //Ustaw numer dnia
    @PostMapping("/setDayNumber")
    public String setDayNumber(@RequestParam int dayNumber){
        this.dayNumber = dayNumber;
        return "redirect:/calendar/normalNotes";
    }

    //Wyświetl formularz dodający notkę
    @GetMapping("/addNote")
    public String showAddNoteForm(){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        return "add-note";
    }

    //Wyświetł formularz edytujący notkę
    @GetMapping("/editNote/{id}")
    public String showEditNoteForm(@PathVariable Long id, Model model, @AuthenticationPrincipal User user){
        if(this.dayNumber==-1){
            return "redirect:/calendar";
        }
        Note note = noteService.getNote(id);
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

    //Wyświetl formularz dodający kalendarz
    @GetMapping("/addCalendar")
    public String showAddCalendarForm(){
        return "add-calendar";
    }

    //Wyświetl formularz edytujący kalendarz
    @GetMapping("/editCalendar")
    public String showEditCalendarForm(Model model){
        if(this.calendarId==-1){
            return "calendar-list";
        }
        DbCalendar dbCalendar = calendarService.getCalendar((long) this.calendarId);
        model.addAttribute("updateCalendar", dbCalendar);
        return "update-calendar";
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
    public void loadNotes(int id, String timezone){
        List<Note> notes = noteService.loadNotesByCalendarId(id, timezone);
        this.calendar.setNotes(notes);
    }

    //Wczytaj kalendarze użytkownika i zapisz jako atrybut modelu
    public void loadCalendars(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

}
