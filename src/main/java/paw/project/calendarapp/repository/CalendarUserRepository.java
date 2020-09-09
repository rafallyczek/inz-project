package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.CalendarUser;

import java.util.List;

@Repository
public interface CalendarUserRepository extends CrudRepository<CalendarUser,Long> {

    List<CalendarUser> findAllByUserId(int id);

}
