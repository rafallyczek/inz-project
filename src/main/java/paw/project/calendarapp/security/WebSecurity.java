package paw.project.calendarapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import paw.project.calendarapp.model.CalendarRole;
import paw.project.calendarapp.model.User;
import paw.project.calendarapp.repository.CalendarRoleRepository;
import paw.project.calendarapp.repository.UserRepository;

import java.util.List;

public class WebSecurity {

    @Autowired
    private CalendarRoleRepository calendarRoleRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean checkCalendarId(Authentication authentication, int id){
        User user = userRepository.findByUsername(authentication.getName());
        if(user!=null){
            List<CalendarRole> calendarRoles = calendarRoleRepository.findAllByCalendarId((long)id);
            for(CalendarRole calendarRole : calendarRoles){
                if(user.getId().equals(calendarRole.getUserId())){
                    return true;
                }
            }
        }
        return false;
    }

}
