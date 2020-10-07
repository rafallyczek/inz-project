package paw.project.calendarapp.controller;

import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.TO.AddNote;
import paw.project.calendarapp.TO.UpdateNote;
import paw.project.calendarapp.model.DbCalendar;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.pdf.Pdf;
import paw.project.calendarapp.service.CalendarService;
import paw.project.calendarapp.service.NoteService;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private NoteService noteService;
    private CalendarService calendarService;
    private List<Note> notes;

    //Wstrzykiwanie serwisu
    @Autowired
    public NoteController(NoteService noteService, CalendarService calendarService){
        this.noteService = noteService;
        this.calendarService = calendarService;
        this.notes = new ArrayList<>();
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void notes(Model model, @AuthenticationPrincipal User user){
        loadCalendars(model, user);
        if(notes.isEmpty()){
            notes = getAllNotes(user);
        }
        model.addAttribute("notes", notes);
    }

    //Wyświetl listę notek
    @GetMapping("/list")
    public String showNoteList(){
        return "note-list";
    }

    //Pobierz pdf
    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> pdf() throws DocumentException, IOException {
        Pdf pdf = new Pdf(notes);
        ByteArrayInputStream in = pdf.buildPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=notes.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(in));
    }

    //Dodaj notkę
    @PostMapping("/add")
    public String addNote(@ModelAttribute("addNote") AddNote addNote){
        noteService.addNote(addNote);
        return "redirect:/calendar";
    }

    //Aktualizuj notkę
    @PostMapping("/update")
    public String updateNote(@ModelAttribute("updateNote") UpdateNote updateNote){
        noteService.updateNote(updateNote);
        return "redirect:/calendar";
    }

    //Usuń notkę
    @RequestMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
        return "redirect:/calendar";
    }

    //Wczytaj notki danego kalendarza
    @PostMapping("/getCalendarNotes")
    public String getCalendarNotes(@RequestParam int calendarId, @AuthenticationPrincipal User user){
        if(calendarId==0){
            notes = getAllNotes(user);
        }else{
            notes = noteService.loadNotesByCalendarId(calendarId);
        }
        return "redirect:/notes/list";
    }

    //Wczytaj notki użytkownika
    public List<Note> getAllNotes(User user){
        return noteService.loadNotesByUserId(user.getId().intValue());
    }

    //Wczytaj kalendarze użytkownika i zapisz jako atrybut modelu
    public void loadCalendars(Model model, User user){
        List<DbCalendar> dbCalendars = calendarService.getCalendarsByUserId(user.getId().intValue());
        model.addAttribute("calendars", dbCalendars);
    }

}
