package paw.project.calendarapp.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "Notes")
public class Note {

    //Pola
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Integer userId;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private Integer calendarId;

    @Transient
    private LocalDate date;
    @Transient
    private String time;

    //Ustaw datę
    public void setNoteDate(){
        this.date = dateTime.toLocalDate();
    }

    //Ustaw czas
    public void setNoteTime(){
        this.time = dateTime.toLocalTime().toString();
    }

}
