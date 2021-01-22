package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.TO.AddNote;
import paw.project.calendarapp.TO.UpdateNote;
import paw.project.calendarapp.model.*;
import paw.project.calendarapp.repository.CalendarUserRepository;
import paw.project.calendarapp.repository.NoteRepository;
import paw.project.calendarapp.repository.ReminderRepository;
import paw.project.calendarapp.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {

    private NoteRepository noteRepository;
    private UserRepository userRepository;
    private CalendarUserRepository calendarUserRepository;
    private ReminderRepository reminderRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository, UserRepository userRepository, CalendarUserRepository calendarUserRepository, ReminderRepository reminderRepository){
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.calendarUserRepository = calendarUserRepository;
        this.reminderRepository = reminderRepository;
    }

    //Zwróć notki po id użytkownika
    public List<Note> loadNotesByUserId(int id, String timezone){
        List<CalendarUser> calendars = calendarUserRepository.findAllByUserId(id);
        List<Note> allNotes = new ArrayList<>();
        for(CalendarUser calendar : calendars){
            List<Note> notes = loadNotesByCalendarId(calendar.getCalendarId(), timezone);
            allNotes.addAll(notes);
        }
        return allNotes;
    }

    //Zwróć notki po id kalendarza
    public List<Note> loadNotesByCalendarId(int id, String timezone){
        List<Note> notes = noteRepository.findAllByCalendarId(id);
        for(Note note : notes){
            User owner = userRepository.findById((long) note.getUserId()).get();
            note.setNoteDate(timezone, owner.getTimezone());
            note.setNoteTime(timezone, owner.getTimezone());
        }
        return notes;
    }

    //Zwróć notkę po id
    public Note getNote(Long id){
        return noteRepository.findById(id).get();
    }

    //Dodaj notkę
    public void addNote(AddNote addNote){
        Note note = new Note();
        note.setTitle(addNote.getTitle());
        note.setContent(addNote.getContent());
        note.setUserId(addNote.getUserId());
        note.setDateTime(toLocalDateTime(addNote.getDate(),addNote.getTime()));
        note.setCalendarId(addNote.getCalendarId());
        note.setTask(addNote.getIsTask());
        if(addNote.getIsTask()){
            note.setStatus("to-do");
        }else{
            note.setStatus("-");
        }
        noteRepository.save(note);
        List<CalendarUser> calendarUsers = calendarUserRepository.findAllByCalendarId(note.getCalendarId());
        for(CalendarUser calendarUser : calendarUsers){
            User user = userRepository.findById((long) calendarUser.getUserId()).get();
            Reminder reminder = new Reminder();
            reminder.setNoteId(note.getId().intValue());
            reminder.setUserId(calendarUser.getUserId());
            reminder.setReminderTime(user.getReminderTime());
            reminder.setReminded(false);
            reminderRepository.save(reminder);
        }
    }

    //Aktualizuj notkę
    public void updateNote(UpdateNote updateNote){
        Note note = noteRepository.findById(updateNote.getNoteId()).get();
        note.setDateTime(toLocalDateTime(updateNote.getDate(),updateNote.getTime()));
        note.setTitle(updateNote.getTitle());
        note.setContent(updateNote.getContent());
        note.setTask(updateNote.getIsTask());
        if(updateNote.getIsTask()){
            if(note.getStatus().equals("-")){
                note.setStatus("to-do");
            }
        }else{
            note.setStatus("-");
        }
        noteRepository.save(note);
    }

    //Usuń notkę
    public void deleteNote(Long id){
        noteRepository.deleteById(id);
        reminderRepository.deleteAllByNoteId(id.intValue());
    }

    //Zmień status notki
    public void changeStatus(Long id, String status){
        Note note = noteRepository.findById(id).get();
        note.setStatus(status);
        noteRepository.save(note);
    }

    //Konwersja daty i czasu na LocalDateTime
    public LocalDateTime toLocalDateTime(String date, String time){
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String dateTime = date+" "+time+":00";
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(dateTimeFormat));
    }

}
