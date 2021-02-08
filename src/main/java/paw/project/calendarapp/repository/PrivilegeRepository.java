package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.Privilege;

import java.util.List;

@Repository
public interface PrivilegeRepository extends CrudRepository<Privilege,Long> {

    List<Privilege> findAllByUserId(Long id);

    Privilege findByUserIdAndCalendarId(Long userId, Long calendarId);

}
