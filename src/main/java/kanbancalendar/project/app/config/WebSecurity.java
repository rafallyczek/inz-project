package kanbancalendar.project.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import kanbancalendar.project.app.model.CalendarRole;
import kanbancalendar.project.app.model.User;
import kanbancalendar.project.app.repository.CalendarRoleRepository;
import kanbancalendar.project.app.repository.UserRepository;

import java.util.List;

public class WebSecurity {

    @Autowired
    private CalendarRoleRepository calendarRoleRepository;

    @Autowired
    private UserRepository userRepository;

    //Sprawdź czy użytkownik ma dostęp do kalendarza
    public boolean checkCalendarId(Authentication authentication, int id){
        User user = userRepository.findByUsername(authentication.getName());
        if(user!=null){
            CalendarRole calendarRole = calendarRoleRepository.findByUserIdAndCalendarId(user.getId(),(long) id);
            return calendarRole != null;
        }
        return false;
    }

    //Sprawdź czy użytkownik ma prawa edycji w kalendarzu
    public boolean checkCalendarRole(Authentication authentication, int id){
        User user = userRepository.findByUsername(authentication.getName());
        if(user!=null){
            CalendarRole calendarRole = calendarRoleRepository.findByUserIdAndCalendarId(user.getId(),(long) id);
            if(calendarRole!=null){
                return calendarRole.getName().equals("OWNER") || calendarRole.getName().equals("ADMIN");
            }
        }
        return false;
    }

}
