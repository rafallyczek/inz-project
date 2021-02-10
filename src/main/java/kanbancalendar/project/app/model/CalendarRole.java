package kanbancalendar.project.app.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "calendar_roles")
public class CalendarRole {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long calendarId;
    private String name;

}
