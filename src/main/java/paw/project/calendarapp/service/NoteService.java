package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.repository.NoteRepository;

import java.util.List;

@Service
public class NoteService {

    private NoteRepository noteRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository){
        this.noteRepository = noteRepository;
    }

    //Zwróć notki po id użytkownika
    public List<Note> loadNotesByUserId(int id){
        return noteRepository.findAllByUserId(id);
    }

    //Zwróć notki po id kalendarza
    public List<Note> loadNotesByCalendarId(int id){
        return noteRepository.findAllByCalendarId(id);
    }

    //Dodaj notkę
    public void addNote(Note note){
        noteRepository.save(note);
    }

    //Aktualizuj notkę
    public void updateNote(Note note){
        Note newNote = noteRepository.findById(note.getId()).get();
        newNote.setTitle(note.getTitle());
        newNote.setContent(note.getContent());
        noteRepository.save(newNote);
    }

    //Usuń notkę
    public void deleteNote(Long id){
        noteRepository.deleteById(id);
    }

}
