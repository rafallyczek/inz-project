package paw.project.calendarapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import paw.project.calendarapp.model.CalendarUser;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.CalendarUserRepository;
import paw.project.calendarapp.repository.UserRepository;

import java.util.List;

public class WebSecurity {

    @Autowired
    private CalendarUserRepository calendarUserRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean checkCalendarId(Authentication authentication, String id){
        int calendarId;
        try{
            calendarId = Integer.parseInt(id);
        }catch(NumberFormatException e){
            return true;
        }
        User user = userRepository.findByUsername(authentication.getName());
        if(user!=null){
            List<CalendarUser> calendarUsers = calendarUserRepository.findAllByCalendarId(calendarId);
            for(CalendarUser calendarUser : calendarUsers){
                if(user.getId().intValue()==calendarUser.getUserId()){
                    return true;
                }
            }
        }
        return false;
    }

}
