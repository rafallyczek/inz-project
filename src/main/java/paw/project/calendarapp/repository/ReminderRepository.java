package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.Reminder;

import java.util.List;

@Repository
public interface ReminderRepository extends CrudRepository<Reminder,Long> {

    Reminder findByUserIdAndNoteId(int userId, int noteId);

    List<Reminder> findAllByUserId(int id);

    void deleteAllByNoteId(int id);

}
