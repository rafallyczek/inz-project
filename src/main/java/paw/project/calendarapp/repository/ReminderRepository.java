package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.Reminder;

import java.util.List;

@Repository
public interface ReminderRepository extends CrudRepository<Reminder,Long> {

    List<Reminder> findAllByUserId(int id);

    List<Reminder> findAllByObjectId(int id);

}
