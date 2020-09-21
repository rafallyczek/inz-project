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
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.pdf.Pdf;
import paw.project.calendarapp.service.NoteService;


import java.io.*;
import java.util.List;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private NoteService noteService;

    //Wstrzykiwanie serwisu
    @Autowired
    public NoteController(NoteService noteService){
        this.noteService = noteService;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void notes(Model model, @AuthenticationPrincipal User user){
        List<Note> notes = getAllNotes(user);
        model.addAttribute("notes", notes);
    }

    //Wyświetl listę notek
    @GetMapping("/list")
    public String showNoteList(){
        return "note-list";
    }

    //Pobierz pdf
    @GetMapping("/pdf")
    public ResponseEntity<InputStreamResource> pdf(@AuthenticationPrincipal User user) throws DocumentException, IOException {
        List<Note> notes = getAllNotes(user);
        Pdf pdf = new Pdf(notes);
        ByteArrayInputStream in = pdf.buildPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition","attachment; filename=notes.pdf");
        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(in));
    }

    //Dodaj notkę
    @PostMapping("/add")
    public String addNote(@ModelAttribute("note") Note note){
        noteService.addNote(note);
        return "redirect:/calendar";
    }

    //Aktualizuj notkę
    @PostMapping("/update")
    public String updateNote(@ModelAttribute("note") Note note){
        noteService.updateNote(note);
        return "redirect:/calendar";
    }

    //Usuń notkę
    @RequestMapping("/delete/{id}")
    public String deleteNote(@PathVariable Long id){
        noteService.deleteNote(id);
        return "redirect:/calendar";
    }

    //Wczytaj notki użytkownika
    public List<Note> getAllNotes(User user){
        return noteService.loadNotesByUserId(user.getId().intValue());
    }

}
