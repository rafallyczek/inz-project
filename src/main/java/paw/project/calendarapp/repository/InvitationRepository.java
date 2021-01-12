package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.Invitation;

import java.util.List;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation,Long> {

    List<Invitation> findAllByReceiverId(int id);

    List<Invitation> findAllByCalendarId(int id);

}
