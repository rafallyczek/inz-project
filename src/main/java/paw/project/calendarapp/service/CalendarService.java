package paw.project.calendarapp.service;

import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.CalendarUser;
import paw.project.calendarapp.model.DbCalendar;
import paw.project.calendarapp.repository.CalendarRepository;
import paw.project.calendarapp.repository.CalendarUserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalendarService {

    private CalendarRepository calendarRepository;
    private CalendarUserRepository calendarUserRepository;

    public CalendarService(CalendarRepository calendarRepository, CalendarUserRepository calendarUserRepository){
        this.calendarRepository = calendarRepository;
        this.calendarUserRepository = calendarUserRepository;
    }

    //Dodaj kalendarz
    public void addCalendar(DbCalendar dbCalendar){
        calendarRepository.save(dbCalendar);
        CalendarUser calendarUser = new CalendarUser();
        calendarUser.setCalendarId(dbCalendar.getId().intValue());
        calendarUser.setUserId(dbCalendar.getOwnerId());
        addCalendarUser(calendarUser);
    }

    //Dodaj powiązanie użytkownika z kalendarzem
    public void addCalendarUser(CalendarUser calendarUser){
        calendarUserRepository.save(calendarUser);
    }

    //Zwróć kalendarze po id użytkownika
    public List<DbCalendar> getCalendarsByUserId(int id){
        List<DbCalendar> calendars = new ArrayList<>();
        List<CalendarUser> calendarUsers = calendarUserRepository.findAllByUserId(id);
        for(CalendarUser calendarUser : calendarUsers){
            DbCalendar dbCalendar = calendarRepository.findById(calendarUser.getCalendarId().longValue()).get();
            calendars.add(dbCalendar);
        }
        return calendars;
    }

}
