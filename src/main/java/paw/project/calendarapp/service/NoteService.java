package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.TO.AddNote;
import paw.project.calendarapp.TO.UpdateNote;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.repository.NoteRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        List<Note> notes = noteRepository.findAllByUserId(id);
        for(Note note : notes){
            note.setNoteDate();
            note.setNoteTime();
        }
        return notes;
    }

    //Zwróć notki po id kalendarza
    public List<Note> loadNotesByCalendarId(int id){
        List<Note> notes = noteRepository.findAllByCalendarId(id);
        for(Note note : notes){
            note.setNoteDate();
            note.setNoteTime();
        }
        return notes;
    }

    //Dodaj notkę
    public void addNote(AddNote addNote){
        Note note = new Note();
        note.setTitle(addNote.getTitle());
        note.setContent(addNote.getContent());
        note.setUserId(addNote.getUserId());
        note.setDateTime(toLocalDateTime(addNote.getDate(),addNote.getTime()));
        note.setCalendarId(addNote.getCalendarId());
        noteRepository.save(note);
    }

    //Aktualizuj notkę
    public void updateNote(UpdateNote updateNote){
        Note note = noteRepository.findById(updateNote.getNoteId()).get();
        note.setDateTime(toLocalDateTime(updateNote.getDate(),updateNote.getTime()));
        note.setTitle(updateNote.getTitle());
        note.setContent(updateNote.getContent());
        noteRepository.save(note);
    }

    //Usuń notkę
    public void deleteNote(Long id){
        noteRepository.deleteById(id);
    }

    //Konwersja daty i czasu na LocalDateTime
    public LocalDateTime toLocalDateTime(String date, String time){
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String dateTime = date+" "+time+":00";
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(dateTimeFormat));
    }

}
