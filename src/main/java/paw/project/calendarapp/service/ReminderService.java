package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.*;
import paw.project.calendarapp.repository.CalendarRepository;
import paw.project.calendarapp.repository.NoteRepository;
import paw.project.calendarapp.repository.ReminderRepository;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ReminderService {

    private final EmailService emailService;
    private final UserService userService;
    private final InvitationService invitationService;
    private final NoteRepository noteRepository;
    private final ReminderRepository reminderRepository;
    private final CalendarRepository calendarRepository;
    private final SimpMessagingTemplate template;

    @Autowired
    public ReminderService(EmailService emailService,
                           UserService userService,
                           InvitationService invitationService,
                           NoteRepository noteRepository,
                           ReminderRepository reminderRepository,
                           SimpMessagingTemplate template,
                           CalendarRepository calendarRepository){
        this.emailService = emailService;
        this.userService = userService;
        this.invitationService = invitationService;
        this.noteRepository = noteRepository;
        this.reminderRepository = reminderRepository;
        this.template = template;
        this.calendarRepository = calendarRepository;
    }

    //Zwróć wszystkie przypomnienia dla danego użytkownika
    public List<Reminder> getAllRemindersByUserId(int id, String timezone){
        return reminderRepository.findAllByUserId(id);
    }

    //Zwróć przypomnienie
    public Reminder getReminderById(Long id){
        return reminderRepository.findById(id).get();
    }

    //Zaktualizuj przypomnienie
    public void updateReminder(Reminder reminder){
        reminderRepository.save(reminder);
    }

    //Wyślij przypomnienia; 30*60*1000 = 1800000 (wywołaj co 30 minut); 60*1000 = 60000 (pierwsze wywołanie po 1 minucie)
    @Scheduled(fixedRate = 1800000, initialDelay = 60000)
    public void sendReminders() throws MessagingException {
        List<Reminder> reminders = new ArrayList<>();
        reminderRepository.findAll().forEach(reminders::add);
        for(Reminder reminder : reminders) {
            switch (reminder.getType()) {
                case "note":
                    noteReminder(reminder);
                    break;
                case "invitation":
                    invitationReminder(reminder);
                    break;
                case "status-change":
                    statusChangeReminder(reminder);
                    break;
            }
        }
    }

    //Przypomnienie dotyczące notki
    public void noteReminder(Reminder reminder) throws MessagingException {
        Note note = noteRepository.findById((long) reminder.getObjectId()).get();
        User owner = userService.getUser((long) note.getUserId());
        User user = userService.getUser((long) reminder.getUserId());
        ZonedDateTime ownerDateTime = note.getDateTime().atZone(ZoneId.of(owner.getTimezone()));
        ZonedDateTime localDateTime = ownerDateTime.withZoneSameInstant(ZoneId.of(user.getTimezone()));
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(user.getTimezone()));
        Duration duration = Duration.between(now, localDateTime);
        if (duration.isNegative() || reminder.isReminded()) {
            reminderRepository.deleteById(reminder.getId());
        }else if(duration.toMinutes()<reminder.getReminderTime()){
                emailService.sendEmail(user.getEmail(),"Wydarzenie","<p>Przypomnienie o nadchodzącym wydarzeniu.</p><p>Wydarzenie: <b>" +
                        note.getTitle()+"</b></p>"+"<p>Opis: <b>"+note.getContent()+"</b></p><p>Zaplanowano na: <b>"+localDateTime.toLocalDate().toString() +
                        "</b>, godzina: <b>"+localDateTime.toLocalTime().toString()+"</b></p>");
            Message message = new Message();
            message.setTitle("Powiadomienie");
            message.setContent("Nadchodzące wydarzenie: <span id='wsMessageLinkText' onclick='goTo()'>"+note.getTitle()+"</span>");
            HashMap<String, String> data = new HashMap<>();
            data.put("Data: ",localDateTime.toLocalDate().toString());
            data.put("Godzina: ",localDateTime.toLocalTime().toString());
            data.put("type","note");
            data.put("id",reminder.getId().toString());
            message.setData(data);
            template.convertAndSendToUser(user.getUsername(),"/queue/message", message);
        }
    }

    //Przypomnienie dotyczące zaproszenia
    public void invitationReminder(Reminder reminder){
        Invitation invitation = invitationService.getInvitation((long) reminder.getObjectId());
        User user = userService.getUser((long)invitation.getReceiverId());
        User sender = userService.getUser((long)invitation.getSenderId());
        DbCalendar dbCalendar = calendarRepository.findById((long) invitation.getCalendarId()).get();
        if (reminder.isReminded()) {
            reminderRepository.deleteById(reminder.getId());
        }else{
            Message message = new Message();
            message.setTitle("Zaproszenie");
            message.setContent("Otrzymano zaproszenie do kalendarza: <span id='wsMessageLinkText' onclick='goTo()'>"+dbCalendar.getTitle()+"</span>");
            HashMap<String, String> data = new HashMap<>();
            data.put("Od użytkownika: ",sender.getUsername());
            data.put("type","invitation");
            data.put("id",reminder.getId().toString());
            message.setData(data);
            template.convertAndSendToUser(user.getUsername(),"/queue/message", message);
        }
    }

    //Przypomnienie dotyczące zmiany statusu zadania
    public void statusChangeReminder(Reminder reminder){
        Note note = noteRepository.findById((long) reminder.getObjectId()).get();
        User user = userService.getUser((long)note.getUserId());
        Message message = new Message();
        message.setTitle("Aktualizacja zadania");
        message.setContent("Zmieniono status zadania: <span id='wsMessageLinkText' onclick='goTo()'>"+note.getTitle()+"</span>");
        HashMap<String, String> data = new HashMap<>();
        switch (note.getStatus()) {
            case "to-do":
                data.put("Nowy status: ", "Do zrobienia");
                break;
            case "in-progress":
                data.put("Nowy status: ", "W trakcie");
                break;
            case "finished":
                data.put("Nowy status: ", "Zakończone");
                break;
        }
        data.put("type","status-change");
        data.put("id",reminder.getId().toString());
        message.setData(data);
        template.convertAndSendToUser(user.getUsername(),"/queue/message", message);
    }

}
