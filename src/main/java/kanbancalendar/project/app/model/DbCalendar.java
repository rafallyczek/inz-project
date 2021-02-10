package kanbancalendar.project.app.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
@Table(name = "calendars")
public class DbCalendar {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Tytuł nie może być pusty.")
    private String title;
    private Integer ownerId;

}
