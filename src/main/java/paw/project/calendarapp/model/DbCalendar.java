package paw.project.calendarapp.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "calendars")
public class DbCalendar {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer ownerId;

}
