package paw.project.calendarapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import paw.project.calendarapp.model.ReminderCheck;
import paw.project.calendarapp.repository.ReminderCheckRepository;

@Service
public class ReminderCheckService {

    private ReminderCheckRepository reminderCheckRepository;

    @Autowired
    public ReminderCheckService(ReminderCheckRepository reminderCheckRepository){
        this.reminderCheckRepository = reminderCheckRepository;
    }

    //Zwróć sprawdzenie dla danego użytkownika i danej notki
    public ReminderCheck getReminderCheckByUserIdAndNoteId(int userId, int noteId){
        return reminderCheckRepository.findByUserIdAndNoteId(userId,noteId);
    }

}
