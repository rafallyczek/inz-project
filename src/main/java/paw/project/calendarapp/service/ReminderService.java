package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.Reminder;
import paw.project.calendarapp.model.Note;
import paw.project.calendarapp.model.DbReminder;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.NoteRepository;
import paw.project.calendarapp.repository.ReminderRepository;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReminderService {

    private EmailService emailService;
    private UserService userService;
    private NoteRepository noteRepository;
    private ReminderRepository reminderRepository;
    private SimpMessagingTemplate template;

    @Autowired
    public ReminderService(EmailService emailService, UserService userService, NoteRepository noteRepository, ReminderRepository reminderRepository, SimpMessagingTemplate template){
        this.emailService = emailService;
        this.userService = userService;
        this.noteRepository = noteRepository;
        this.reminderRepository = reminderRepository;
        this.template = template;
    }

    //Zwróć wszystkie przypomnienia dla danego użytkownika
    public List<DbReminder> getAllRemindersByUserId(int id, String timezone){
        List<DbReminder> reminders = reminderRepository.findAllByUserId(id);
        for(DbReminder reminder : reminders){
            Note note = noteRepository.findById((long)reminder.getNoteId()).get();
            User owner = userService.getUser((long)note.getUserId());
            note.setNoteDate(timezone,owner.getTimezone());
            note.setNoteTime(timezone,owner.getTimezone());
            reminder.setNoteTitle(note.getTitle());
            reminder.setNoteDate(note.getDate().toString());
            reminder.setNoteTime(note.getTime());
        }
        return reminders;
    }

    //Zwróć przypomnienie
    public DbReminder getReminderById(Long id){
        return reminderRepository.findById(id).get();
    }

    //Zaktualizuj przypomnienie
    public void updateReminder(DbReminder dbReminder){
        reminderRepository.save(dbReminder);
    }

    //Wyślij wiadomość email z przypomnieniem; 30*60*1000 = 1800000 (wywołaj co 30 minut); 60*1000 = 60000 (pierwsze wywołanie po 1 minucie)
    @Scheduled(fixedRate = 1800000, initialDelay = 60000)
    public void reminder() throws MessagingException {
        List<Note> notes = new ArrayList<>();
        noteRepository.findAll().forEach(notes::add);
        for(Note note : notes){
            List<User> users = userService.getAllUsersByCalendarId(note.getCalendarId());
            for(User user : users){
                User owner = userService.getUser((long) note.getUserId());
                ZonedDateTime ownerDateTime = note.getDateTime().atZone(ZoneId.of(owner.getTimezone()));
                ZonedDateTime localDateTime = ownerDateTime.withZoneSameInstant(ZoneId.of(user.getTimezone()));
                ZonedDateTime now = ZonedDateTime.now(ZoneId.of(user.getTimezone()));
                Duration duration = Duration.between(now, localDateTime);
                DbReminder dbReminder = reminderRepository.findByUserIdAndNoteId(user.getId().intValue(),note.getId().intValue());
                if(!dbReminder.isReminded() && !duration.isNegative() && duration.toMinutes()< dbReminder.getReminderTime()){
                    emailService.sendEmail(user.getEmail(),"Wydarzenie","<p>Przypomnienie o nadchodzącym wydarzeniu.</p><p>Wydarzenie: <b>" +
                            note.getTitle()+"</b></p>"+"<p>Opis: <b>"+note.getContent()+"</b></p><p>Zaplanowano na: <b>"+localDateTime.toLocalDate().toString() +
                            "</b>, godzina: <b>"+localDateTime.toLocalTime().toString()+"</b></p>");
                    template.convertAndSendToUser(user.getUsername(),"/queue/message", new Reminder("Powiadomienie",
                            note.getTitle(),localDateTime.toLocalDate().toString(),localDateTime.toLocalTime().toString(), dbReminder.getId().intValue()));
                }
            }
        }
    }

}
