package kanbancalendar.project.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kanbancalendar.project.app.model.DbCalendar;

@Repository
public interface CalendarRepository extends CrudRepository<DbCalendar,Long> {
}
