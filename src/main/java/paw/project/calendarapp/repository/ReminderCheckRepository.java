package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.ReminderCheck;

@Repository
public interface ReminderCheckRepository extends CrudRepository<ReminderCheck,Long> {

    ReminderCheck findByUserIdAndNoteId(int userId, int noteId);

    void deleteAllByNoteId(int id);

}
