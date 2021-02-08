package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.Role;
import paw.project.calendarapp.repository.RoleRepository;

import java.util.List;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    //Zwróć uprawnienia użytkownika
    public List<Role> getUserPrivileges(Long id){
        return roleRepository.findAllByUserId(id);
    }

    //Zwróć uprawnienie dla danego kalendarza
    public Role getUserCalendarPrivilege(Long userId, Long calendarId){
        return roleRepository.findByUserIdAndCalendarId(userId, calendarId);
    }

    //Dodaj uprawnienie
    public void addPrivilege(Role role){
        roleRepository.save(role);
    }

    //Usuń uprawnienie
    public void deletePrivilege(Long id){
        roleRepository.deleteById(id);
    }

}
