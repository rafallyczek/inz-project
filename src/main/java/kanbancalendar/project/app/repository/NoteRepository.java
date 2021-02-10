package kanbancalendar.project.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kanbancalendar.project.app.model.Note;

import java.util.List;

@Repository
public interface NoteRepository extends CrudRepository<Note,Long> {

    //Znajdź notki po id użytkownika
    List<Note> findAllByCalendarId(int id);

}
