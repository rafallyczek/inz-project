package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.TO.AddNote;
import paw.project.calendarapp.TO.UpdateNote;
import paw.project.calendarapp.model.*;
import paw.project.calendarapp.repository.*;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NoteService {

    private NoteRepository noteRepository;
    private UserRepository userRepository;
    private ReminderRepository reminderRepository;
    private EmailService emailService;
    private CalendarRoleRepository calendarRoleRepository;

    @Autowired
    public NoteService(NoteRepository noteRepository,
                       UserRepository userRepository,
                       ReminderRepository reminderRepository,
                       EmailService emailService,
                       CalendarRoleRepository calendarRoleRepository){
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.reminderRepository = reminderRepository;
        this.emailService = emailService;
        this.calendarRoleRepository = calendarRoleRepository;
    }

    //Zwróć notki po id użytkownika
    public List<Note> loadNotesByUserId(int id, String timezone){
        List<CalendarRole> calendarRoles = calendarRoleRepository.findAllByUserId((long)id);
        List<Note> allNotes = new ArrayList<>();
        for(CalendarRole calendarRole : calendarRoles){
            List<Note> notes = loadNotesByCalendarId(calendarRole.getCalendarId().intValue(), timezone);
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
        List<CalendarRole> calendarRoles = calendarRoleRepository.findAllByCalendarId((long)note.getCalendarId());
        for(CalendarRole calendarRole : calendarRoles){
            User user = userRepository.findById(calendarRole.getUserId()).get();
            Reminder reminder = new Reminder();
            reminder.setObjectId(note.getId().intValue());
            reminder.setUserId(calendarRole.getUserId().intValue());
            reminder.setReminderTime(user.getReminderTime());
            reminder.setReminded(false);
            reminder.setType("note");
            reminderRepository.save(reminder);
        }
    }

    //Aktualizuj notkę
    public Note updateNote(UpdateNote updateNote){
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
        return note;
    }

    //Usuń notkę
    public void deleteNote(Long id){
        noteRepository.deleteById(id);
        List<Reminder> noteReminders = reminderRepository.findAllByObjectId(id.intValue());
        for(Reminder reminder : noteReminders){
            if(reminder.getType().equals("note")){
                reminderRepository.deleteById(reminder.getId());
            }
        }
    }

    //Zmień status notki
    public Note changeStatus(Long id, String status) throws MessagingException {
        Note note = noteRepository.findById(id).get();
        note.setStatus(status);
        if(status.equals("to-do")){
            status = "Do zrobienia";
        }else if(status.equals("in-progress")){
            status = "W trakcie";
        }else if(status.equals("finished")){
            status = "Zakończone";
        }
        List<CalendarRole> calendarRoles = calendarRoleRepository.findAllByCalendarId((long)note.getCalendarId());
        List<Reminder> reminders = reminderRepository.findAllByObjectId(note.getId().intValue());
        reminders.removeIf(reminder -> !reminder.getType().equals("status-change"));
        for(CalendarRole calendarRole : calendarRoles){
            User user = userRepository.findById(calendarRole.getUserId()).get();
            Reminder reminder = new Reminder();
            reminder.setObjectId(note.getId().intValue());
            reminder.setUserId(user.getId().intValue());
            reminder.setReminderTime(user.getReminderTime());
            reminder.setReminded(false);
            reminder.setType("status-change");
            if(!reminders.contains(reminder)){
                reminderRepository.save(reminder);
            }
            emailService.sendEmail(user.getEmail(),"Aktualizacja zadania",
                    "<p>Zmieniono status zadania: <b>"+note.getTitle()+"</b></p>" +
                            "<p>Na: <b>"+status+"</b></p>");
        }
        noteRepository.save(note);
        return note;
    }

    //Konwersja daty i czasu na LocalDateTime
    public LocalDateTime toLocalDateTime(String date, String time){
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        String dateTime = date+" "+time+":00";
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(dateTimeFormat));
    }

}
