package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.Note;

import java.util.List;

@Repository
public interface NoteRepository extends CrudRepository<Note,Long> {

    //Znajdź notki po id użytkownika
    List<Note> findAllByUserId(int id);

    //Znajdź notki po id użytkownika
    List<Note> findAllByCalendarId(int id);

}
