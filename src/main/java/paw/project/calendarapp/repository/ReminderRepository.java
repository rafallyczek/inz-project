package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.DbReminder;

import java.util.List;

@Repository
public interface ReminderRepository extends CrudRepository<DbReminder,Long> {

    DbReminder findByUserIdAndNoteId(int userId, int noteId);

    List<DbReminder> findAllByUserId(int id);

    void deleteAllByNoteId(int id);

}
