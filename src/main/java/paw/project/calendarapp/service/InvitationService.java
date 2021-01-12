package paw.project.calendarapp.service;

import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.Invitation;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.InvitationRepository;
import paw.project.calendarapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvitationService {

    private InvitationRepository invitationRepository;
    private UserRepository userRepository;

    public InvitationService(InvitationRepository invitationRepository, UserRepository userRepository){
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
    }

    //Dodaj zaproszenie
    public void addInvitation(Invitation invitation){
        invitationRepository.save(invitation);
    }

    //Zwróć zaproszenia po id użytkownika
    public List<Invitation> getInvitationsByUserId(int id){
        return invitationRepository.findAllByReceiverId(id);
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

}
