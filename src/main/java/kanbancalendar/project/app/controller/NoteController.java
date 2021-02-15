package kanbancalendar.project.app.controller;

import com.itextpdf.text.DocumentException;
import kanbancalendar.project.app.model.DbCalendar;
import kanbancalendar.project.app.model.Note;
import kanbancalendar.project.app.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import kanbancalendar.project.app.TO.AddNote;
import kanbancalendar.project.app.TO.UpdateNote;
import kanbancalendar.project.app.model.User;
import kanbancalendar.project.app.file.Csv;
import kanbancalendar.project.app.file.Pdf;
import kanbancalendar.project.app.service.CalendarService;
import kanbancalendar.project.app.service.RequestService;


import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final CalendarService calendarService;
    private final RequestService requestService;
    private List<Note> notes;

    //Wstrzykiwanie serwisu
    @Autowired
    public NoteController(NoteService noteService,
                          CalendarService calendarService,
                          RequestService requestService){
        this.noteService = noteService;
        this.calendarService = calendarService;
        this.requestService = requestService;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void notes(Model model,
                      @AuthenticationPrincipal User user){
        if(user!=null){
            loadCalendars(model, user);
            if(notes==null){
                notes = getAllNotes(user);
            }
        }
    }

    //Wyświetl listę notek
    @GetMapping("/list")
    public String showNoteList(Model model,
                               @AuthenticationPrincipal User user){
        model.addAttribute("notes", notes);
        return "note-list";
    }

    //Pobierz pdf
    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> pdf(@AuthenticationPrincipal User user) throws DocumentException, IOException {
        Pdf pdf = new Pdf(notes,user.getTimezone());
        ByteArrayInputStream in = pdf.buildPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=notes.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(in));
    }

    //Pobierz csv
    @GetMapping("/csv")
    public ResponseEntity<InputStreamResource> csv() throws IOException {
        Csv scv = new Csv(notes);
        ByteArrayInputStream in = scv.buildCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=notes.csv");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("text/csv")).body(new InputStreamResource(in));
    }

    //Dodaj notkę
    @PostMapping("/add")
    public String addNote(@ModelAttribute("addNote") AddNote addNote,
                          @AuthenticationPrincipal User user){
        if(!addNote.getIsTask()){
            addNote.setUserId(user.getId().intValue());
        }
        noteService.addNote(addNote);
        return "redirect:/calendar/id/"+addNote.getCalendarId()+"/date/"+addNote.getYear()+"/"+addNote.getMonth()+"/"+addNote.getDay();
    }

    //Aktualizuj notkę
    @PostMapping("/update")
    public String updateNote(@ModelAttribute("updateNote") UpdateNote updateNote,
                             @AuthenticationPrincipal User user){
        if(!updateNote.getIsTask()){
            updateNote.setUserId(user.getId().intValue());
        }
        Note note = noteService.updateNote(updateNote);
        return "redirect:/calendar/id/"+note.getCalendarId()+"/date/"+updateNote.getYear()+"/"+updateNote.getMonth()+"/"+updateNote.getDay();
    }

    //Usuń notkę
    @RequestMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id, HttpServletRequest request){
        noteService.deleteNote(id);
        return "redirect:"+requestService.determineRequestAddress(request);
    }

    //Zmień status na do zrobienia
    @PostMapping("/statusToDo/{id}")
    public String statusToDo(@PathVariable Long id,
                             RedirectAttributes redirectAttributes) throws MessagingException {
        Note note = noteService.changeStatus(id, "to-do");
        redirectAttributes.addFlashAttribute("ajax",true);
        return "redirect:/calendar/id/"+note.getCalendarId()+"/date/"+note.getYear()+"/"+note.getMonth()+"/"+note.getDay()+"/tasks";
    }

    //Zmień status na w trakcie
    @PostMapping("/statusInProgress/{id}")
    public String statusInProgress(@PathVariable Long id,
                                   RedirectAttributes redirectAttributes) throws MessagingException {
        Note note = noteService.changeStatus(id, "in-progress");
        redirectAttributes.addFlashAttribute("ajax",true);
        return "redirect:/calendar/id/"+note.getCalendarId()+"/date/"+note.getYear()+"/"+note.getMonth()+"/"+note.getDay()+"/tasks";
    }

    //Zmień status na zakończone
    @PostMapping("/statusFinished/{id}")
    public String statusFinished(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) throws MessagingException {
        Note note = noteService.changeStatus(id, "finished");
        redirectAttributes.addFlashAttribute("ajax",true);
        return "redirect:/calendar/id/"+note.getCalendarId()+"/date/"+note.getYear()+"/"+note.getMonth()+"/"+note.getDay()+"/tasks";
    }

    //Wczytaj notki danego kalendarza
    @PostMapping("/getCalendarNotes")
    public String getCalendarNotes(@RequestParam int calendarId,
                                   @AuthenticationPrincipal User user){
        if(calendarId==0){
            notes = getAllNotes(user);
        }else{
            notes = noteService.loadNotesByCalendarId(calendarId, user.getTimezone());
        }
        return "redirect:/notes/list";
    }

    //Wczytaj notki użytkownika
    public List<Note> getAllNotes(User user){
        return noteService.loadNotesByUserId(user.getId().intValue(), user.getTimezone());
    }

    //Wczytaj kalendarze użytkownika i zapisz jako atrybut modelu
    public void loadCalendars(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

}
