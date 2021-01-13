package paw.project.calendarapp.service;

import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.CalendarUser;
import paw.project.calendarapp.model.DbCalendar;
import paw.project.calendarapp.model.Invitation;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.CalendarRepository;
import paw.project.calendarapp.repository.CalendarUserRepository;
import paw.project.calendarapp.repository.InvitationRepository;
import paw.project.calendarapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvitationService {

    private InvitationRepository invitationRepository;
    private UserRepository userRepository;
    private CalendarRepository calendarRepository;
    private CalendarUserRepository calendarUserRepository;

    public InvitationService(InvitationRepository invitationRepository, UserRepository userRepository, CalendarRepository calendarRepository, CalendarUserRepository calendarUserRepository){
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.calendarRepository = calendarRepository;
        this.calendarUserRepository = calendarUserRepository;
    }

    //Dodaj zaproszenie
    public void addInvitation(Invitation invitation){
        invitationRepository.save(invitation);
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
