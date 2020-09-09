package paw.project.calendarapp.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "calendar_users")
public class CalendarUser {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer calendarId;
    private Integer userId;

}
