package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.CalendarRole;
import paw.project.calendarapp.repository.CalendarRoleRepository;

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
