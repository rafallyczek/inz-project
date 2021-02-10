package kanbancalendar.project.app.repository;

import kanbancalendar.project.app.model.CalendarRole;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRoleRepository extends CrudRepository<CalendarRole,Long> {

    List<CalendarRole> findAllByCalendarId(Long id);

    List<CalendarRole> findAllByUserId(Long id);

    CalendarRole findByUserIdAndCalendarId(Long userId, Long calendarId);

}
