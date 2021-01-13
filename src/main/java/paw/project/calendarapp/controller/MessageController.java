package paw.project.calendarapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import paw.project.calendarapp.model.Invitation;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.service.InvitationService;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private InvitationService invitationService;

    //Wstrzykiwanie zależności
    @Autowired
    public MessageController(InvitationService invitationService){
        this.invitationService = invitationService;
    }

    //Ustaw atrybuty modelu
    @ModelAttribute
    public void setModelAttributes(Model model, @AuthenticationPrincipal User user){
        if(user!=null){
            loadInvitations(model, user);
        }
    }

    //Wyświetl wiadomości
    @GetMapping
    public String messages(){
        return "messages";
    }

    //Akceptuj zaproszenie
    @GetMapping("/accept/{id}")
    public String acceptInvitation(@PathVariable Long id){
        invitationService.acceptInvitation(id);
        return "redirect:/messages";
    }

    //Odrzuć zaproszenie
    @GetMapping("/reject/{id}")
    public String rejectInvitation(@PathVariable Long id){
        invitationService.deleteInvitation(id);
        return "redirect:/messages";
    }

    //Wczytaj zaproszenia użytkownika i zapisz jako atrybut modelu
    public void loadInvitations(Model model, User user){
        List<Invitation> invitations = invitationService.getInvitationsByUserId(user.getId().intValue());
        model.addAttribute("invitations", invitations);
    }

}
