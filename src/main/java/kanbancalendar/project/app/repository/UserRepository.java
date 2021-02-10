package kanbancalendar.project.app.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import kanbancalendar.project.app.model.User;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User,Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    List<User> findByUsernameContaining(String username);

}
