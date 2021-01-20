package paw.project.calendarapp.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "reminders")
public class ReminderCheck {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer noteId;
    private Integer userId;
    private Integer reminderTime;
    private boolean isReminded;

}
