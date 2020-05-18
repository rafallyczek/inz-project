package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.service.NoteService;

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

    @GetMapping("/list")
    public String showNoteList(){
        return "list";
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

    //Pobierz notkę i zapisz ją w modelu (notka do edycji)
    @GetMapping("/get/{id}")
    public String getNote(@PathVariable Long id, Model model){
        model.addAttribute("retrievednote",noteService.getNote(id));
        return "redirect:/notes/update";
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
