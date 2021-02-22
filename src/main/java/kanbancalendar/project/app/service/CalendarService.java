package kanbancalendar.project.app.service;

import kanbancalendar.project.app.model.DbCalendar;
import kanbancalendar.project.app.repository.CalendarRepository;
import org.springframework.stereotype.Service;
import kanbancalendar.project.app.model.CalendarRole;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final CalendarRoleService calendarRoleService;

    public CalendarService(CalendarRepository calendarRepository,
                           CalendarRoleService calendarRoleService){
        this.calendarRepository = calendarRepository;
        this.calendarRoleService = calendarRoleService;
    }

    //Dodaj kalendarz
    public void addCalendar(DbCalendar dbCalendar){
        calendarRepository.save(dbCalendar);
        CalendarRole calendarRole = new CalendarRole();
        calendarRole.setCalendarId(dbCalendar.getId());
        calendarRole.setUserId((long)dbCalendar.getOwnerId());
        calendarRole.setName("OWNER");
        calendarRoleService.addRole(calendarRole);
    }

    //Zwróć kalendarze po id użytkownika
    public List<DbCalendar> getCalendarsByUserId(int id){
        List<DbCalendar> calendars = new ArrayList<>();
        List<CalendarRole> calendarRoles = calendarRoleService.getAllRolesByUserId((long)id);
        for(CalendarRole calendarRole : calendarRoles){
            DbCalendar dbCalendar = calendarRepository.findById(calendarRole.getCalendarId()).get();
            calendars.add(dbCalendar);
        }
        return calendars;
    }

    //Zwróć kalendarz po id
    public DbCalendar getCalendar(Long id){
        return calendarRepository.findById(id).get();
    }

    //Aktualizuj kalendarz
    public void updateCalendar(DbCalendar dbCalendar){
        DbCalendar calendar = calendarRepository.findById(dbCalendar.getId()).get();
        calendar.setTitle(dbCalendar.getTitle());
        calendar.setDescription(dbCalendar.getDescription());
        calendarRepository.save(calendar);
    }

}
