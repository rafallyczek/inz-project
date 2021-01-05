package paw.project.calendarapp.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    public void setNoteDate(String localTimezone, String ownerTimezone){
        ZonedDateTime ownerDateTime = this.dateTime.atZone(ZoneId.of(ownerTimezone));
        ZonedDateTime localDateTime = ownerDateTime.withZoneSameInstant(ZoneId.of(localTimezone));
        this.date = localDateTime.toLocalDate();
    }

    //Ustaw czas
    public void setNoteTime(String localTimezone, String ownerTimezone){
        ZonedDateTime ownerDateTime = this.dateTime.atZone(ZoneId.of(ownerTimezone));
        ZonedDateTime localDateTime = ownerDateTime.withZoneSameInstant(ZoneId.of(localTimezone));
        this.time = localDateTime.toLocalTime().toString();
    }

}
