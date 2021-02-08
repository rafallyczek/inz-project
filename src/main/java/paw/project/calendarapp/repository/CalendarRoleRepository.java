package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.CalendarRole;

import java.util.List;

@Repository
public interface CalendarRoleRepository extends CrudRepository<CalendarRole,Long> {

    List<CalendarRole> findAllByCalendarId(Long id);

    List<CalendarRole> findAllByUserId(Long id);

    CalendarRole findByUserIdAndCalendarId(Long userId, Long calendarId);

}
