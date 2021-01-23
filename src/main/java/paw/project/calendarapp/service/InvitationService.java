package paw.project.calendarapp.service;

import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.*;
import paw.project.calendarapp.repository.*;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvitationService {

    private InvitationRepository invitationRepository;
    private UserRepository userRepository;
    private CalendarRepository calendarRepository;
    private CalendarUserRepository calendarUserRepository;
    private ReminderRepository reminderRepository;
    private EmailService emailService;

    public InvitationService(InvitationRepository invitationRepository,
                             UserRepository userRepository,
                             CalendarRepository calendarRepository,
                             CalendarUserRepository calendarUserRepository,
                             ReminderRepository reminderRepository,
                             EmailService emailService){
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
        this.calendarUserRepository = calendarUserRepository;
        this.reminderRepository = reminderRepository;
        this.emailService = emailService;
    }

    //Dodaj zaproszenie
    public void addInvitation(Invitation invitation) throws MessagingException {
        User user = userRepository.findById((long) invitation.getReceiverId()).get();
        DbCalendar dbCalendar = calendarRepository.findById((long) invitation.getCalendarId()).get();
        emailService.sendEmail(user.getEmail(),"Zaproszenie","<p>Otrzymano zaproszenie do kalendarza: <b>"+dbCalendar.getTitle()+"</b></p>" +
                "<p>Od użytkownika: <b>"+user.getUsername()+"</b></p>" +
                "<a href='https://localhost:8443/messages'>Zobacz zaproszenie</a>");
        invitationRepository.save(invitation);
        Reminder reminder = new Reminder();
        reminder.setObjectId(invitation.getId().intValue());
        reminder.setUserId(invitation.getReceiverId());
        reminder.setReminderTime(user.getReminderTime());
        reminder.setReminded(false);
        reminder.setType("invitation");
        reminderRepository.save(reminder);
    }

    //Zwróć zaproszenie
    public Invitation getInvitation(Long id){
        return invitationRepository.findById(id).get();
    }

    //Zwróć zaproszenia po id użytkownika
    public List<Invitation> getInvitationsByUserId(int id){
        List<Invitation> invitations = invitationRepository.findAllByReceiverId(id);
        for(Invitation invitation : invitations){
            DbCalendar dbCalendar = calendarRepository.findById((long) invitation.getCalendarId()).get();
            User user = userRepository.findById((long) invitation.getSenderId()).get();
            invitation.setCalendarTitle(dbCalendar.getTitle());
            invitation.setSenderName(user.getUsername());
        }
        return invitations;
    }

    //Zwróć zaproszenia po id kalendarza
    public List<Invitation> getInvitationsByCalendarId(int id){
        return invitationRepository.findAllByCalendarId(id);
    }

    //Zwróć użytkowników zaproszonych do kalendarza
    public List<User> getInvitedUsers(int id){
        List<Invitation> calendarInvitations = getInvitationsByCalendarId(id);
        List<User> invitedUsers = new ArrayList<>();
        for(Invitation invitation : calendarInvitations){
            User user = userRepository.findById((long) invitation.getReceiverId()).get();
            invitedUsers.add(user);
        }
        return invitedUsers;
    }

    //Akceptuj zaproszenie
    public void acceptInvitation(Long id){
        Invitation invitation = invitationRepository.findById(id).get();
        CalendarUser calendarUser = new CalendarUser();
        calendarUser.setUserId(invitation.getReceiverId());
        calendarUser.setCalendarId(invitation.getCalendarId());
        calendarUserRepository.save(calendarUser);
        invitationRepository.deleteById(id);
    }

    //Usuń zaproszenie
    public void deleteInvitation(Long id){
        invitationRepository.deleteById(id);
    }

}
