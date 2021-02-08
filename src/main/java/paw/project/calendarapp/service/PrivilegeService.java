package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.Privilege;
import paw.project.calendarapp.repository.PrivilegeRepository;

import java.util.List;

@Service
public class PrivilegeService {

    private PrivilegeRepository privilegeRepository;

    @Autowired
    public PrivilegeService(PrivilegeRepository privilegeRepository){
        this.privilegeRepository = privilegeRepository;
    }

    //Zwróć uprawnienia użytkownika
    public List<Privilege> getUserPrivileges(Long id){
        return privilegeRepository.findAllByUserId(id);
    }

    //Zwróć uprawnienie dla danego kalendarza
    public Privilege getUserCalendarPrivilege(Long userId, Long calendarId){
        return privilegeRepository.findByUserIdAndCalendarId(userId, calendarId);
    }

    //Dodaj uprawnienie
    public void addPrivilege(Privilege privilege){
        privilegeRepository.save(privilege);
    }

    //Usuń uprawnienie
    public void deletePrivilege(Long id){
        privilegeRepository.deleteById(id);
    }

}
