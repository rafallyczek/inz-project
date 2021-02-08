package paw.project.calendarapp.service;

import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.*;
import paw.project.calendarapp.repository.*;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final CalendarRepository calendarRepository;
    private final ReminderRepository reminderRepository;
    private final EmailService emailService;
    private final CalendarRoleService calendarRoleService;

    public InvitationService(InvitationRepository invitationRepository,
                             UserRepository userRepository,
                             CalendarRepository calendarRepository,
                             ReminderRepository reminderRepository,
                             EmailService emailService,
                             CalendarRoleService calendarRoleService){
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
        this.reminderRepository = reminderRepository;
        this.emailService = emailService;
        this.calendarRoleService = calendarRoleService;
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
        CalendarRole calendarRole = new CalendarRole();
        calendarRole.setUserId((long)invitation.getReceiverId());
        calendarRole.setCalendarId((long)invitation.getCalendarId());
        calendarRole.setName("NORMAL_MEMBER");
        calendarRoleService.addRole(calendarRole);
        invitationRepository.deleteById(id);
    }

    //Usuń zaproszenie
    public void deleteInvitation(Long id){
        invitationRepository.deleteById(id);
    }

}
