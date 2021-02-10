package kanbancalendar.project.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kanbancalendar.project.app.model.CalendarRole;
import kanbancalendar.project.app.repository.CalendarRoleRepository;

import java.util.List;

@Service
public class CalendarRoleService {

    private final CalendarRoleRepository calendarRoleRepository;

    @Autowired
    public CalendarRoleService(CalendarRoleRepository calendarRoleRepository){
        this.calendarRoleRepository = calendarRoleRepository;
    }

    //Zwróć uprawnienie dla danego kalendarza
    public CalendarRole getUserCalendarRole(Long userId, Long calendarId){
        return calendarRoleRepository.findByUserIdAndCalendarId(userId, calendarId);
    }

    //Zwróć uprawnienia użytkownika
    public List<CalendarRole> getAllRolesByUserId(Long id){
        return calendarRoleRepository.findAllByUserId(id);
    }

    //Dodaj uprawnienie
    public void addRole(CalendarRole calendarRole){
        calendarRoleRepository.save(calendarRole);
    }

    //Usuń uprawnienie
    public void deleteRole(Long id){
        calendarRoleRepository.deleteById(id);
    }

}
