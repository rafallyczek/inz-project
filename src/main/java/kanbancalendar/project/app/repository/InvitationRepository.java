package kanbancalendar.project.app.repository;

import kanbancalendar.project.app.model.Invitation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation,Long> {

    List<Invitation> findAllByReceiverId(int id);

    List<Invitation> findAllByCalendarId(int id);

}
