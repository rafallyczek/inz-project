package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.ReminderCheck;

import java.util.List;

@Repository
public interface ReminderCheckRepository extends CrudRepository<ReminderCheck,Long> {

    List<ReminderCheck> findAllByUserId(int id);

    ReminderCheck findByUserIdAndNoteId(int userId, int noteId);

    void deleteAllByNoteId(int id);

}
