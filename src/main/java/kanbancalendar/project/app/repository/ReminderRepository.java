package kanbancalendar.project.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kanbancalendar.project.app.model.Reminder;

import java.util.List;

@Repository
public interface ReminderRepository extends CrudRepository<Reminder,Long> {

    List<Reminder> findAllByUserId(int id);

    List<Reminder> findAllByObjectId(int id);

}
