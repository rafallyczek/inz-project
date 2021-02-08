package paw.project.calendarapp.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import paw.project.calendarapp.model.Role;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role,Long> {

    List<Role> findAllByUserId(Long id);

    Role findByUserIdAndCalendarId(Long userId, Long calendarId);

}
