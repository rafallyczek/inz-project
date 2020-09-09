package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.DbCalendar;

@Repository
public interface CalendarRepository extends CrudRepository<DbCalendar,Long> {
}
